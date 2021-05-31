package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.Difficulty;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;

public class PigZombie extends Zombie {
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID;
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING;
    private int angerTime;
    private int playAngrySoundIn;
    private UUID lastHurtByUUID;
    
    public PigZombie(final EntityType<? extends PigZombie> ais, final Level bhr) {
        super(ais, bhr);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0f);
    }
    
    public void setLastHurtByMob(@Nullable final LivingEntity aix) {
        super.setLastHurtByMob(aix);
        if (aix != null) {
            this.lastHurtByUUID = aix.getUUID();
        }
    }
    
    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new PigZombieHurtByOtherGoal(this));
        this.targetSelector.addGoal(2, new PigZombieAngerTargetGoal(this));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(PigZombie.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }
    
    @Override
    protected boolean convertsInWater() {
        return false;
    }
    
    @Override
    protected void customServerAiStep() {
        final AttributeInstance ajo2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        final LivingEntity aix3 = this.getLastHurtByMob();
        if (this.isAngry()) {
            if (!this.isBaby() && !ajo2.hasModifier(PigZombie.SPEED_MODIFIER_ATTACKING)) {
                ajo2.addModifier(PigZombie.SPEED_MODIFIER_ATTACKING);
            }
            --this.angerTime;
            final LivingEntity aix4 = (aix3 != null) ? aix3 : this.getTarget();
            if (!this.isAngry() && aix4 != null) {
                if (!this.canSee(aix4)) {
                    this.setLastHurtByMob(null);
                    this.setTarget(null);
                }
                else {
                    this.angerTime = this.getAngerTime();
                }
            }
        }
        else if (ajo2.hasModifier(PigZombie.SPEED_MODIFIER_ATTACKING)) {
            ajo2.removeModifier(PigZombie.SPEED_MODIFIER_ATTACKING);
        }
        if (this.playAngrySoundIn > 0 && --this.playAngrySoundIn == 0) {
            this.playSound(SoundEvents.ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 1.8f);
        }
        if (this.isAngry() && this.lastHurtByUUID != null && aix3 == null) {
            final Player awg4 = this.level.getPlayerByUUID(this.lastHurtByUUID);
            this.setLastHurtByMob(awg4);
            this.lastHurtByPlayer = awg4;
            this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
        }
        super.customServerAiStep();
    }
    
    public static boolean checkPigZombieSpawnRules(final EntityType<PigZombie> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        return bhu.isUnobstructed(this) && !bhu.containsAnyLiquid(this.getBoundingBox());
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putShort("Anger", (short)this.angerTime);
        if (this.lastHurtByUUID != null) {
            id.putString("HurtBy", this.lastHurtByUUID.toString());
        }
        else {
            id.putString("HurtBy", "");
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.angerTime = id.getShort("Anger");
        final String string3 = id.getString("HurtBy");
        if (!string3.isEmpty()) {
            this.lastHurtByUUID = UUID.fromString(string3);
            final Player awg4 = this.level.getPlayerByUUID(this.lastHurtByUUID);
            this.setLastHurtByMob(awg4);
            if (awg4 != null) {
                this.lastHurtByPlayer = awg4;
                this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
            }
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        final Entity aio4 = ahx.getEntity();
        if (aio4 instanceof Player && !((Player)aio4).isCreative() && this.canSee(aio4)) {
            this.makeAngry(aio4);
        }
        return super.hurt(ahx, float2);
    }
    
    private boolean makeAngry(final Entity aio) {
        this.angerTime = this.getAngerTime();
        this.playAngrySoundIn = this.random.nextInt(40);
        if (aio instanceof LivingEntity) {
            this.setLastHurtByMob((LivingEntity)aio);
        }
        return true;
    }
    
    private int getAngerTime() {
        return 400 + this.random.nextInt(400);
    }
    
    private boolean isAngry() {
        return this.angerTime > 0;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_PIGMAN_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.ZOMBIE_PIGMAN_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_PIGMAN_DEATH;
    }
    
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        return false;
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean isPreventingPlayerRest(final Player awg) {
        return this.isAngry();
    }
    
    static {
        SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
        SPEED_MODIFIER_ATTACKING = new AttributeModifier(PigZombie.SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05, AttributeModifier.Operation.ADDITION).setSerialize(false);
    }
    
    static class PigZombieHurtByOtherGoal extends HurtByTargetGoal {
        public PigZombieHurtByOtherGoal(final PigZombie auv) {
            super(auv, new Class[0]);
            this.setAlertOthers(Zombie.class);
        }
        
        @Override
        protected void alertOther(final Mob aiy, final LivingEntity aix) {
            if (aiy instanceof PigZombie && this.mob.canSee(aix) && ((PigZombie)aiy).makeAngry(aix)) {
                aiy.setTarget(aix);
            }
        }
    }
    
    static class PigZombieAngerTargetGoal extends NearestAttackableTargetGoal<Player> {
        public PigZombieAngerTargetGoal(final PigZombie auv) {
            super(auv, Player.class, true);
        }
        
        @Override
        public boolean canUse() {
            return ((PigZombie)this.mob).isAngry() && super.canUse();
        }
    }
}
