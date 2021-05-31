package net.minecraft.world.entity.animal;

import net.minecraft.util.Mth;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public abstract class AbstractFish extends WaterAnimal {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET;
    
    public AbstractFish(final EntityType<? extends AbstractFish> ais, final Level bhr) {
        super(ais, bhr);
        this.moveControl = new FishMoveControl(this);
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.65f;
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0);
    }
    
    @Override
    public boolean requiresCustomPersistence() {
        return this.fromBucket();
    }
    
    public static boolean checkFishSpawnRules(final EntityType<? extends AbstractFish> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getBlockState(ew).getBlock() == Blocks.WATER && bhs.getBlockState(ew.above()).getBlock() == Blocks.WATER;
    }
    
    @Override
    public boolean removeWhenFarAway(final double double1) {
        return !this.fromBucket() && !this.hasCustomName();
    }
    
    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(AbstractFish.FROM_BUCKET, false);
    }
    
    private boolean fromBucket() {
        return this.entityData.<Boolean>get(AbstractFish.FROM_BUCKET);
    }
    
    public void setFromBucket(final boolean boolean1) {
        this.entityData.<Boolean>set(AbstractFish.FROM_BUCKET, boolean1);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("FromBucket", this.fromBucket());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setFromBucket(id.getBoolean("FromBucket"));
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0f, 1.6, 1.4, (Predicate<LivingEntity>)EntitySelector.NO_SPECTATORS::test));
        this.goalSelector.addGoal(4, new FishSwimGoal(this));
    }
    
    @Override
    protected PathNavigation createNavigation(final Level bhr) {
        return new WaterBoundPathNavigation(this, bhr);
    }
    
    @Override
    public void travel(final Vec3 csi) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01f, csi);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        }
        else {
            super.travel(csi);
        }
    }
    
    @Override
    public void aiStep() {
        if (!this.isInWater() && this.onGround && this.verticalCollision) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f, 0.4000000059604645, (this.random.nextFloat() * 2.0f - 1.0f) * 0.05f));
            this.onGround = false;
            this.hasImpulse = true;
            this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getVoicePitch());
        }
        super.aiStep();
    }
    
    @Override
    protected boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f);
            bcj4.shrink(1);
            final ItemStack bcj5 = this.getBucketItemStack();
            this.saveToBucketTag(bcj5);
            if (!this.level.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)awg, bcj5);
            }
            if (bcj4.isEmpty()) {
                awg.setItemInHand(ahi, bcj5);
            }
            else if (!awg.inventory.add(bcj5)) {
                awg.drop(bcj5, false);
            }
            this.remove();
            return true;
        }
        return super.mobInteract(awg, ahi);
    }
    
    protected void saveToBucketTag(final ItemStack bcj) {
        if (this.hasCustomName()) {
            bcj.setHoverName(this.getCustomName());
        }
    }
    
    protected abstract ItemStack getBucketItemStack();
    
    protected boolean canRandomSwim() {
        return true;
    }
    
    protected abstract SoundEvent getFlopSound();
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }
    
    static {
        FROM_BUCKET = SynchedEntityData.<Boolean>defineId(AbstractFish.class, EntityDataSerializers.BOOLEAN);
    }
    
    static class FishSwimGoal extends RandomSwimmingGoal {
        private final AbstractFish fish;
        
        public FishSwimGoal(final AbstractFish aqx) {
            super(aqx, 1.0, 40);
            this.fish = aqx;
        }
        
        @Override
        public boolean canUse() {
            return this.fish.canRandomSwim() && super.canUse();
        }
    }
    
    static class FishMoveControl extends MoveControl {
        private final AbstractFish fish;
        
        FishMoveControl(final AbstractFish aqx) {
            super(aqx);
            this.fish = aqx;
        }
        
        @Override
        public void tick() {
            if (this.fish.isUnderLiquid(FluidTags.WATER)) {
                this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0, 0.005, 0.0));
            }
            if (this.operation != Operation.MOVE_TO || this.fish.getNavigation().isDone()) {
                this.fish.setSpeed(0.0f);
                return;
            }
            final double double2 = this.wantedX - this.fish.x;
            double double3 = this.wantedY - this.fish.y;
            final double double4 = this.wantedZ - this.fish.z;
            final double double5 = Mth.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
            double3 /= double5;
            final float float10 = (float)(Mth.atan2(double4, double2) * 57.2957763671875) - 90.0f;
            this.fish.yRot = this.rotlerp(this.fish.yRot, float10, 90.0f);
            this.fish.yBodyRot = this.fish.yRot;
            final float float11 = (float)(this.speedModifier * this.fish.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.fish.setSpeed(Mth.lerp(0.125f, this.fish.getSpeed(), float11));
            this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(0.0, this.fish.getSpeed() * double3 * 0.1, 0.0));
        }
    }
}
