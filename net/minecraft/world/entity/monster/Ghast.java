package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.nbt.CompoundTag;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.FlyingMob;

public class Ghast extends FlyingMob implements Enemy {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING;
    private int explosionPower;
    
    public Ghast(final EntityType<? extends Ghast> ais, final Level bhr) {
        super(ais, bhr);
        this.explosionPower = 1;
        this.xpReward = 5;
        this.moveControl = new GhastMoveControl(this);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new GhastLookGoal(this));
        this.goalSelector.addGoal(7, new GhastShootFireballGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)(aix -> Math.abs(aix.y - this.y) <= 4.0)));
    }
    
    public boolean isCharging() {
        return this.entityData.<Boolean>get(Ghast.DATA_IS_CHARGING);
    }
    
    public void setCharging(final boolean boolean1) {
        this.entityData.<Boolean>set(Ghast.DATA_IS_CHARGING, boolean1);
    }
    
    public int getExplosionPower() {
        return this.explosionPower;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }
    
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (ahx.getDirectEntity() instanceof LargeFireball && ahx.getEntity() instanceof Player) {
            super.hurt(ahx, 1000.0f);
            return true;
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(Ghast.DATA_IS_CHARGING, false);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0);
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.GHAST_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }
    
    protected float getSoundVolume() {
        return 10.0f;
    }
    
    public static boolean checkGhastSpawnRules(final EntityType<Ghast> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(20) == 0 && Mob.checkMobSpawnRules(ais, bhs, aja, ew, random);
    }
    
    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("ExplosionPower", this.explosionPower);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("ExplosionPower", 99)) {
            this.explosionPower = id.getInt("ExplosionPower");
        }
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 2.6f;
    }
    
    static {
        DATA_IS_CHARGING = SynchedEntityData.<Boolean>defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
    }
    
    static class GhastMoveControl extends MoveControl {
        private final Ghast ghast;
        private int floatDuration;
        
        public GhastMoveControl(final Ghast aum) {
            super(aum);
            this.ghast = aum;
        }
        
        @Override
        public void tick() {
            if (this.operation != Operation.MOVE_TO) {
                return;
            }
            if (this.floatDuration-- <= 0) {
                this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                Vec3 csi2 = new Vec3(this.wantedX - this.ghast.x, this.wantedY - this.ghast.y, this.wantedZ - this.ghast.z);
                final double double3 = csi2.length();
                csi2 = csi2.normalize();
                if (this.canReach(csi2, Mth.ceil(double3))) {
                    this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(csi2.scale(0.1)));
                }
                else {
                    this.operation = Operation.WAIT;
                }
            }
        }
        
        private boolean canReach(final Vec3 csi, final int integer) {
            AABB csc4 = this.ghast.getBoundingBox();
            for (int integer2 = 1; integer2 < integer; ++integer2) {
                csc4 = csc4.move(csi);
                if (!this.ghast.level.noCollision(this.ghast, csc4)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    static class RandomFloatAroundGoal extends Goal {
        private final Ghast ghast;
        
        public RandomFloatAroundGoal(final Ghast aum) {
            this.ghast = aum;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        @Override
        public boolean canUse() {
            final MoveControl amj2 = this.ghast.getMoveControl();
            if (!amj2.hasWanted()) {
                return true;
            }
            final double double3 = amj2.getWantedX() - this.ghast.x;
            final double double4 = amj2.getWantedY() - this.ghast.y;
            final double double5 = amj2.getWantedZ() - this.ghast.z;
            final double double6 = double3 * double3 + double4 * double4 + double5 * double5;
            return double6 < 1.0 || double6 > 3600.0;
        }
        
        @Override
        public boolean canContinueToUse() {
            return false;
        }
        
        @Override
        public void start() {
            final Random random2 = this.ghast.getRandom();
            final double double3 = this.ghast.x + (random2.nextFloat() * 2.0f - 1.0f) * 16.0f;
            final double double4 = this.ghast.y + (random2.nextFloat() * 2.0f - 1.0f) * 16.0f;
            final double double5 = this.ghast.z + (random2.nextFloat() * 2.0f - 1.0f) * 16.0f;
            this.ghast.getMoveControl().setWantedPosition(double3, double4, double5, 1.0);
        }
    }
    
    static class GhastLookGoal extends Goal {
        private final Ghast ghast;
        
        public GhastLookGoal(final Ghast aum) {
            this.ghast = aum;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            return true;
        }
        
        @Override
        public void tick() {
            if (this.ghast.getTarget() == null) {
                final Vec3 csi2 = this.ghast.getDeltaMovement();
                this.ghast.yRot = -(float)Mth.atan2(csi2.x, csi2.z) * 57.295776f;
                this.ghast.yBodyRot = this.ghast.yRot;
            }
            else {
                final LivingEntity aix2 = this.ghast.getTarget();
                final double double3 = 64.0;
                if (aix2.distanceToSqr(this.ghast) < 4096.0) {
                    final double double4 = aix2.x - this.ghast.x;
                    final double double5 = aix2.z - this.ghast.z;
                    this.ghast.yRot = -(float)Mth.atan2(double4, double5) * 57.295776f;
                    this.ghast.yBodyRot = this.ghast.yRot;
                }
            }
        }
    }
    
    static class GhastShootFireballGoal extends Goal {
        private final Ghast ghast;
        public int chargeTime;
        
        public GhastShootFireballGoal(final Ghast aum) {
            this.ghast = aum;
        }
        
        @Override
        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }
        
        @Override
        public void start() {
            this.chargeTime = 0;
        }
        
        @Override
        public void stop() {
            this.ghast.setCharging(false);
        }
        
        @Override
        public void tick() {
            final LivingEntity aix2 = this.ghast.getTarget();
            final double double3 = 64.0;
            if (aix2.distanceToSqr(this.ghast) < 4096.0 && this.ghast.canSee(aix2)) {
                final Level bhr5 = this.ghast.level;
                ++this.chargeTime;
                if (this.chargeTime == 10) {
                    bhr5.levelEvent(null, 1015, new BlockPos(this.ghast), 0);
                }
                if (this.chargeTime == 20) {
                    final double double4 = 4.0;
                    final Vec3 csi8 = this.ghast.getViewVector(1.0f);
                    final double double5 = aix2.x - (this.ghast.x + csi8.x * 4.0);
                    final double double6 = aix2.getBoundingBox().minY + aix2.getBbHeight() / 2.0f - (0.5 + this.ghast.y + this.ghast.getBbHeight() / 2.0f);
                    final double double7 = aix2.z - (this.ghast.z + csi8.z * 4.0);
                    bhr5.levelEvent(null, 1016, new BlockPos(this.ghast), 0);
                    final LargeFireball awt15 = new LargeFireball(bhr5, this.ghast, double5, double6, double7);
                    awt15.explosionPower = this.ghast.getExplosionPower();
                    awt15.x = this.ghast.x + csi8.x * 4.0;
                    awt15.y = this.ghast.y + this.ghast.getBbHeight() / 2.0f + 0.5;
                    awt15.z = this.ghast.z + csi8.z * 4.0;
                    bhr5.addFreshEntity(awt15);
                    this.chargeTime = -40;
                }
            }
            else if (this.chargeTime > 0) {
                --this.chargeTime;
            }
            this.ghast.setCharging(this.chargeTime > 10);
        }
    }
}
