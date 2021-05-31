package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.Difficulty;
import java.util.Random;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Guardian extends Monster {
    private static final EntityDataAccessor<Boolean> DATA_ID_MOVING;
    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET;
    protected float clientSideTailAnimation;
    protected float clientSideTailAnimationO;
    protected float clientSideTailAnimationSpeed;
    protected float clientSideSpikesAnimation;
    protected float clientSideSpikesAnimationO;
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;
    private boolean clientSideTouchedGround;
    protected RandomStrollGoal randomStrollGoal;
    
    public Guardian(final EntityType<? extends Guardian> ais, final Level bhr) {
        super(ais, bhr);
        this.xpReward = 10;
        this.moveControl = new GuardianMoveControl(this);
        this.clientSideTailAnimation = this.random.nextFloat();
        this.clientSideTailAnimationO = this.clientSideTailAnimation;
    }
    
    @Override
    protected void registerGoals() {
        final MoveTowardsRestrictionGoal ans2 = new MoveTowardsRestrictionGoal(this, 1.0);
        this.randomStrollGoal = new RandomStrollGoal(this, 1.0, 80);
        this.goalSelector.addGoal(4, new GuardianAttackGoal(this));
        this.goalSelector.addGoal(5, ans2);
        this.goalSelector.addGoal(7, this.randomStrollGoal);
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Guardian.class, 12.0f, 0.01f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.randomStrollGoal.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
        ans2.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, (Predicate<LivingEntity>)new GuardianAttackSelector(this)));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
    }
    
    @Override
    protected PathNavigation createNavigation(final Level bhr) {
        return new WaterBoundPathNavigation(this, bhr);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(Guardian.DATA_ID_MOVING, false);
        this.entityData.<Integer>define(Guardian.DATA_ID_ATTACK_TARGET, 0);
    }
    
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    public MobType getMobType() {
        return MobType.WATER;
    }
    
    public boolean isMoving() {
        return this.entityData.<Boolean>get(Guardian.DATA_ID_MOVING);
    }
    
    private void setMoving(final boolean boolean1) {
        this.entityData.<Boolean>set(Guardian.DATA_ID_MOVING, boolean1);
    }
    
    public int getAttackDuration() {
        return 80;
    }
    
    private void setActiveAttackTarget(final int integer) {
        this.entityData.<Integer>set(Guardian.DATA_ID_ATTACK_TARGET, integer);
    }
    
    public boolean hasActiveAttackTarget() {
        return this.entityData.<Integer>get(Guardian.DATA_ID_ATTACK_TARGET) != 0;
    }
    
    @Nullable
    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        }
        if (!this.level.isClientSide) {
            return this.getTarget();
        }
        if (this.clientSideCachedAttackTarget != null) {
            return this.clientSideCachedAttackTarget;
        }
        final Entity aio2 = this.level.getEntity(this.entityData.<Integer>get(Guardian.DATA_ID_ATTACK_TARGET));
        if (aio2 instanceof LivingEntity) {
            return this.clientSideCachedAttackTarget = (LivingEntity)aio2;
        }
        return null;
    }
    
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        super.onSyncedDataUpdated(qk);
        if (Guardian.DATA_ID_ATTACK_TARGET.equals(qk)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWaterOrBubble() ? SoundEvents.GUARDIAN_AMBIENT : SoundEvents.GUARDIAN_AMBIENT_LAND;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return this.isInWaterOrBubble() ? SoundEvents.GUARDIAN_HURT : SoundEvents.GUARDIAN_HURT_LAND;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return this.isInWaterOrBubble() ? SoundEvents.GUARDIAN_DEATH : SoundEvents.GUARDIAN_DEATH_LAND;
    }
    
    protected boolean makeStepSound() {
        return false;
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.5f;
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        if (bhu.getFluidState(ew).is(FluidTags.WATER)) {
            return 10.0f + bhu.getBrightness(ew) - 0.5f;
        }
        return super.getWalkTargetValue(ew, bhu);
    }
    
    @Override
    public void aiStep() {
        if (this.isAlive()) {
            if (this.level.isClientSide) {
                this.clientSideTailAnimationO = this.clientSideTailAnimation;
                if (!this.isInWater()) {
                    this.clientSideTailAnimationSpeed = 2.0f;
                    final Vec3 csi2 = this.getDeltaMovement();
                    if (csi2.y > 0.0 && this.clientSideTouchedGround && !this.isSilent()) {
                        this.level.playLocalSound(this.x, this.y, this.z, this.getFlopSound(), this.getSoundSource(), 1.0f, 1.0f, false);
                    }
                    this.clientSideTouchedGround = (csi2.y < 0.0 && this.level.loadedAndEntityCanStandOn(new BlockPos(this).below(), this));
                }
                else if (this.isMoving()) {
                    if (this.clientSideTailAnimationSpeed < 0.5f) {
                        this.clientSideTailAnimationSpeed = 4.0f;
                    }
                    else {
                        this.clientSideTailAnimationSpeed += (0.5f - this.clientSideTailAnimationSpeed) * 0.1f;
                    }
                }
                else {
                    this.clientSideTailAnimationSpeed += (0.125f - this.clientSideTailAnimationSpeed) * 0.2f;
                }
                this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
                this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
                if (!this.isInWaterOrBubble()) {
                    this.clientSideSpikesAnimation = this.random.nextFloat();
                }
                else if (this.isMoving()) {
                    this.clientSideSpikesAnimation += (0.0f - this.clientSideSpikesAnimation) * 0.25f;
                }
                else {
                    this.clientSideSpikesAnimation += (1.0f - this.clientSideSpikesAnimation) * 0.06f;
                }
                if (this.isMoving() && this.isInWater()) {
                    final Vec3 csi2 = this.getViewVector(0.0f);
                    for (int integer3 = 0; integer3 < 2; ++integer3) {
                        this.level.addParticle(ParticleTypes.BUBBLE, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth() - csi2.x * 1.5, this.y + this.random.nextDouble() * this.getBbHeight() - csi2.y * 1.5, this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth() - csi2.z * 1.5, 0.0, 0.0, 0.0);
                    }
                }
                if (this.hasActiveAttackTarget()) {
                    if (this.clientSideAttackTime < this.getAttackDuration()) {
                        ++this.clientSideAttackTime;
                    }
                    final LivingEntity aix2 = this.getActiveAttackTarget();
                    if (aix2 != null) {
                        this.getLookControl().setLookAt(aix2, 90.0f, 90.0f);
                        this.getLookControl().tick();
                        final double double3 = this.getAttackAnimationScale(0.0f);
                        double double4 = aix2.x - this.x;
                        double double5 = aix2.y + aix2.getBbHeight() * 0.5f - (this.y + this.getEyeHeight());
                        double double6 = aix2.z - this.z;
                        final double double7 = Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
                        double4 /= double7;
                        double5 /= double7;
                        double6 /= double7;
                        double double8 = this.random.nextDouble();
                        while (double8 < double7) {
                            double8 += 1.8 - double3 + this.random.nextDouble() * (1.7 - double3);
                            this.level.addParticle(ParticleTypes.BUBBLE, this.x + double4 * double8, this.y + double5 * double8 + this.getEyeHeight(), this.z + double6 * double8, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            }
            else if (this.onGround) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.4f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f));
                this.yRot = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.hasImpulse = true;
            }
            if (this.hasActiveAttackTarget()) {
                this.yRot = this.yHeadRot;
            }
        }
        super.aiStep();
    }
    
    protected SoundEvent getFlopSound() {
        return SoundEvents.GUARDIAN_FLOP;
    }
    
    public float getTailAnimation(final float float1) {
        return Mth.lerp(float1, this.clientSideTailAnimationO, this.clientSideTailAnimation);
    }
    
    public float getSpikesAnimation(final float float1) {
        return Mth.lerp(float1, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
    }
    
    public float getAttackAnimationScale(final float float1) {
        return (this.clientSideAttackTime + float1) / this.getAttackDuration();
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        return bhu.isUnobstructed(this);
    }
    
    public static boolean checkGuardianSpawnRules(final EntityType<? extends Guardian> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return (random.nextInt(20) == 0 || !bhs.canSeeSkyFromBelowWater(ew)) && bhs.getDifficulty() != Difficulty.PEACEFUL && (aja == MobSpawnType.SPAWNER || bhs.getFluidState(ew).is(FluidTags.WATER));
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (!this.isMoving() && !ahx.isMagic() && ahx.getDirectEntity() instanceof LivingEntity) {
            final LivingEntity aix4 = (LivingEntity)ahx.getDirectEntity();
            if (!ahx.isExplosion()) {
                aix4.hurt(DamageSource.thorns(this), 2.0f);
            }
        }
        if (this.randomStrollGoal != null) {
            this.randomStrollGoal.trigger();
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    public int getMaxHeadXRot() {
        return 180;
    }
    
    public void travel(final Vec3 csi) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1f, csi);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (!this.isMoving() && this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        }
        else {
            super.travel(csi);
        }
    }
    
    static {
        DATA_ID_MOVING = SynchedEntityData.<Boolean>defineId(Guardian.class, EntityDataSerializers.BOOLEAN);
        DATA_ID_ATTACK_TARGET = SynchedEntityData.<Integer>defineId(Guardian.class, EntityDataSerializers.INT);
    }
    
    static class GuardianAttackSelector implements Predicate<LivingEntity> {
        private final Guardian guardian;
        
        public GuardianAttackSelector(final Guardian auo) {
            this.guardian = auo;
        }
        
        public boolean test(@Nullable final LivingEntity aix) {
            return (aix instanceof Player || aix instanceof Squid) && aix.distanceToSqr(this.guardian) > 9.0;
        }
    }
    
    static class GuardianAttackGoal extends Goal {
        private final Guardian guardian;
        private int attackTime;
        private final boolean elder;
        
        public GuardianAttackGoal(final Guardian auo) {
            this.guardian = auo;
            this.elder = (auo instanceof ElderGuardian);
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = this.guardian.getTarget();
            return aix2 != null && aix2.isAlive();
        }
        
        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && (this.elder || this.guardian.distanceToSqr(this.guardian.getTarget()) > 9.0);
        }
        
        @Override
        public void start() {
            this.attackTime = -10;
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(this.guardian.getTarget(), 90.0f, 90.0f);
            this.guardian.hasImpulse = true;
        }
        
        @Override
        public void stop() {
            this.guardian.setActiveAttackTarget(0);
            this.guardian.setTarget(null);
            this.guardian.randomStrollGoal.trigger();
        }
        
        @Override
        public void tick() {
            final LivingEntity aix2 = this.guardian.getTarget();
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(aix2, 90.0f, 90.0f);
            if (!this.guardian.canSee(aix2)) {
                this.guardian.setTarget(null);
                return;
            }
            ++this.attackTime;
            if (this.attackTime == 0) {
                this.guardian.setActiveAttackTarget(this.guardian.getTarget().getId());
                this.guardian.level.broadcastEntityEvent(this.guardian, (byte)21);
            }
            else if (this.attackTime >= this.guardian.getAttackDuration()) {
                float float3 = 1.0f;
                if (this.guardian.level.getDifficulty() == Difficulty.HARD) {
                    float3 += 2.0f;
                }
                if (this.elder) {
                    float3 += 2.0f;
                }
                aix2.hurt(DamageSource.indirectMagic(this.guardian, this.guardian), float3);
                aix2.hurt(DamageSource.mobAttack(this.guardian), (float)this.guardian.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
                this.guardian.setTarget(null);
            }
            super.tick();
        }
    }
    
    static class GuardianMoveControl extends MoveControl {
        private final Guardian guardian;
        
        public GuardianMoveControl(final Guardian auo) {
            super(auo);
            this.guardian = auo;
        }
        
        @Override
        public void tick() {
            if (this.operation != Operation.MOVE_TO || this.guardian.getNavigation().isDone()) {
                this.guardian.setSpeed(0.0f);
                this.guardian.setMoving(false);
                return;
            }
            final Vec3 csi2 = new Vec3(this.wantedX - this.guardian.x, this.wantedY - this.guardian.y, this.wantedZ - this.guardian.z);
            final double double3 = csi2.length();
            final double double4 = csi2.x / double3;
            final double double5 = csi2.y / double3;
            final double double6 = csi2.z / double3;
            final float float11 = (float)(Mth.atan2(csi2.z, csi2.x) * 57.2957763671875) - 90.0f;
            this.guardian.yRot = this.rotlerp(this.guardian.yRot, float11, 90.0f);
            this.guardian.yBodyRot = this.guardian.yRot;
            final float float12 = (float)(this.speedModifier * this.guardian.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            final float float13 = Mth.lerp(0.125f, this.guardian.getSpeed(), float12);
            this.guardian.setSpeed(float13);
            final double double7 = Math.sin((this.guardian.tickCount + this.guardian.getId()) * 0.5) * 0.05;
            final double double8 = Math.cos((double)(this.guardian.yRot * 0.017453292f));
            final double double9 = Math.sin((double)(this.guardian.yRot * 0.017453292f));
            final double double10 = Math.sin((this.guardian.tickCount + this.guardian.getId()) * 0.75) * 0.05;
            this.guardian.setDeltaMovement(this.guardian.getDeltaMovement().add(double7 * double8, double10 * (double9 + double8) * 0.25 + float13 * double5 * 0.1, double7 * double9));
            final LookControl ami22 = this.guardian.getLookControl();
            final double double11 = this.guardian.x + double4 * 2.0;
            final double double12 = this.guardian.getEyeHeight() + this.guardian.y + double5 / double3;
            final double double13 = this.guardian.z + double6 * 2.0;
            double double14 = ami22.getWantedX();
            double double15 = ami22.getWantedY();
            double double16 = ami22.getWantedZ();
            if (!ami22.isHasWanted()) {
                double14 = double11;
                double15 = double12;
                double16 = double13;
            }
            this.guardian.getLookControl().setLookAt(Mth.lerp(0.125, double14, double11), Mth.lerp(0.125, double15, double12), Mth.lerp(0.125, double16, double13), 10.0f, 40.0f);
            this.guardian.setMoving(true);
        }
    }
}
