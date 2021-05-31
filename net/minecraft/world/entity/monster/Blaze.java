package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Blaze extends Monster {
    private float allowedHeightOffset;
    private int nextHeightOffsetChangeTick;
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    
    public Blaze(final EntityType<? extends Blaze> ais, final Level bhr) {
        super(ais, bhr);
        this.allowedHeightOffset = 0.5f;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0f);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0f);
        this.xpReward = 10;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new BlazeAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0f));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(Blaze.DATA_FLAGS_ID, (Byte)0);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.BLAZE_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }
    
    public int getLightColor() {
        return 15728880;
    }
    
    public float getBrightness() {
        return 1.0f;
    }
    
    @Override
    public void aiStep() {
        if (!this.onGround && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }
        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            for (int integer2 = 0; integer2 < 2; ++integer2) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth(), this.y + this.random.nextDouble() * this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth(), 0.0, 0.0, 0.0);
            }
        }
        super.aiStep();
    }
    
    @Override
    protected void customServerAiStep() {
        if (this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0f);
        }
        --this.nextHeightOffsetChangeTick;
        if (this.nextHeightOffsetChangeTick <= 0) {
            this.nextHeightOffsetChangeTick = 100;
            this.allowedHeightOffset = 0.5f + (float)this.random.nextGaussian() * 3.0f;
        }
        final LivingEntity aix2 = this.getTarget();
        if (aix2 != null && aix2.y + aix2.getEyeHeight() > this.y + this.getEyeHeight() + this.allowedHeightOffset && this.canAttack(aix2)) {
            final Vec3 csi3 = this.getDeltaMovement();
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, (0.30000001192092896 - csi3.y) * 0.30000001192092896, 0.0));
            this.hasImpulse = true;
        }
        super.customServerAiStep();
    }
    
    public void causeFallDamage(final float float1, final float float2) {
    }
    
    public boolean isOnFire() {
        return this.isCharged();
    }
    
    private boolean isCharged() {
        return (this.entityData.<Byte>get(Blaze.DATA_FLAGS_ID) & 0x1) != 0x0;
    }
    
    private void setCharged(final boolean boolean1) {
        byte byte3 = this.entityData.<Byte>get(Blaze.DATA_FLAGS_ID);
        if (boolean1) {
            byte3 |= 0x1;
        }
        else {
            byte3 &= 0xFFFFFFFE;
        }
        this.entityData.<Byte>set(Blaze.DATA_FLAGS_ID, byte3);
    }
    
    static {
        DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(Blaze.class, EntityDataSerializers.BYTE);
    }
    
    static class BlazeAttackGoal extends Goal {
        private final Blaze blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;
        
        public BlazeAttackGoal(final Blaze auc) {
            this.blaze = auc;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = this.blaze.getTarget();
            return aix2 != null && aix2.isAlive() && this.blaze.canAttack(aix2);
        }
        
        @Override
        public void start() {
            this.attackStep = 0;
        }
        
        @Override
        public void stop() {
            this.blaze.setCharged(false);
            this.lastSeen = 0;
        }
        
        @Override
        public void tick() {
            --this.attackTime;
            final LivingEntity aix2 = this.blaze.getTarget();
            if (aix2 == null) {
                return;
            }
            final boolean boolean3 = this.blaze.getSensing().canSee(aix2);
            if (boolean3) {
                this.lastSeen = 0;
            }
            else {
                ++this.lastSeen;
            }
            final double double4 = this.blaze.distanceToSqr(aix2);
            if (double4 < 4.0) {
                if (!boolean3) {
                    return;
                }
                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    this.blaze.doHurtTarget(aix2);
                }
                this.blaze.getMoveControl().setWantedPosition(aix2.x, aix2.y, aix2.z, 1.0);
            }
            else if (double4 < this.getFollowDistance() * this.getFollowDistance() && boolean3) {
                final double double5 = aix2.x - this.blaze.x;
                final double double6 = aix2.getBoundingBox().minY + aix2.getBbHeight() / 2.0f - (this.blaze.y + this.blaze.getBbHeight() / 2.0f);
                final double double7 = aix2.z - this.blaze.z;
                if (this.attackTime <= 0) {
                    ++this.attackStep;
                    if (this.attackStep == 1) {
                        this.attackTime = 60;
                        this.blaze.setCharged(true);
                    }
                    else if (this.attackStep <= 4) {
                        this.attackTime = 6;
                    }
                    else {
                        this.attackTime = 100;
                        this.attackStep = 0;
                        this.blaze.setCharged(false);
                    }
                    if (this.attackStep > 1) {
                        final float float12 = Mth.sqrt(Mth.sqrt(double4)) * 0.5f;
                        this.blaze.level.levelEvent(null, 1018, new BlockPos(this.blaze), 0);
                        for (int integer13 = 0; integer13 < 1; ++integer13) {
                            final SmallFireball awy14 = new SmallFireball(this.blaze.level, this.blaze, double5 + this.blaze.getRandom().nextGaussian() * float12, double6, double7 + this.blaze.getRandom().nextGaussian() * float12);
                            awy14.y = this.blaze.y + this.blaze.getBbHeight() / 2.0f + 0.5;
                            this.blaze.level.addFreshEntity(awy14);
                        }
                    }
                }
                this.blaze.getLookControl().setLookAt(aix2, 10.0f, 10.0f);
            }
            else if (this.lastSeen < 5) {
                this.blaze.getMoveControl().setWantedPosition(aix2.x, aix2.y, aix2.z, 1.0);
            }
            super.tick();
        }
        
        private double getFollowDistance() {
            return this.blaze.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getValue();
        }
    }
}
