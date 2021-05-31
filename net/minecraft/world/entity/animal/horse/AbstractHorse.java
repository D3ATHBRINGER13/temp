package net.minecraft.world.entity.animal.horse;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobType;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.SimpleContainer;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.animal.Animal;

public abstract class AbstractHorse extends Animal implements ContainerListener, PlayerRideableJumping {
    private static final Predicate<LivingEntity> PARENT_HORSE_SELECTOR;
    private static final TargetingConditions MOMMY_TARGETING;
    protected static final Attribute JUMP_STRENGTH;
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS;
    private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID;
    private int eatingCounter;
    private int mouthCounter;
    private int standCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean isJumping;
    protected SimpleContainer inventory;
    protected int temper;
    protected float playerJumpPendingScale;
    private boolean allowStandSliding;
    private float eatAnim;
    private float eatAnimO;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected boolean canGallop;
    protected int gallopSoundCounter;
    
    protected AbstractHorse(final EntityType<? extends AbstractHorse> ais, final Level bhr) {
        super(ais, bhr);
        this.canGallop = true;
        this.maxUpStep = 1.0f;
        this.createInventory();
    }
    
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0, AbstractHorse.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }
    
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(AbstractHorse.DATA_ID_FLAGS, (Byte)0);
        this.entityData.<Optional<UUID>>define(AbstractHorse.DATA_ID_OWNER_UUID, (Optional<UUID>)Optional.empty());
    }
    
    protected boolean getFlag(final int integer) {
        return (this.entityData.<Byte>get(AbstractHorse.DATA_ID_FLAGS) & integer) != 0x0;
    }
    
    protected void setFlag(final int integer, final boolean boolean2) {
        final byte byte4 = this.entityData.<Byte>get(AbstractHorse.DATA_ID_FLAGS);
        if (boolean2) {
            this.entityData.<Byte>set(AbstractHorse.DATA_ID_FLAGS, (byte)(byte4 | integer));
        }
        else {
            this.entityData.<Byte>set(AbstractHorse.DATA_ID_FLAGS, (byte)(byte4 & ~integer));
        }
    }
    
    public boolean isTamed() {
        return this.getFlag(2);
    }
    
    @Nullable
    public UUID getOwnerUUID() {
        return (UUID)this.entityData.<Optional<UUID>>get(AbstractHorse.DATA_ID_OWNER_UUID).orElse(null);
    }
    
    public void setOwnerUUID(@Nullable final UUID uUID) {
        this.entityData.<Optional<UUID>>set(AbstractHorse.DATA_ID_OWNER_UUID, (Optional<UUID>)Optional.ofNullable(uUID));
    }
    
    public boolean isJumping() {
        return this.isJumping;
    }
    
    public void setTamed(final boolean boolean1) {
        this.setFlag(2, boolean1);
    }
    
    public void setIsJumping(final boolean boolean1) {
        this.isJumping = boolean1;
    }
    
    public boolean canBeLeashed(final Player awg) {
        return super.canBeLeashed(awg) && this.getMobType() != MobType.UNDEAD;
    }
    
    protected void onLeashDistance(final float float1) {
        if (float1 > 6.0f && this.isEating()) {
            this.setEating(false);
        }
    }
    
    public boolean isEating() {
        return this.getFlag(16);
    }
    
    public boolean isStanding() {
        return this.getFlag(32);
    }
    
    public boolean isBred() {
        return this.getFlag(8);
    }
    
    public void setBred(final boolean boolean1) {
        this.setFlag(8, boolean1);
    }
    
    public void setSaddled(final boolean boolean1) {
        this.setFlag(4, boolean1);
    }
    
    public int getTemper() {
        return this.temper;
    }
    
    public void setTemper(final int integer) {
        this.temper = integer;
    }
    
    public int modifyTemper(final int integer) {
        final int integer2 = Mth.clamp(this.getTemper() + integer, 0, this.getMaxTemper());
        this.setTemper(integer2);
        return integer2;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        final Entity aio4 = ahx.getEntity();
        return (!this.isVehicle() || aio4 == null || !this.hasIndirectPassenger(aio4)) && super.hurt(ahx, float2);
    }
    
    public boolean isPushable() {
        return !this.isVehicle();
    }
    
    private void eating() {
        this.openMouth();
        if (!this.isSilent()) {
            this.level.playSound(null, this.x, this.y, this.z, SoundEvents.HORSE_EAT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }
    
    public void causeFallDamage(final float float1, final float float2) {
        if (float1 > 1.0f) {
            this.playSound(SoundEvents.HORSE_LAND, 0.4f, 1.0f);
        }
        final int integer4 = Mth.ceil((float1 * 0.5f - 3.0f) * float2);
        if (integer4 <= 0) {
            return;
        }
        this.hurt(DamageSource.FALL, (float)integer4);
        if (this.isVehicle()) {
            for (final Entity aio6 : this.getIndirectPassengers()) {
                aio6.hurt(DamageSource.FALL, (float)integer4);
            }
        }
        final BlockState bvt5 = this.level.getBlockState(new BlockPos(this.x, this.y - 0.2 - this.yRotO, this.z));
        if (!bvt5.isAir() && !this.isSilent()) {
            final SoundType bry6 = bvt5.getSoundType();
            this.level.playSound(null, this.x, this.y, this.z, bry6.getStepSound(), this.getSoundSource(), bry6.getVolume() * 0.5f, bry6.getPitch() * 0.75f);
        }
    }
    
    protected int getInventorySize() {
        return 2;
    }
    
    protected void createInventory() {
        final SimpleContainer aho2 = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (aho2 != null) {
            aho2.removeListener(this);
            for (int integer3 = Math.min(aho2.getContainerSize(), this.inventory.getContainerSize()), integer4 = 0; integer4 < integer3; ++integer4) {
                final ItemStack bcj5 = aho2.getItem(integer4);
                if (!bcj5.isEmpty()) {
                    this.inventory.setItem(integer4, bcj5.copy());
                }
            }
        }
        this.inventory.addListener(this);
        this.updateEquipment();
    }
    
    protected void updateEquipment() {
        if (this.level.isClientSide) {
            return;
        }
        this.setSaddled(!this.inventory.getItem(0).isEmpty() && this.canBeSaddled());
    }
    
    @Override
    public void containerChanged(final Container ahc) {
        final boolean boolean3 = this.isSaddled();
        this.updateEquipment();
        if (this.tickCount > 20 && !boolean3 && this.isSaddled()) {
            this.playSound(SoundEvents.HORSE_SADDLE, 0.5f, 1.0f);
        }
    }
    
    public double getCustomJump() {
        return this.getAttribute(AbstractHorse.JUMP_STRENGTH).getValue();
    }
    
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }
    
    @Nullable
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        if (this.random.nextInt(3) == 0) {
            this.stand();
        }
        return null;
    }
    
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.stand();
        }
        return null;
    }
    
    public boolean canBeSaddled() {
        return true;
    }
    
    public boolean isSaddled() {
        return this.getFlag(4);
    }
    
    @Nullable
    protected SoundEvent getAngrySound() {
        this.stand();
        return null;
    }
    
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        if (bvt.getMaterial().isLiquid()) {
            return;
        }
        final BlockState bvt2 = this.level.getBlockState(ew.above());
        SoundType bry5 = bvt.getSoundType();
        if (bvt2.getBlock() == Blocks.SNOW) {
            bry5 = bvt2.getSoundType();
        }
        if (this.isVehicle() && this.canGallop) {
            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                this.playGallopSound(bry5);
            }
            else if (this.gallopSoundCounter <= 5) {
                this.playSound(SoundEvents.HORSE_STEP_WOOD, bry5.getVolume() * 0.15f, bry5.getPitch());
            }
        }
        else if (bry5 == SoundType.WOOD) {
            this.playSound(SoundEvents.HORSE_STEP_WOOD, bry5.getVolume() * 0.15f, bry5.getPitch());
        }
        else {
            this.playSound(SoundEvents.HORSE_STEP, bry5.getVolume() * 0.15f, bry5.getPitch());
        }
    }
    
    protected void playGallopSound(final SoundType bry) {
        this.playSound(SoundEvents.HORSE_GALLOP, bry.getVolume() * 0.15f, bry.getPitch());
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(AbstractHorse.JUMP_STRENGTH);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(53.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22499999403953552);
    }
    
    public int getMaxSpawnClusterSize() {
        return 6;
    }
    
    public int getMaxTemper() {
        return 100;
    }
    
    protected float getSoundVolume() {
        return 0.8f;
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }
    
    public void openInventory(final Player awg) {
        if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger(awg)) && this.isTamed()) {
            awg.openHorseInventory(this, this.inventory);
        }
    }
    
    protected boolean handleEating(final Player awg, final ItemStack bcj) {
        boolean boolean4 = false;
        float float5 = 0.0f;
        int integer6 = 0;
        int integer7 = 0;
        final Item bce8 = bcj.getItem();
        if (bce8 == Items.WHEAT) {
            float5 = 2.0f;
            integer6 = 20;
            integer7 = 3;
        }
        else if (bce8 == Items.SUGAR) {
            float5 = 1.0f;
            integer6 = 30;
            integer7 = 3;
        }
        else if (bce8 == Blocks.HAY_BLOCK.asItem()) {
            float5 = 20.0f;
            integer6 = 180;
        }
        else if (bce8 == Items.APPLE) {
            float5 = 3.0f;
            integer6 = 60;
            integer7 = 3;
        }
        else if (bce8 == Items.GOLDEN_CARROT) {
            float5 = 4.0f;
            integer6 = 60;
            integer7 = 5;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                boolean4 = true;
                this.setInLove(awg);
            }
        }
        else if (bce8 == Items.GOLDEN_APPLE || bce8 == Items.ENCHANTED_GOLDEN_APPLE) {
            float5 = 10.0f;
            integer6 = 240;
            integer7 = 10;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                boolean4 = true;
                this.setInLove(awg);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && float5 > 0.0f) {
            this.heal(float5);
            boolean4 = true;
        }
        if (this.isBaby() && integer6 > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.ageUp(integer6);
            }
            boolean4 = true;
        }
        if (integer7 > 0 && (boolean4 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            boolean4 = true;
            if (!this.level.isClientSide) {
                this.modifyTemper(integer7);
            }
        }
        if (boolean4) {
            this.eating();
        }
        return boolean4;
    }
    
    protected void doPlayerRide(final Player awg) {
        this.setEating(false);
        this.setStanding(false);
        if (!this.level.isClientSide) {
            awg.yRot = this.yRot;
            awg.xRot = this.xRot;
            awg.startRiding(this);
        }
    }
    
    protected boolean isImmobile() {
        return (super.isImmobile() && this.isVehicle() && this.isSaddled()) || this.isEating() || this.isStanding();
    }
    
    @Override
    public boolean isFood(final ItemStack bcj) {
        return false;
    }
    
    private void moveTail() {
        this.tailCounter = 1;
    }
    
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory == null) {
            return;
        }
        for (int integer2 = 0; integer2 < this.inventory.getContainerSize(); ++integer2) {
            final ItemStack bcj3 = this.inventory.getItem(integer2);
            if (!bcj3.isEmpty()) {
                this.spawnAtLocation(bcj3);
            }
        }
    }
    
    @Override
    public void aiStep() {
        if (this.random.nextInt(200) == 0) {
            this.moveTail();
        }
        super.aiStep();
        if (this.level.isClientSide || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.canEatGrass()) {
            if (!this.isEating() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level.getBlockState(new BlockPos(this).below()).getBlock() == Blocks.GRASS_BLOCK) {
                this.setEating(true);
            }
            if (this.isEating() && ++this.eatingCounter > 50) {
                this.eatingCounter = 0;
                this.setEating(false);
            }
        }
        this.followMommy();
    }
    
    protected void followMommy() {
        if (this.isBred() && this.isBaby() && !this.isEating()) {
            final LivingEntity aix2 = this.level.<LivingEntity>getNearestEntity((java.lang.Class<? extends LivingEntity>)AbstractHorse.class, AbstractHorse.MOMMY_TARGETING, (LivingEntity)this, this.x, this.y, this.z, this.getBoundingBox().inflate(16.0));
            if (aix2 != null && this.distanceToSqr(aix2) > 4.0) {
                this.navigation.createPath(aix2, 0);
            }
        }
    }
    
    public boolean canEatGrass() {
        return true;
    }
    
    public void tick() {
        super.tick();
        if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
            this.mouthCounter = 0;
            this.setFlag(64, false);
        }
        if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }
        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }
        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }
        this.eatAnimO = this.eatAnim;
        if (this.isEating()) {
            this.eatAnim += (1.0f - this.eatAnim) * 0.4f + 0.05f;
            if (this.eatAnim > 1.0f) {
                this.eatAnim = 1.0f;
            }
        }
        else {
            this.eatAnim += (0.0f - this.eatAnim) * 0.4f - 0.05f;
            if (this.eatAnim < 0.0f) {
                this.eatAnim = 0.0f;
            }
        }
        this.standAnimO = this.standAnim;
        if (this.isStanding()) {
            this.eatAnim = 0.0f;
            this.eatAnimO = this.eatAnim;
            this.standAnim += (1.0f - this.standAnim) * 0.4f + 0.05f;
            if (this.standAnim > 1.0f) {
                this.standAnim = 1.0f;
            }
        }
        else {
            this.allowStandSliding = false;
            this.standAnim += (0.8f * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6f - 0.05f;
            if (this.standAnim < 0.0f) {
                this.standAnim = 0.0f;
            }
        }
        this.mouthAnimO = this.mouthAnim;
        if (this.getFlag(64)) {
            this.mouthAnim += (1.0f - this.mouthAnim) * 0.7f + 0.05f;
            if (this.mouthAnim > 1.0f) {
                this.mouthAnim = 1.0f;
            }
        }
        else {
            this.mouthAnim += (0.0f - this.mouthAnim) * 0.7f - 0.05f;
            if (this.mouthAnim < 0.0f) {
                this.mouthAnim = 0.0f;
            }
        }
    }
    
    private void openMouth() {
        if (!this.level.isClientSide) {
            this.mouthCounter = 1;
            this.setFlag(64, true);
        }
    }
    
    public void setEating(final boolean boolean1) {
        this.setFlag(16, boolean1);
    }
    
    public void setStanding(final boolean boolean1) {
        if (boolean1) {
            this.setEating(false);
        }
        this.setFlag(32, boolean1);
    }
    
    private void stand() {
        if (this.isControlledByLocalInstance() || this.isEffectiveAi()) {
            this.standCounter = 1;
            this.setStanding(true);
        }
    }
    
    public void makeMad() {
        this.stand();
        final SoundEvent yo2 = this.getAngrySound();
        if (yo2 != null) {
            this.playSound(yo2, this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    public boolean tameWithName(final Player awg) {
        this.setOwnerUUID(awg.getUUID());
        this.setTamed(true);
        if (awg instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)awg, this);
        }
        this.level.broadcastEntityEvent(this, (byte)7);
        return true;
    }
    
    public void travel(final Vec3 csi) {
        if (!this.isAlive()) {
            return;
        }
        if (!this.isVehicle() || !this.canBeControlledByRider() || !this.isSaddled()) {
            this.flyingSpeed = 0.02f;
            super.travel(csi);
            return;
        }
        final LivingEntity aix3 = (LivingEntity)this.getControllingPassenger();
        this.yRot = aix3.yRot;
        this.yRotO = this.yRot;
        this.xRot = aix3.xRot * 0.5f;
        this.setRot(this.yRot, this.xRot);
        this.yBodyRot = this.yRot;
        this.yHeadRot = this.yBodyRot;
        float float4 = aix3.xxa * 0.5f;
        float float5 = aix3.zza;
        if (float5 <= 0.0f) {
            float5 *= 0.25f;
            this.gallopSoundCounter = 0;
        }
        if (this.onGround && this.playerJumpPendingScale == 0.0f && this.isStanding() && !this.allowStandSliding) {
            float4 = 0.0f;
            float5 = 0.0f;
        }
        if (this.playerJumpPendingScale > 0.0f && !this.isJumping() && this.onGround) {
            final double double6 = this.getCustomJump() * this.playerJumpPendingScale;
            double double7;
            if (this.hasEffect(MobEffects.JUMP)) {
                double7 = double6 + (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1f;
            }
            else {
                double7 = double6;
            }
            final Vec3 csi2 = this.getDeltaMovement();
            this.setDeltaMovement(csi2.x, double7, csi2.z);
            this.setIsJumping(true);
            this.hasImpulse = true;
            if (float5 > 0.0f) {
                final float float6 = Mth.sin(this.yRot * 0.017453292f);
                final float float7 = Mth.cos(this.yRot * 0.017453292f);
                this.setDeltaMovement(this.getDeltaMovement().add(-0.4f * float6 * this.playerJumpPendingScale, 0.0, 0.4f * float7 * this.playerJumpPendingScale));
                this.playJumpSound();
            }
            this.playerJumpPendingScale = 0.0f;
        }
        this.flyingSpeed = this.getSpeed() * 0.1f;
        if (this.isControlledByLocalInstance()) {
            this.setSpeed((float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            super.travel(new Vec3(float4, csi.y, float5));
        }
        else if (aix3 instanceof Player) {
            this.setDeltaMovement(Vec3.ZERO);
        }
        if (this.onGround) {
            this.playerJumpPendingScale = 0.0f;
            this.setIsJumping(false);
        }
        this.animationSpeedOld = this.animationSpeed;
        final double double6 = this.x - this.xo;
        double double7 = this.z - this.zo;
        float float8 = Mth.sqrt(double6 * double6 + double7 * double7) * 4.0f;
        if (float8 > 1.0f) {
            float8 = 1.0f;
        }
        this.animationSpeed += (float8 - this.animationSpeed) * 0.4f;
        this.animationPosition += this.animationSpeed;
    }
    
    protected void playJumpSound() {
        this.playSound(SoundEvents.HORSE_JUMP, 0.4f, 1.0f);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("EatingHaystack", this.isEating());
        id.putBoolean("Bred", this.isBred());
        id.putInt("Temper", this.getTemper());
        id.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            id.putString("OwnerUUID", this.getOwnerUUID().toString());
        }
        if (!this.inventory.getItem(0).isEmpty()) {
            id.put("SaddleItem", (Tag)this.inventory.getItem(0).save(new CompoundTag()));
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setEating(id.getBoolean("EatingHaystack"));
        this.setBred(id.getBoolean("Bred"));
        this.setTemper(id.getInt("Temper"));
        this.setTamed(id.getBoolean("Tame"));
        String string3;
        if (id.contains("OwnerUUID", 8)) {
            string3 = id.getString("OwnerUUID");
        }
        else {
            final String string4 = id.getString("Owner");
            string3 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string4);
        }
        if (!string3.isEmpty()) {
            this.setOwnerUUID(UUID.fromString(string3));
        }
        final AttributeInstance ajo4 = this.getAttributes().getInstance("Speed");
        if (ajo4 != null) {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ajo4.getBaseValue() * 0.25);
        }
        if (id.contains("SaddleItem", 10)) {
            final ItemStack bcj5 = ItemStack.of(id.getCompound("SaddleItem"));
            if (bcj5.getItem() == Items.SADDLE) {
                this.inventory.setItem(0, bcj5);
            }
        }
        this.updateEquipment();
    }
    
    @Override
    public boolean canMate(final Animal ara) {
        return false;
    }
    
    protected boolean canParent() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }
    
    @Nullable
    @Override
    public AgableMob getBreedOffspring(final AgableMob aim) {
        return null;
    }
    
    protected void setOffspringAttributes(final AgableMob aim, final AbstractHorse asb) {
        final double double4 = this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + aim.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + this.generateRandomMaxHealth();
        asb.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(double4 / 3.0);
        final double double5 = this.getAttribute(AbstractHorse.JUMP_STRENGTH).getBaseValue() + aim.getAttribute(AbstractHorse.JUMP_STRENGTH).getBaseValue() + this.generateRandomJumpStrength();
        asb.getAttribute(AbstractHorse.JUMP_STRENGTH).setBaseValue(double5 / 3.0);
        final double double6 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + aim.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.generateRandomSpeed();
        asb.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(double6 / 3.0);
    }
    
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }
    
    public float getEatAnim(final float float1) {
        return Mth.lerp(float1, this.eatAnimO, this.eatAnim);
    }
    
    public float getStandAnim(final float float1) {
        return Mth.lerp(float1, this.standAnimO, this.standAnim);
    }
    
    public float getMouthAnim(final float float1) {
        return Mth.lerp(float1, this.mouthAnimO, this.mouthAnim);
    }
    
    @Override
    public void onPlayerJump(int integer) {
        if (!this.isSaddled()) {
            return;
        }
        if (integer < 0) {
            integer = 0;
        }
        else {
            this.allowStandSliding = true;
            this.stand();
        }
        if (integer >= 90) {
            this.playerJumpPendingScale = 1.0f;
        }
        else {
            this.playerJumpPendingScale = 0.4f + 0.4f * integer / 90.0f;
        }
    }
    
    @Override
    public boolean canJump() {
        return this.isSaddled();
    }
    
    @Override
    public void handleStartJump(final int integer) {
        this.allowStandSliding = true;
        this.stand();
    }
    
    @Override
    public void handleStopJump() {
    }
    
    protected void spawnTamingParticles(final boolean boolean1) {
        final ParticleOptions gf3 = boolean1 ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int integer4 = 0; integer4 < 7; ++integer4) {
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            final double double7 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(gf3, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 0.5 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double5, double6, double7);
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 7) {
            this.spawnTamingParticles(true);
        }
        else if (byte1 == 6) {
            this.spawnTamingParticles(false);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public void positionRider(final Entity aio) {
        super.positionRider(aio);
        if (aio instanceof Mob) {
            final Mob aiy3 = (Mob)aio;
            this.yBodyRot = aiy3.yBodyRot;
        }
        if (this.standAnimO > 0.0f) {
            final float float3 = Mth.sin(this.yBodyRot * 0.017453292f);
            final float float4 = Mth.cos(this.yBodyRot * 0.017453292f);
            final float float5 = 0.7f * this.standAnimO;
            final float float6 = 0.15f * this.standAnimO;
            aio.setPos(this.x + float5 * float3, this.y + this.getRideHeight() + aio.getRidingHeight() + float6, this.z - float5 * float4);
            if (aio instanceof LivingEntity) {
                ((LivingEntity)aio).yBodyRot = this.yBodyRot;
            }
        }
    }
    
    protected float generateRandomMaxHealth() {
        return 15.0f + this.random.nextInt(8) + this.random.nextInt(9);
    }
    
    protected double generateRandomJumpStrength() {
        return 0.4000000059604645 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
    }
    
    protected double generateRandomSpeed() {
        return (0.44999998807907104 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
    }
    
    public boolean onLadder() {
        return false;
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * 0.95f;
    }
    
    public boolean wearsArmor() {
        return false;
    }
    
    public boolean isArmor(final ItemStack bcj) {
        return false;
    }
    
    public boolean setSlot(final int integer, final ItemStack bcj) {
        final int integer2 = integer - 400;
        if (integer2 >= 0 && integer2 < 2 && integer2 < this.inventory.getContainerSize()) {
            if (integer2 == 0 && bcj.getItem() != Items.SADDLE) {
                return false;
            }
            if (integer2 == 1 && (!this.wearsArmor() || !this.isArmor(bcj))) {
                return false;
            }
            this.inventory.setItem(integer2, bcj);
            this.updateEquipment();
            return true;
        }
        else {
            final int integer3 = integer - 500 + 2;
            if (integer3 >= 2 && integer3 < this.inventory.getContainerSize()) {
                this.inventory.setItem(integer3, bcj);
                return true;
            }
            return false;
        }
    }
    
    @Nullable
    public Entity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        return (Entity)this.getPassengers().get(0);
    }
    
    @Nullable
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        if (this.random.nextInt(5) == 0) {
            this.setAge(-24000);
        }
        return ajj;
    }
    
    static {
        PARENT_HORSE_SELECTOR = (aix -> aix instanceof AbstractHorse && ((AbstractHorse)aix).isBred());
        MOMMY_TARGETING = new TargetingConditions().range(16.0).allowInvulnerable().allowSameTeam().allowUnseeable().selector(AbstractHorse.PARENT_HORSE_SELECTOR);
        JUMP_STRENGTH = new RangedAttribute((Attribute)null, "horse.jumpStrength", 0.7, 0.0, 2.0).importLegacyName("Jump Strength").setSyncable(true);
        DATA_ID_FLAGS = SynchedEntityData.<Byte>defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
        DATA_ID_OWNER_UUID = SynchedEntityData.<Optional<UUID>>defineId(AbstractHorse.class, EntityDataSerializers.OPTIONAL_UUID);
    }
}
