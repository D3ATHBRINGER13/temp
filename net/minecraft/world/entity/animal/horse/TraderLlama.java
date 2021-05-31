package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class TraderLlama extends Llama {
    private int despawnDelay;
    
    public TraderLlama(final EntityType<? extends TraderLlama> ais, final Level bhr) {
        super(ais, bhr);
        this.despawnDelay = 47999;
    }
    
    @Override
    public boolean isTraderLlama() {
        return true;
    }
    
    @Override
    protected Llama makeBabyLlama() {
        return EntityType.TRADER_LLAMA.create(this.level);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("DespawnDelay", this.despawnDelay);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("DespawnDelay", 99)) {
            this.despawnDelay = id.getInt("DespawnDelay");
        }
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.targetSelector.addGoal(1, new TraderLlamaDefendWanderingTraderGoal(this));
    }
    
    @Override
    protected void doPlayerRide(final Player awg) {
        final Entity aio3 = this.getLeashHolder();
        if (aio3 instanceof WanderingTrader) {
            return;
        }
        super.doPlayerRide(awg);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.maybeDespawn();
        }
    }
    
    private void maybeDespawn() {
        if (!this.canDespawn()) {
            return;
        }
        this.despawnDelay = (this.isLeashedToWanderingTrader() ? (((WanderingTrader)this.getLeashHolder()).getDespawnDelay() - 1) : (this.despawnDelay - 1));
        if (this.despawnDelay <= 0) {
            this.dropLeash(true, false);
            this.remove();
        }
    }
    
    private boolean canDespawn() {
        return !this.isTamed() && !this.isLeashedToSomethingOtherThanTheWanderingTrader() && !this.hasOnePlayerPassenger();
    }
    
    private boolean isLeashedToWanderingTrader() {
        return this.getLeashHolder() instanceof WanderingTrader;
    }
    
    private boolean isLeashedToSomethingOtherThanTheWanderingTrader() {
        return this.isLeashed() && !this.isLeashedToWanderingTrader();
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        final SpawnGroupData ajj2 = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        if (aja == MobSpawnType.EVENT) {
            this.setAge(0);
        }
        return ajj2;
    }
    
    public class TraderLlamaDefendWanderingTraderGoal extends TargetGoal {
        private final Llama llama;
        private LivingEntity ownerLastHurtBy;
        private int timestamp;
        
        public TraderLlamaDefendWanderingTraderGoal(final Llama ase) {
            super(ase, false);
            this.llama = ase;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.TARGET));
        }
        
        @Override
        public boolean canUse() {
            if (!this.llama.isLeashed()) {
                return false;
            }
            final Entity aio2 = this.llama.getLeashHolder();
            if (!(aio2 instanceof WanderingTrader)) {
                return false;
            }
            final WanderingTrader avz3 = (WanderingTrader)aio2;
            this.ownerLastHurtBy = avz3.getLastHurtByMob();
            final int integer4 = avz3.getLastHurtByMobTimestamp();
            return integer4 != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
        }
        
        @Override
        public void start() {
            this.mob.setTarget(this.ownerLastHurtBy);
            final Entity aio2 = this.llama.getLeashHolder();
            if (aio2 instanceof WanderingTrader) {
                this.timestamp = ((WanderingTrader)aio2).getLastHurtByMobTimestamp();
            }
            super.start();
        }
    }
}
