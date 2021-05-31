package net.minecraft.world.entity.animal;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillage;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public class IronGolem extends AbstractGolem {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    private int attackAnimationTick;
    private int offerFlowerTick;
    
    public IronGolem(final EntityType<? extends IronGolem> ais, final Level bhr) {
        super(ais, bhr);
        this.maxUpStep = 1.0f;
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0f));
        this.goalSelector.addGoal(2, new MoveBackToVillage(this, 0.6));
        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6, false, 4, () -> false));
        this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (Predicate<LivingEntity>)(aix -> aix instanceof Enemy && !(aix instanceof Creeper))));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(IronGolem.DATA_FLAGS_ID, (Byte)0);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }
    
    @Override
    protected int decreaseAirSupply(final int integer) {
        return integer;
    }
    
    @Override
    protected void doPush(final Entity aio) {
        if (aio instanceof Enemy && !(aio instanceof Creeper) && this.getRandom().nextInt(20) == 0) {
            this.setTarget((LivingEntity)aio);
        }
        super.doPush(aio);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }
        if (this.offerFlowerTick > 0) {
            --this.offerFlowerTick;
        }
        if (Entity.getHorizontalDistanceSqr(this.getDeltaMovement()) > 2.500000277905201E-7 && this.random.nextInt(5) == 0) {
            final int integer2 = Mth.floor(this.x);
            final int integer3 = Mth.floor(this.y - 0.20000000298023224);
            final int integer4 = Mth.floor(this.z);
            final BlockState bvt5 = this.level.getBlockState(new BlockPos(integer2, integer3, integer4));
            if (!bvt5.isAir()) {
                this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, bvt5), this.x + (this.random.nextFloat() - 0.5) * this.getBbWidth(), this.getBoundingBox().minY + 0.1, this.z + (this.random.nextFloat() - 0.5) * this.getBbWidth(), 4.0 * (this.random.nextFloat() - 0.5), 0.5, (this.random.nextFloat() - 0.5) * 4.0);
            }
        }
    }
    
    @Override
    public boolean canAttackType(final EntityType<?> ais) {
        return (!this.isPlayerCreated() || ais != EntityType.PLAYER) && ais != EntityType.CREEPER && super.canAttackType(ais);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("PlayerCreated", this.isPlayerCreated());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setPlayerCreated(id.getBoolean("PlayerCreated"));
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        final boolean boolean3 = aio.hurt(DamageSource.mobAttack(this), (float)(7 + this.random.nextInt(15)));
        if (boolean3) {
            aio.setDeltaMovement(aio.getDeltaMovement().add(0.0, 0.4000000059604645, 0.0));
            this.doEnchantDamageEffects(this, aio);
        }
        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        return boolean3;
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 4) {
            this.attackAnimationTick = 10;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        }
        else if (byte1 == 11) {
            this.offerFlowerTick = 400;
        }
        else if (byte1 == 34) {
            this.offerFlowerTick = 0;
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }
    
    public void offerFlower(final boolean boolean1) {
        if (boolean1) {
            this.offerFlowerTick = 400;
            this.level.broadcastEntityEvent(this, (byte)11);
        }
        else {
            this.offerFlowerTick = 0;
            this.level.broadcastEntityEvent(this, (byte)34);
        }
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.IRON_GOLEM_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0f, 1.0f);
    }
    
    public int getOfferFlowerTick() {
        return this.offerFlowerTick;
    }
    
    public boolean isPlayerCreated() {
        return (this.entityData.<Byte>get(IronGolem.DATA_FLAGS_ID) & 0x1) != 0x0;
    }
    
    public void setPlayerCreated(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(IronGolem.DATA_FLAGS_ID);
        if (boolean1) {
            this.entityData.<Byte>set(IronGolem.DATA_FLAGS_ID, (byte)(byte3 | 0x1));
        }
        else {
            this.entityData.<Byte>set(IronGolem.DATA_FLAGS_ID, (byte)(byte3 & 0xFFFFFFFE));
        }
    }
    
    @Override
    public void die(final DamageSource ahx) {
        super.die(ahx);
    }
    
    @Override
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        final BlockPos ew3 = new BlockPos(this);
        final BlockPos ew4 = ew3.below();
        final BlockState bvt5 = bhu.getBlockState(ew4);
        if (bvt5.entityCanStandOn(bhu, ew4, this)) {
            for (int integer6 = 1; integer6 < 3; ++integer6) {
                final BlockPos ew5 = ew3.above(integer6);
                final BlockState bvt6 = bhu.getBlockState(ew5);
                if (!NaturalSpawner.isValidEmptySpawnBlock(bhu, ew5, bvt6, bvt6.getFluidState())) {
                    return false;
                }
            }
            return NaturalSpawner.isValidEmptySpawnBlock(bhu, ew3, bhu.getBlockState(ew3), Fluids.EMPTY.defaultFluidState()) && bhu.isUnobstructed(this);
        }
        return false;
    }
    
    static {
        DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(IronGolem.class, EntityDataSerializers.BYTE);
    }
}
