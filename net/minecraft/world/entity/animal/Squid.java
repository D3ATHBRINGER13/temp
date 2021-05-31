package net.minecraft.world.entity.animal;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MoverType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Squid extends WaterAnimal {
    public float xBodyRot;
    public float xBodyRotO;
    public float zBodyRot;
    public float zBodyRotO;
    public float tentacleMovement;
    public float oldTentacleMovement;
    public float tentacleAngle;
    public float oldTentacleAngle;
    private float speed;
    private float tentacleSpeed;
    private float rotateSpeed;
    private float tx;
    private float ty;
    private float tz;
    
    public Squid(final EntityType<? extends Squid> ais, final Level bhr) {
        super(ais, bhr);
        this.random.setSeed((long)this.getId());
        this.tentacleSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this));
        this.goalSelector.addGoal(1, new SquidFleeGoal());
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.5f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SQUID_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.SQUID_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SQUID_DEATH;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        this.xBodyRotO = this.xBodyRot;
        this.zBodyRotO = this.zBodyRot;
        this.oldTentacleMovement = this.tentacleMovement;
        this.oldTentacleAngle = this.tentacleAngle;
        this.tentacleMovement += this.tentacleSpeed;
        if (this.tentacleMovement > 6.283185307179586) {
            if (this.level.isClientSide) {
                this.tentacleMovement = 6.2831855f;
            }
            else {
                this.tentacleMovement -= (float)6.283185307179586;
                if (this.random.nextInt(10) == 0) {
                    this.tentacleSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
                }
                this.level.broadcastEntityEvent(this, (byte)19);
            }
        }
        if (this.isInWaterOrBubble()) {
            if (this.tentacleMovement < 3.1415927f) {
                final float float2 = this.tentacleMovement / 3.1415927f;
                this.tentacleAngle = Mth.sin(float2 * float2 * 3.1415927f) * 3.1415927f * 0.25f;
                if (float2 > 0.75) {
                    this.speed = 1.0f;
                    this.rotateSpeed = 1.0f;
                }
                else {
                    this.rotateSpeed *= 0.8f;
                }
            }
            else {
                this.tentacleAngle = 0.0f;
                this.speed *= 0.9f;
                this.rotateSpeed *= 0.99f;
            }
            if (!this.level.isClientSide) {
                this.setDeltaMovement(this.tx * this.speed, this.ty * this.speed, this.tz * this.speed);
            }
            final Vec3 csi2 = this.getDeltaMovement();
            final float float3 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi2));
            this.yBodyRot += (-(float)Mth.atan2(csi2.x, csi2.z) * 57.295776f - this.yBodyRot) * 0.1f;
            this.yRot = this.yBodyRot;
            this.zBodyRot += (float)(3.141592653589793 * this.rotateSpeed * 1.5);
            this.xBodyRot += (-(float)Mth.atan2(float3, csi2.y) * 57.295776f - this.xBodyRot) * 0.1f;
        }
        else {
            this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * 3.1415927f * 0.25f;
            if (!this.level.isClientSide) {
                double double2 = this.getDeltaMovement().y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    double2 = 0.05 * (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
                }
                else if (!this.isNoGravity()) {
                    double2 -= 0.08;
                }
                this.setDeltaMovement(0.0, double2 * 0.9800000190734863, 0.0);
            }
            this.xBodyRot += (float)((-90.0f - this.xBodyRot) * 0.02);
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (super.hurt(ahx, float2) && this.getLastHurtByMob() != null) {
            this.spawnInk();
            return true;
        }
        return false;
    }
    
    private Vec3 rotateVector(final Vec3 csi) {
        Vec3 csi2 = csi.xRot(this.xBodyRotO * 0.017453292f);
        csi2 = csi2.yRot(-this.yBodyRotO * 0.017453292f);
        return csi2;
    }
    
    private void spawnInk() {
        this.playSound(SoundEvents.SQUID_SQUIRT, this.getSoundVolume(), this.getVoicePitch());
        final Vec3 csi2 = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.x, this.y, this.z);
        for (int integer3 = 0; integer3 < 30; ++integer3) {
            final Vec3 csi3 = this.rotateVector(new Vec3(this.random.nextFloat() * 0.6 - 0.3, -1.0, this.random.nextFloat() * 0.6 - 0.3));
            final Vec3 csi4 = csi3.scale(0.3 + this.random.nextFloat() * 2.0f);
            ((ServerLevel)this.level).<SimpleParticleType>sendParticles(ParticleTypes.SQUID_INK, csi2.x, csi2.y + 0.5, csi2.z, 0, csi4.x, csi4.y, csi4.z, 0.10000000149011612);
        }
    }
    
    @Override
    public void travel(final Vec3 csi) {
        this.move(MoverType.SELF, this.getDeltaMovement());
    }
    
    public static boolean checkSquidSpawnRules(final EntityType<Squid> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return ew.getY() > 45 && ew.getY() < bhs.getSeaLevel();
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 19) {
            this.tentacleMovement = 0.0f;
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public void setMovementVector(final float float1, final float float2, final float float3) {
        this.tx = float1;
        this.ty = float2;
        this.tz = float3;
    }
    
    public boolean hasMovementVector() {
        return this.tx != 0.0f || this.ty != 0.0f || this.tz != 0.0f;
    }
    
    class SquidRandomMovementGoal extends Goal {
        private final Squid squid;
        
        public SquidRandomMovementGoal(final Squid arv2) {
            this.squid = arv2;
        }
        
        @Override
        public boolean canUse() {
            return true;
        }
        
        @Override
        public void tick() {
            final int integer2 = this.squid.getNoActionTime();
            if (integer2 > 100) {
                this.squid.setMovementVector(0.0f, 0.0f, 0.0f);
            }
            else if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.wasInWater || !this.squid.hasMovementVector()) {
                final float float3 = this.squid.getRandom().nextFloat() * 6.2831855f;
                final float float4 = Mth.cos(float3) * 0.2f;
                final float float5 = -0.1f + this.squid.getRandom().nextFloat() * 0.2f;
                final float float6 = Mth.sin(float3) * 0.2f;
                this.squid.setMovementVector(float4, float5, float6);
            }
        }
    }
    
    class SquidFleeGoal extends Goal {
        private int fleeTicks;
        
        private SquidFleeGoal() {
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = Squid.this.getLastHurtByMob();
            return Squid.this.isInWater() && aix2 != null && Squid.this.distanceToSqr(aix2) < 100.0;
        }
        
        @Override
        public void start() {
            this.fleeTicks = 0;
        }
        
        @Override
        public void tick() {
            ++this.fleeTicks;
            final LivingEntity aix2 = Squid.this.getLastHurtByMob();
            if (aix2 == null) {
                return;
            }
            Vec3 csi3 = new Vec3(Squid.this.x - aix2.x, Squid.this.y - aix2.y, Squid.this.z - aix2.z);
            final BlockState bvt4 = Squid.this.level.getBlockState(new BlockPos(Squid.this.x + csi3.x, Squid.this.y + csi3.y, Squid.this.z + csi3.z));
            final FluidState clk5 = Squid.this.level.getFluidState(new BlockPos(Squid.this.x + csi3.x, Squid.this.y + csi3.y, Squid.this.z + csi3.z));
            if (clk5.is(FluidTags.WATER) || bvt4.isAir()) {
                final double double6 = csi3.length();
                if (double6 > 0.0) {
                    csi3.normalize();
                    float float8 = 3.0f;
                    if (double6 > 5.0) {
                        float8 -= (float)((double6 - 5.0) / 5.0);
                    }
                    if (float8 > 0.0f) {
                        csi3 = csi3.scale(float8);
                    }
                }
                if (bvt4.isAir()) {
                    csi3 = csi3.subtract(0.0, csi3.y, 0.0);
                }
                Squid.this.setMovementVector((float)csi3.x / 20.0f, (float)csi3.y / 20.0f, (float)csi3.z / 20.0f);
            }
            if (this.fleeTicks % 10 == 5) {
                Squid.this.level.addParticle(ParticleTypes.BUBBLE, Squid.this.x, Squid.this.y, Squid.this.z, 0.0, 0.0, 0.0);
            }
        }
    }
}
