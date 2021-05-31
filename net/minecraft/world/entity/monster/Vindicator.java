package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import java.util.Random;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import com.google.common.collect.Maps;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.Difficulty;
import java.util.function.Predicate;

public class Vindicator extends AbstractIllager {
    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE;
    private boolean isJohnny;
    
    public Vindicator(final EntityType<? extends Vindicator> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new VindicatorBreakDoorGoal(this));
        this.goalSelector.addGoal(2, new RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(3, new HoldGroundAttackGoal(this, 10.0f));
        this.goalSelector.addGoal(4, new VindicatorMeleeAttackGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] { Raider.class }).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new VindicatorJohnnyAttackGoal(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }
    
    @Override
    protected void customServerAiStep() {
        if (!this.isNoAi()) {
            if (((ServerLevel)this.level).isRaided(new BlockPos(this))) {
                ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
            }
            else {
                ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(false);
            }
        }
        super.customServerAiStep();
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.isJohnny) {
            id.putBoolean("Johnny", true);
        }
    }
    
    @Override
    public IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        }
        if (this.isCelebrating()) {
            return IllagerArmPose.CELEBRATING;
        }
        return IllagerArmPose.CROSSED;
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("Johnny", 99)) {
            this.isJohnny = id.getBoolean("Johnny");
        }
    }
    
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        final SpawnGroupData ajj2 = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.populateDefaultEquipmentSlots(ahh);
        this.populateDefaultEquipmentEnchantments(ahh);
        return ajj2;
    }
    
    @Override
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        }
    }
    
    public boolean isAlliedTo(final Entity aio) {
        return super.isAlliedTo(aio) || (aio instanceof LivingEntity && ((LivingEntity)aio).getMobType() == MobType.ILLAGER && this.getTeam() == null && aio.getTeam() == null);
    }
    
    public void setCustomName(@Nullable final Component jo) {
        super.setCustomName(jo);
        if (!this.isJohnny && jo != null && jo.getString().equals("Johnny")) {
            this.isJohnny = true;
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.VINDICATOR_HURT;
    }
    
    @Override
    public void applyRaidBuffs(final int integer, final boolean boolean2) {
        final ItemStack bcj4 = new ItemStack(Items.IRON_AXE);
        final Raid axk5 = this.getCurrentRaid();
        int integer2 = 1;
        if (integer > axk5.getNumGroups(Difficulty.NORMAL)) {
            integer2 = 2;
        }
        final boolean boolean3 = this.random.nextFloat() <= axk5.getEnchantOdds();
        if (boolean3) {
            final Map<Enchantment, Integer> map8 = (Map<Enchantment, Integer>)Maps.newHashMap();
            map8.put(Enchantments.SHARPNESS, integer2);
            EnchantmentHelper.setEnchantments(map8, bcj4);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, bcj4);
    }
    
    static {
        DOOR_BREAKING_PREDICATE = (ahg -> ahg == Difficulty.NORMAL || ahg == Difficulty.HARD);
    }
    
    class VindicatorMeleeAttackGoal extends MeleeAttackGoal {
        public VindicatorMeleeAttackGoal(final Vindicator avj2) {
            super(avj2, 1.0, false);
        }
        
        @Override
        protected double getAttackReachSqr(final LivingEntity aix) {
            if (this.mob.getVehicle() instanceof Ravager) {
                final float float3 = this.mob.getVehicle().getBbWidth() - 0.1f;
                return float3 * 2.0f * (float3 * 2.0f) + aix.getBbWidth();
            }
            return super.getAttackReachSqr(aix);
        }
    }
    
    static class VindicatorBreakDoorGoal extends BreakDoorGoal {
        public VindicatorBreakDoorGoal(final Mob aiy) {
            super(aiy, 6, Vindicator.DOOR_BREAKING_PREDICATE);
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canContinueToUse() {
            final Vindicator avj2 = (Vindicator)this.mob;
            return avj2.hasActiveRaid() && super.canContinueToUse();
        }
        
        @Override
        public boolean canUse() {
            final Vindicator avj2 = (Vindicator)this.mob;
            return avj2.hasActiveRaid() && avj2.random.nextInt(10) == 0 && super.canUse();
        }
        
        @Override
        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }
    
    static class VindicatorJohnnyAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
        public VindicatorJohnnyAttackGoal(final Vindicator avj) {
            super(avj, LivingEntity.class, 0, true, true, (Predicate<LivingEntity>)LivingEntity::attackable);
        }
        
        @Override
        public boolean canUse() {
            return ((Vindicator)this.mob).isJohnny && super.canUse();
        }
        
        @Override
        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }
}
