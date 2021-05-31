package net.minecraft.world.entity.animal;

import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biomes;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public class PolarBear extends Animal {
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID;
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    
    public PolarBear(final EntityType<? extends PolarBear> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return EntityType.POLAR_BEAR.create(this.level);
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return false;
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PolarBearMeleeAttackGoal());
        this.goalSelector.addGoal(1, new PolarBearPanicGoal());
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new PolarBearHurtByTargetGoal());
        this.targetSelector.addGoal(2, new PolarBearAttackPlayersGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Fox.class, 10, true, true, null));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
    }
    
    public static boolean checkPolarBearSpawnRules(final EntityType<PolarBear> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        final Biome bio6 = bhs.getBiome(ew);
        if (bio6 == Biomes.FROZEN_OCEAN || bio6 == Biomes.DEEP_FROZEN_OCEAN) {
            return bhs.getRawBrightness(ew, 0) > 8 && bhs.getBlockState(ew.below()).getBlock() == Blocks.ICE;
        }
        return Animal.checkAnimalSpawnRules(ais, bhs, aja, ew, random);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isBaby()) {
            return SoundEvents.POLAR_BEAR_AMBIENT_BABY;
        }
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.POLAR_BEAR_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15f, 1.0f);
    }
    
    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0f, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(PolarBear.DATA_STANDING_ID, false);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }
            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0f, 0.0f, 6.0f);
            }
            else {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0f, 0.0f, 6.0f);
            }
        }
        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
    }
    
    @Override
    public EntityDimensions getDimensions(final Pose ajh) {
        if (this.clientSideStandAnimation > 0.0f) {
            final float float3 = this.clientSideStandAnimation / 6.0f;
            final float float4 = 1.0f + float3;
            return super.getDimensions(ajh).scale(1.0f, float4);
        }
        return super.getDimensions(ajh);
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        final boolean boolean3 = aio.hurt(DamageSource.mobAttack(this), (float)(int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
        if (boolean3) {
            this.doEnchantDamageEffects(this, aio);
        }
        return boolean3;
    }
    
    public boolean isStanding() {
        return this.entityData.<Boolean>get(PolarBear.DATA_STANDING_ID);
    }
    
    public void setStanding(final boolean boolean1) {
        this.entityData.<Boolean>set(PolarBear.DATA_STANDING_ID, boolean1);
    }
    
    public float getStandingAnimationScale(final float float1) {
        return Mth.lerp(float1, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0f;
    }
    
    @Override
    protected float getWaterSlowDown() {
        return 0.98f;
    }
    
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        if (ajj instanceof PolarBearGroupData) {
            this.setAge(-24000);
        }
        else {
            ajj = new PolarBearGroupData();
        }
        return ajj;
    }
    
    static {
        DATA_STANDING_ID = SynchedEntityData.<Boolean>defineId(PolarBear.class, EntityDataSerializers.BOOLEAN);
    }
    
    static class PolarBearGroupData implements SpawnGroupData {
        private PolarBearGroupData() {
        }
    }
    
    class PolarBearHurtByTargetGoal extends HurtByTargetGoal {
        public PolarBearHurtByTargetGoal() {
            super(PolarBear.this, new Class[0]);
        }
        
        @Override
        public void start() {
            super.start();
            if (PolarBear.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }
        }
        
        @Override
        protected void alertOther(final Mob aiy, final LivingEntity aix) {
            if (aiy instanceof PolarBear && !aiy.isBaby()) {
                super.alertOther(aiy, aix);
            }
        }
    }
    
    class PolarBearAttackPlayersGoal extends NearestAttackableTargetGoal<Player> {
        public PolarBearAttackPlayersGoal() {
            super(PolarBear.this, Player.class, 20, true, true, null);
        }
        
        @Override
        public boolean canUse() {
            if (PolarBear.this.isBaby()) {
                return false;
            }
            if (super.canUse()) {
                final List<PolarBear> list2 = PolarBear.this.level.<PolarBear>getEntitiesOfClass((java.lang.Class<? extends PolarBear>)PolarBear.class, PolarBear.this.getBoundingBox().inflate(8.0, 4.0, 8.0));
                for (final PolarBear aro4 : list2) {
                    if (aro4.isBaby()) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5;
        }
    }
    
    class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
        public PolarBearMeleeAttackGoal() {
            super(PolarBear.this, 1.25, true);
        }
        
        @Override
        protected void checkAndPerformAttack(final LivingEntity aix, final double double2) {
            final double double3 = this.getAttackReachSqr(aix);
            if (double2 <= double3 && this.attackTime <= 0) {
                this.attackTime = 20;
                this.mob.doHurtTarget(aix);
                PolarBear.this.setStanding(false);
            }
            else if (double2 <= double3 * 2.0) {
                if (this.attackTime <= 0) {
                    PolarBear.this.setStanding(false);
                    this.attackTime = 20;
                }
                if (this.attackTime <= 10) {
                    PolarBear.this.setStanding(true);
                    PolarBear.this.playWarningSound();
                }
            }
            else {
                this.attackTime = 20;
                PolarBear.this.setStanding(false);
            }
        }
        
        @Override
        public void stop() {
            PolarBear.this.setStanding(false);
            super.stop();
        }
        
        @Override
        protected double getAttackReachSqr(final LivingEntity aix) {
            return 4.0f + aix.getBbWidth();
        }
    }
    
    class PolarBearPanicGoal extends PanicGoal {
        public PolarBearPanicGoal() {
            super(PolarBear.this, 2.0);
        }
        
        @Override
        public boolean canUse() {
            return (PolarBear.this.isBaby() || PolarBear.this.isOnFire()) && super.canUse();
        }
    }
}
