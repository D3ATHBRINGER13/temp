package net.minecraft.world.entity.animal;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.MoverType;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.biome.Biomes;
import java.util.Random;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.DolphinLookControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Dolphin extends WaterAnimal {
    private static final EntityDataAccessor<BlockPos> TREASURE_POS;
    private static final EntityDataAccessor<Boolean> GOT_FISH;
    private static final EntityDataAccessor<Integer> MOISNTESS_LEVEL;
    private static final TargetingConditions SWIM_WITH_PLAYER_TARGETING;
    public static final Predicate<ItemEntity> ALLOWED_ITEMS;
    
    public Dolphin(final EntityType<? extends Dolphin> ais, final Level bhr) {
        super(ais, bhr);
        this.moveControl = new DolphinMoveControl(this);
        this.lookControl = new DolphinLookControl(this, 10);
        this.setCanPickUpLoot(true);
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.setAirSupply(this.getMaxAirSupply());
        this.xRot = 0.0f;
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }
    
    @Override
    protected void handleAirSupply(final int integer) {
    }
    
    public void setTreasurePos(final BlockPos ew) {
        this.entityData.<BlockPos>set(Dolphin.TREASURE_POS, ew);
    }
    
    public BlockPos getTreasurePos() {
        return this.entityData.<BlockPos>get(Dolphin.TREASURE_POS);
    }
    
    public boolean gotFish() {
        return this.entityData.<Boolean>get(Dolphin.GOT_FISH);
    }
    
    public void setGotFish(final boolean boolean1) {
        this.entityData.<Boolean>set(Dolphin.GOT_FISH, boolean1);
    }
    
    public int getMoistnessLevel() {
        return this.entityData.<Integer>get(Dolphin.MOISNTESS_LEVEL);
    }
    
    public void setMoisntessLevel(final int integer) {
        this.entityData.<Integer>set(Dolphin.MOISNTESS_LEVEL, integer);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<BlockPos>define(Dolphin.TREASURE_POS, BlockPos.ZERO);
        this.entityData.<Boolean>define(Dolphin.GOT_FISH, false);
        this.entityData.<Integer>define(Dolphin.MOISNTESS_LEVEL, 2400);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("TreasurePosX", this.getTreasurePos().getX());
        id.putInt("TreasurePosY", this.getTreasurePos().getY());
        id.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        id.putBoolean("GotFish", this.gotFish());
        id.putInt("Moistness", this.getMoistnessLevel());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        final int integer3 = id.getInt("TreasurePosX");
        final int integer4 = id.getInt("TreasurePosY");
        final int integer5 = id.getInt("TreasurePosZ");
        this.setTreasurePos(new BlockPos(integer3, integer4, integer5));
        super.readAdditionalSaveData(id);
        this.setGotFish(id.getBoolean("GotFish"));
        this.setMoisntessLevel(id.getInt("Moistness"));
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreathAirGoal(this));
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new DolphinSwimToTreasureGoal(this));
        this.goalSelector.addGoal(2, new DolphinSwimWithPlayerGoal(this, 4.0));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2000000476837158, true));
        this.goalSelector.addGoal(8, new PlayWithItemsGoal());
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, Guardian.class, 8.0f, 1.0, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[] { Guardian.class }).setAlertOthers(new Class[0]));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.2000000476837158);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
    }
    
    @Override
    protected PathNavigation createNavigation(final Level bhr) {
        return new WaterBoundPathNavigation(this, bhr);
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        final boolean boolean3 = aio.hurt(DamageSource.mobAttack(this), (float)(int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
        if (boolean3) {
            this.doEnchantDamageEffects(this, aio);
            this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0f, 1.0f);
        }
        return boolean3;
    }
    
    @Override
    public int getMaxAirSupply() {
        return 4800;
    }
    
    @Override
    protected int increaseAirSupply(final int integer) {
        return this.getMaxAirSupply();
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.3f;
    }
    
    @Override
    public int getMaxHeadXRot() {
        return 1;
    }
    
    @Override
    public int getMaxHeadYRot() {
        return 1;
    }
    
    @Override
    protected boolean canRide(final Entity aio) {
        return true;
    }
    
    @Override
    public boolean canTakeItem(final ItemStack bcj) {
        final EquipmentSlot ait3 = Mob.getEquipmentSlotForItem(bcj);
        return this.getItemBySlot(ait3).isEmpty() && ait3 == EquipmentSlot.MAINHAND && super.canTakeItem(bcj);
    }
    
    @Override
    protected void pickUpItem(final ItemEntity atx) {
        if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            final ItemStack bcj3 = atx.getItem();
            if (this.canHoldItem(bcj3)) {
                this.setItemSlot(EquipmentSlot.MAINHAND, bcj3);
                this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0f;
                this.take(atx, bcj3.getCount());
                atx.remove();
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.isNoAi()) {
            return;
        }
        if (this.isInWaterRainOrBubble()) {
            this.setMoisntessLevel(2400);
        }
        else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
                this.hurt(DamageSource.DRY_OUT, 1.0f);
            }
            if (this.onGround) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.2f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f));
                this.yRot = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.hasImpulse = true;
            }
        }
        if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03) {
            final Vec3 csi2 = this.getViewVector(0.0f);
            final float float3 = Mth.cos(this.yRot * 0.017453292f) * 0.3f;
            final float float4 = Mth.sin(this.yRot * 0.017453292f) * 0.3f;
            final float float5 = 1.2f - this.random.nextFloat() * 0.7f;
            for (int integer6 = 0; integer6 < 2; ++integer6) {
                this.level.addParticle(ParticleTypes.DOLPHIN, this.x - csi2.x * float5 + float3, this.y - csi2.y, this.z - csi2.z * float5 + float4, 0.0, 0.0, 0.0);
                this.level.addParticle(ParticleTypes.DOLPHIN, this.x - csi2.x * float5 - float3, this.y - csi2.y, this.z - csi2.z * float5 - float4, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 38) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    private void addParticlesAroundSelf(final ParticleOptions gf) {
        for (int integer3 = 0; integer3 < 7; ++integer3) {
            final double double4 = this.random.nextGaussian() * 0.01;
            final double double5 = this.random.nextGaussian() * 0.01;
            final double double6 = this.random.nextGaussian() * 0.01;
            this.level.addParticle(gf, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.20000000298023224 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double4, double5, double6);
        }
    }
    
    @Override
    protected boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (!bcj4.isEmpty() && bcj4.getItem().is(ItemTags.FISHES)) {
            if (!this.level.isClientSide) {
                this.playSound(SoundEvents.DOLPHIN_EAT, 1.0f, 1.0f);
            }
            this.setGotFish(true);
            if (!awg.abilities.instabuild) {
                bcj4.shrink(1);
            }
            return true;
        }
        return super.mobInteract(awg, ahi);
    }
    
    public static boolean checkDolphinSpawnRules(final EntityType<Dolphin> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return ew.getY() > 45 && ew.getY() < bhs.getSeaLevel() && (bhs.getBiome(ew) != Biomes.OCEAN || bhs.getBiome(ew) != Biomes.DEEP_OCEAN) && bhs.getFluidState(ew).is(FluidTags.WATER);
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.DOLPHIN_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
    }
    
    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.DOLPHIN_SPLASH;
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }
    
    protected boolean closeToNextPos() {
        final BlockPos ew2 = this.getNavigation().getTargetPos();
        return ew2 != null && ew2.closerThan(this.position(), 12.0);
    }
    
    @Override
    public void travel(final Vec3 csi) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), csi);
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
    public boolean canBeLeashed(final Player awg) {
        return true;
    }
    
    static {
        TREASURE_POS = SynchedEntityData.<BlockPos>defineId(Dolphin.class, EntityDataSerializers.BLOCK_POS);
        GOT_FISH = SynchedEntityData.<Boolean>defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);
        MOISNTESS_LEVEL = SynchedEntityData.<Integer>defineId(Dolphin.class, EntityDataSerializers.INT);
        SWIM_WITH_PLAYER_TARGETING = new TargetingConditions().range(10.0).allowSameTeam().allowInvulnerable().allowUnseeable();
        ALLOWED_ITEMS = (atx -> !atx.hasPickUpDelay() && atx.isAlive() && atx.isInWater());
    }
    
    static class DolphinMoveControl extends MoveControl {
        private final Dolphin dolphin;
        
        public DolphinMoveControl(final Dolphin arf) {
            super(arf);
            this.dolphin = arf;
        }
        
        @Override
        public void tick() {
            if (this.dolphin.isInWater()) {
                this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0, 0.005, 0.0));
            }
            if (this.operation != Operation.MOVE_TO || this.dolphin.getNavigation().isDone()) {
                this.dolphin.setSpeed(0.0f);
                this.dolphin.setXxa(0.0f);
                this.dolphin.setYya(0.0f);
                this.dolphin.setZza(0.0f);
                return;
            }
            final double double2 = this.wantedX - this.dolphin.x;
            final double double3 = this.wantedY - this.dolphin.y;
            final double double4 = this.wantedZ - this.dolphin.z;
            final double double5 = double2 * double2 + double3 * double3 + double4 * double4;
            if (double5 < 2.500000277905201E-7) {
                this.mob.setZza(0.0f);
                return;
            }
            final float float10 = (float)(Mth.atan2(double4, double2) * 57.2957763671875) - 90.0f;
            this.dolphin.yRot = this.rotlerp(this.dolphin.yRot, float10, 10.0f);
            this.dolphin.yBodyRot = this.dolphin.yRot;
            this.dolphin.yHeadRot = this.dolphin.yRot;
            final float float11 = (float)(this.speedModifier * this.dolphin.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            if (this.dolphin.isInWater()) {
                this.dolphin.setSpeed(float11 * 0.02f);
                float float12 = -(float)(Mth.atan2(double3, Mth.sqrt(double2 * double2 + double4 * double4)) * 57.2957763671875);
                float12 = Mth.clamp(Mth.wrapDegrees(float12), -85.0f, 85.0f);
                this.dolphin.xRot = this.rotlerp(this.dolphin.xRot, float12, 5.0f);
                final float float13 = Mth.cos(this.dolphin.xRot * 0.017453292f);
                final float float14 = Mth.sin(this.dolphin.xRot * 0.017453292f);
                this.dolphin.zza = float13 * float11;
                this.dolphin.yya = -float14 * float11;
            }
            else {
                this.dolphin.setSpeed(float11 * 0.1f);
            }
        }
    }
    
    class PlayWithItemsGoal extends Goal {
        private int cooldown;
        
        private PlayWithItemsGoal() {
        }
        
        @Override
        public boolean canUse() {
            if (this.cooldown > Dolphin.this.tickCount) {
                return false;
            }
            final List<ItemEntity> list2 = Dolphin.this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), (java.util.function.Predicate<? super ItemEntity>)Dolphin.ALLOWED_ITEMS);
            return !list2.isEmpty() || !Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }
        
        @Override
        public void start() {
            final List<ItemEntity> list2 = Dolphin.this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), (java.util.function.Predicate<? super ItemEntity>)Dolphin.ALLOWED_ITEMS);
            if (!list2.isEmpty()) {
                Dolphin.this.getNavigation().moveTo((Entity)list2.get(0), 1.2000000476837158);
                Dolphin.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0f, 1.0f);
            }
            this.cooldown = 0;
        }
        
        @Override
        public void stop() {
            final ItemStack bcj2 = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!bcj2.isEmpty()) {
                this.drop(bcj2);
                Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.cooldown = Dolphin.this.tickCount + Dolphin.this.random.nextInt(100);
            }
        }
        
        @Override
        public void tick() {
            final List<ItemEntity> list2 = Dolphin.this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), (java.util.function.Predicate<? super ItemEntity>)Dolphin.ALLOWED_ITEMS);
            final ItemStack bcj3 = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!bcj3.isEmpty()) {
                this.drop(bcj3);
                Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            else if (!list2.isEmpty()) {
                Dolphin.this.getNavigation().moveTo((Entity)list2.get(0), 1.2000000476837158);
            }
        }
        
        private void drop(final ItemStack bcj) {
            if (bcj.isEmpty()) {
                return;
            }
            final double double3 = Dolphin.this.y - 0.30000001192092896 + Dolphin.this.getEyeHeight();
            final ItemEntity atx5 = new ItemEntity(Dolphin.this.level, Dolphin.this.x, double3, Dolphin.this.z, bcj);
            atx5.setPickUpDelay(40);
            atx5.setThrower(Dolphin.this.getUUID());
            final float float6 = 0.3f;
            final float float7 = Dolphin.this.random.nextFloat() * 6.2831855f;
            final float float8 = 0.02f * Dolphin.this.random.nextFloat();
            atx5.setDeltaMovement(0.3f * -Mth.sin(Dolphin.this.yRot * 0.017453292f) * Mth.cos(Dolphin.this.xRot * 0.017453292f) + Mth.cos(float7) * float8, 0.3f * Mth.sin(Dolphin.this.xRot * 0.017453292f) * 1.5f, 0.3f * Mth.cos(Dolphin.this.yRot * 0.017453292f) * Mth.cos(Dolphin.this.xRot * 0.017453292f) + Mth.sin(float7) * float8);
            Dolphin.this.level.addFreshEntity(atx5);
        }
    }
    
    static class DolphinSwimWithPlayerGoal extends Goal {
        private final Dolphin dolphin;
        private final double speedModifier;
        private Player player;
        
        DolphinSwimWithPlayerGoal(final Dolphin arf, final double double2) {
            this.dolphin = arf;
            this.speedModifier = double2;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            this.player = this.dolphin.level.getNearestPlayer(Dolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
            return this.player != null && this.player.isSwimming();
        }
        
        @Override
        public boolean canContinueToUse() {
            return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0;
        }
        
        @Override
        public void start() {
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100));
        }
        
        @Override
        public void stop() {
            this.player = null;
            this.dolphin.getNavigation().stop();
        }
        
        @Override
        public void tick() {
            this.dolphin.getLookControl().setLookAt(this.player, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
            if (this.dolphin.distanceToSqr(this.player) < 6.25) {
                this.dolphin.getNavigation().stop();
            }
            else {
                this.dolphin.getNavigation().moveTo(this.player, this.speedModifier);
            }
            if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
                this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100));
            }
        }
    }
    
    static class DolphinSwimToTreasureGoal extends Goal {
        private final Dolphin dolphin;
        private boolean stuck;
        
        DolphinSwimToTreasureGoal(final Dolphin arf) {
            this.dolphin = arf;
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean isInterruptable() {
            return false;
        }
        
        @Override
        public boolean canUse() {
            return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
        }
        
        @Override
        public boolean canContinueToUse() {
            final BlockPos ew2 = this.dolphin.getTreasurePos();
            return !new BlockPos(ew2.getX(), this.dolphin.y, ew2.getZ()).closerThan(this.dolphin.position(), 4.0) && !this.stuck && this.dolphin.getAirSupply() >= 100;
        }
        
        @Override
        public void start() {
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            final Level bhr2 = this.dolphin.level;
            final BlockPos ew3 = new BlockPos(this.dolphin);
            final String string4 = (bhr2.random.nextFloat() >= 0.5) ? "Ocean_Ruin" : "Shipwreck";
            final BlockPos ew4 = bhr2.findNearestMapFeature(string4, ew3, 50, false);
            if (ew4 == null) {
                final BlockPos ew5 = bhr2.findNearestMapFeature(string4.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", ew3, 50, false);
                if (ew5 == null) {
                    this.stuck = true;
                    return;
                }
                this.dolphin.setTreasurePos(ew5);
            }
            else {
                this.dolphin.setTreasurePos(ew4);
            }
            bhr2.broadcastEntityEvent(this.dolphin, (byte)38);
        }
        
        @Override
        public void stop() {
            final BlockPos ew2 = this.dolphin.getTreasurePos();
            if (new BlockPos(ew2.getX(), this.dolphin.y, ew2.getZ()).closerThan(this.dolphin.position(), 4.0) || this.stuck) {
                this.dolphin.setGotFish(false);
            }
        }
        
        @Override
        public void tick() {
            final BlockPos ew2 = this.dolphin.getTreasurePos();
            final Level bhr3 = this.dolphin.level;
            if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
                Vec3 csi4 = RandomPos.getPosTowards(this.dolphin, 16, 1, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()), 0.39269909262657166);
                if (csi4 == null) {
                    csi4 = RandomPos.getPosTowards(this.dolphin, 8, 4, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()));
                }
                if (csi4 != null) {
                    final BlockPos ew3 = new BlockPos(csi4);
                    if (!bhr3.getFluidState(ew3).is(FluidTags.WATER) || !bhr3.getBlockState(ew3).isPathfindable(bhr3, ew3, PathComputationType.WATER)) {
                        csi4 = RandomPos.getPosTowards(this.dolphin, 8, 5, new Vec3(ew2.getX(), ew2.getY(), ew2.getZ()));
                    }
                }
                if (csi4 == null) {
                    this.stuck = true;
                    return;
                }
                this.dolphin.getLookControl().setLookAt(csi4.x, csi4.y, csi4.z, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
                this.dolphin.getNavigation().moveTo(csi4.x, csi4.y, csi4.z, 1.3);
                if (bhr3.random.nextInt(80) == 0) {
                    bhr3.broadcastEntityEvent(this.dolphin, (byte)38);
                }
            }
        }
    }
}
