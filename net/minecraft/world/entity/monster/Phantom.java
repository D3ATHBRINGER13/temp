package net.minecraft.world.entity.monster;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Vec3i;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Random;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.Difficulty;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.FlyingMob;

public class Phantom extends FlyingMob implements Enemy {
    private static final EntityDataAccessor<Integer> ID_SIZE;
    private Vec3 moveTargetPoint;
    private BlockPos anchorPoint;
    private AttackPhase attackPhase;
    
    public Phantom(final EntityType<? extends Phantom> ais, final Level bhr) {
        super(ais, bhr);
        this.moveTargetPoint = Vec3.ZERO;
        this.anchorPoint = BlockPos.ZERO;
        this.attackPhase = AttackPhase.CIRCLE;
        this.xpReward = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }
    
    @Override
    protected BodyRotationControl createBodyControl() {
        return new PhantomBodyRotationControl(this);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new PhantomCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new PhantomAttackPlayerTargetGoal());
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Integer>define(Phantom.ID_SIZE, 0);
    }
    
    public void setPhantomSize(final int integer) {
        this.entityData.<Integer>set(Phantom.ID_SIZE, Mth.clamp(integer, 0, 64));
    }
    
    private void updatePhantomSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }
    
    public int getPhantomSize() {
        return this.entityData.<Integer>get(Phantom.ID_SIZE);
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.35f;
    }
    
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (Phantom.ID_SIZE.equals(qk)) {
            this.updatePhantomSizeInfo();
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            final float float2 = Mth.cos((this.getId() * 3 + this.tickCount) * 0.13f + 3.1415927f);
            final float float3 = Mth.cos((this.getId() * 3 + this.tickCount + 1) * 0.13f + 3.1415927f);
            if (float2 > 0.0f && float3 <= 0.0f) {
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            final int integer4 = this.getPhantomSize();
            final float float4 = Mth.cos(this.yRot * 0.017453292f) * (1.3f + 0.21f * integer4);
            final float float5 = Mth.sin(this.yRot * 0.017453292f) * (1.3f + 0.21f * integer4);
            final float float6 = (0.3f + float2 * 0.45f) * (integer4 * 0.2f + 1.0f);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.x + float4, this.y + float6, this.z + float5, 0.0, 0.0, 0.0);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.x - float4, this.y + float6, this.z - float5, 0.0, 0.0, 0.0);
        }
        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }
    
    @Override
    public void aiStep() {
        if (this.isAlive() && this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }
        super.aiStep();
    }
    
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }
    
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.anchorPoint = new BlockPos(this).above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("AX")) {
            this.anchorPoint = new BlockPos(id.getInt("AX"), id.getInt("AY"), id.getInt("AZ"));
        }
        this.setPhantomSize(id.getInt("Size"));
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("AX", this.anchorPoint.getX());
        id.putInt("AY", this.anchorPoint.getY());
        id.putInt("AZ", this.anchorPoint.getZ());
        id.putInt("Size", this.getPhantomSize());
    }
    
    public boolean shouldRenderAtSqrDistance(final double double1) {
        return true;
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.PHANTOM_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }
    
    public MobType getMobType() {
        return MobType.UNDEAD;
    }
    
    protected float getSoundVolume() {
        return 1.0f;
    }
    
    @Override
    public boolean canAttackType(final EntityType<?> ais) {
        return true;
    }
    
    public EntityDimensions getDimensions(final Pose ajh) {
        final int integer3 = this.getPhantomSize();
        final EntityDimensions aip4 = super.getDimensions(ajh);
        final float float5 = (aip4.width + 0.2f * integer3) / aip4.width;
        return aip4.scale(float5);
    }
    
    static {
        ID_SIZE = SynchedEntityData.<Integer>defineId(Phantom.class, EntityDataSerializers.INT);
    }
    
    enum AttackPhase {
        CIRCLE, 
        SWOOP;
    }
    
    class PhantomMoveControl extends MoveControl {
        private float speed;
        
        public PhantomMoveControl(final Mob aiy) {
            super(aiy);
            this.speed = 0.1f;
        }
        
        @Override
        public void tick() {
            if (Phantom.this.horizontalCollision) {
                final Phantom this$0 = Phantom.this;
                this$0.yRot += 180.0f;
                this.speed = 0.1f;
            }
            float float2 = (float)(Phantom.this.moveTargetPoint.x - Phantom.this.x);
            final float float3 = (float)(Phantom.this.moveTargetPoint.y - Phantom.this.y);
            float float4 = (float)(Phantom.this.moveTargetPoint.z - Phantom.this.z);
            double double5 = Mth.sqrt(float2 * float2 + float4 * float4);
            final double double6 = 1.0 - Mth.abs(float3 * 0.7f) / double5;
            float2 *= (float)double6;
            float4 *= (float)double6;
            double5 = Mth.sqrt(float2 * float2 + float4 * float4);
            final double double7 = Mth.sqrt(float2 * float2 + float4 * float4 + float3 * float3);
            final float float5 = Phantom.this.yRot;
            final float float6 = (float)Mth.atan2(float4, float2);
            final float float7 = Mth.wrapDegrees(Phantom.this.yRot + 90.0f);
            final float float8 = Mth.wrapDegrees(float6 * 57.295776f);
            Phantom.this.yRot = Mth.approachDegrees(float7, float8, 4.0f) - 90.0f;
            Phantom.this.yBodyRot = Phantom.this.yRot;
            if (Mth.degreesDifferenceAbs(float5, Phantom.this.yRot) < 3.0f) {
                this.speed = Mth.approach(this.speed, 1.8f, 0.005f * (1.8f / this.speed));
            }
            else {
                this.speed = Mth.approach(this.speed, 0.2f, 0.025f);
            }
            final float float9 = (float)(-(Mth.atan2(-float3, double5) * 57.2957763671875));
            Phantom.this.xRot = float9;
            final float float10 = Phantom.this.yRot + 90.0f;
            final double double8 = this.speed * Mth.cos(float10 * 0.017453292f) * Math.abs(float2 / double7);
            final double double9 = this.speed * Mth.sin(float10 * 0.017453292f) * Math.abs(float4 / double7);
            final double double10 = this.speed * Mth.sin(float9 * 0.017453292f) * Math.abs(float3 / double7);
            final Vec3 csi23 = Phantom.this.getDeltaMovement();
            Phantom.this.setDeltaMovement(csi23.add(new Vec3(double8, double10, double9).subtract(csi23).scale(0.2)));
        }
    }
    
    class PhantomBodyRotationControl extends BodyRotationControl {
        public PhantomBodyRotationControl(final Mob aiy) {
            super(aiy);
        }
        
        @Override
        public void clientTick() {
            Phantom.this.yHeadRot = Phantom.this.yBodyRot;
            Phantom.this.yBodyRot = Phantom.this.yRot;
        }
    }
    
    class PhantomLookControl extends LookControl {
        public PhantomLookControl(final Mob aiy) {
            super(aiy);
        }
        
        @Override
        public void tick() {
        }
    }
    
    abstract class PhantomMoveTargetGoal extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE));
        }
        
        protected boolean touchingTarget() {
            return Phantom.this.moveTargetPoint.distanceToSqr(Phantom.this.x, Phantom.this.y, Phantom.this.z) < 4.0;
        }
    }
    
    class PhantomCircleAroundAnchorGoal extends PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;
        
        private PhantomCircleAroundAnchorGoal() {
        }
        
        @Override
        public boolean canUse() {
            return Phantom.this.getTarget() == null || Phantom.this.attackPhase == AttackPhase.CIRCLE;
        }
        
        @Override
        public void start() {
            this.distance = 5.0f + Phantom.this.random.nextFloat() * 10.0f;
            this.height = -4.0f + Phantom.this.random.nextFloat() * 9.0f;
            this.clockwise = (Phantom.this.random.nextBoolean() ? 1.0f : -1.0f);
            this.selectNext();
        }
        
        @Override
        public void tick() {
            if (Phantom.this.random.nextInt(350) == 0) {
                this.height = -4.0f + Phantom.this.random.nextFloat() * 9.0f;
            }
            if (Phantom.this.random.nextInt(250) == 0) {
                ++this.distance;
                if (this.distance > 15.0f) {
                    this.distance = 5.0f;
                    this.clockwise = -this.clockwise;
                }
            }
            if (Phantom.this.random.nextInt(450) == 0) {
                this.angle = Phantom.this.random.nextFloat() * 2.0f * 3.1415927f;
                this.selectNext();
            }
            if (this.touchingTarget()) {
                this.selectNext();
            }
            if (Phantom.this.moveTargetPoint.y < Phantom.this.y && !Phantom.this.level.isEmptyBlock(new BlockPos(Phantom.this).below(1))) {
                this.height = Math.max(1.0f, this.height);
                this.selectNext();
            }
            if (Phantom.this.moveTargetPoint.y > Phantom.this.y && !Phantom.this.level.isEmptyBlock(new BlockPos(Phantom.this).above(1))) {
                this.height = Math.min(-1.0f, this.height);
                this.selectNext();
            }
        }
        
        private void selectNext() {
            if (BlockPos.ZERO.equals(Phantom.this.anchorPoint)) {
                Phantom.this.anchorPoint = new BlockPos(Phantom.this);
            }
            this.angle += this.clockwise * 15.0f * 0.017453292f;
            Phantom.this.moveTargetPoint = new Vec3(Phantom.this.anchorPoint).add(this.distance * Mth.cos(this.angle), -4.0f + this.height, this.distance * Mth.sin(this.angle));
        }
    }
    
    class PhantomSweepAttackGoal extends PhantomMoveTargetGoal {
        private PhantomSweepAttackGoal() {
        }
        
        @Override
        public boolean canUse() {
            return Phantom.this.getTarget() != null && Phantom.this.attackPhase == AttackPhase.SWOOP;
        }
        
        @Override
        public boolean canContinueToUse() {
            final LivingEntity aix2 = Phantom.this.getTarget();
            if (aix2 == null) {
                return false;
            }
            if (!aix2.isAlive()) {
                return false;
            }
            if (aix2 instanceof Player && (((Player)aix2).isSpectator() || ((Player)aix2).isCreative())) {
                return false;
            }
            if (!this.canUse()) {
                return false;
            }
            if (Phantom.this.tickCount % 20 == 0) {
                final List<Cat> list3 = Phantom.this.level.<Cat>getEntitiesOfClass((java.lang.Class<? extends Cat>)Cat.class, Phantom.this.getBoundingBox().inflate(16.0), (java.util.function.Predicate<? super Cat>)EntitySelector.ENTITY_STILL_ALIVE);
                if (!list3.isEmpty()) {
                    for (final Cat arb5 : list3) {
                        arb5.hiss();
                    }
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public void start() {
        }
        
        @Override
        public void stop() {
            Phantom.this.setTarget(null);
            Phantom.this.attackPhase = AttackPhase.CIRCLE;
        }
        
        @Override
        public void tick() {
            final LivingEntity aix2 = Phantom.this.getTarget();
            Phantom.this.moveTargetPoint = new Vec3(aix2.x, aix2.y + aix2.getBbHeight() * 0.5, aix2.z);
            if (Phantom.this.getBoundingBox().inflate(0.20000000298023224).intersects(aix2.getBoundingBox())) {
                Phantom.this.doHurtTarget(aix2);
                Phantom.this.attackPhase = AttackPhase.CIRCLE;
                Phantom.this.level.levelEvent(1039, new BlockPos(Phantom.this), 0);
            }
            else if (Phantom.this.horizontalCollision || Phantom.this.hurtTime > 0) {
                Phantom.this.attackPhase = AttackPhase.CIRCLE;
            }
        }
    }
    
    class PhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;
        
        private PhantomAttackStrategyGoal() {
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = Phantom.this.getTarget();
            return aix2 != null && Phantom.this.canAttack(Phantom.this.getTarget(), TargetingConditions.DEFAULT);
        }
        
        @Override
        public void start() {
            this.nextSweepTick = 10;
            Phantom.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }
        
        @Override
        public void stop() {
            Phantom.this.anchorPoint = Phantom.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Phantom.this.anchorPoint).above(10 + Phantom.this.random.nextInt(20));
        }
        
        @Override
        public void tick() {
            if (Phantom.this.attackPhase == AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    Phantom.this.attackPhase = AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = (8 + Phantom.this.random.nextInt(4)) * 20;
                    Phantom.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0f, 0.95f + Phantom.this.random.nextFloat() * 0.1f);
                }
            }
        }
        
        private void setAnchorAboveTarget() {
            Phantom.this.anchorPoint = new BlockPos(Phantom.this.getTarget()).above(20 + Phantom.this.random.nextInt(20));
            if (Phantom.this.anchorPoint.getY() < Phantom.this.level.getSeaLevel()) {
                Phantom.this.anchorPoint = new BlockPos(Phantom.this.anchorPoint.getX(), Phantom.this.level.getSeaLevel() + 1, Phantom.this.anchorPoint.getZ());
            }
        }
    }
    
    class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting;
        private int nextScanTick;
        
        private PhantomAttackPlayerTargetGoal() {
            this.attackTargeting = new TargetingConditions().range(64.0);
            this.nextScanTick = 20;
        }
        
        @Override
        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            }
            this.nextScanTick = 60;
            final List<Player> list2 = Phantom.this.level.getNearbyPlayers(this.attackTargeting, Phantom.this, Phantom.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
            if (!list2.isEmpty()) {
                list2.sort((awg1, awg2) -> (awg1.y > awg2.y) ? -1 : 1);
                for (final Player awg4 : list2) {
                    if (Phantom.this.canAttack(awg4, TargetingConditions.DEFAULT)) {
                        Phantom.this.setTarget(awg4);
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public boolean canContinueToUse() {
            final LivingEntity aix2 = Phantom.this.getTarget();
            return aix2 != null && Phantom.this.canAttack(aix2, TargetingConditions.DEFAULT);
        }
    }
}
