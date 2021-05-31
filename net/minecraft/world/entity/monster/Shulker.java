package net.minecraft.world.entity.monster;

import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.Difficulty;
import java.util.EnumSet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import java.util.Random;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.AbstractArrow;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;
import net.minecraft.world.entity.animal.AbstractGolem;

public class Shulker extends AbstractGolem implements Enemy {
    private static final UUID COVERED_ARMOR_MODIFIER_UUID;
    private static final AttributeModifier COVERED_ARMOR_MODIFIER;
    protected static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID;
    protected static final EntityDataAccessor<Optional<BlockPos>> DATA_ATTACH_POS_ID;
    protected static final EntityDataAccessor<Byte> DATA_PEEK_ID;
    protected static final EntityDataAccessor<Byte> DATA_COLOR_ID;
    private float currentPeekAmountO;
    private float currentPeekAmount;
    private BlockPos oldAttachPosition;
    private int clientSideTeleportInterpolation;
    
    public Shulker(final EntityType<? extends Shulker> ais, final Level bhr) {
        super(ais, bhr);
        this.yBodyRotO = 180.0f;
        this.yBodyRot = 180.0f;
        this.oldAttachPosition = null;
        this.xpReward = 5;
    }
    
    @Nullable
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.yBodyRot = 180.0f;
        this.yBodyRotO = 180.0f;
        this.yRot = 180.0f;
        this.yRotO = 180.0f;
        this.yHeadRot = 180.0f;
        this.yHeadRotO = 180.0f;
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new ShulkerAttackGoal());
        this.goalSelector.addGoal(7, new ShulkerPeekGoal());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new ShulkerNearestAttackGoal(this));
        this.targetSelector.addGoal(3, new ShulkerDefenseAttackGoal(this));
    }
    
    protected boolean makeStepSound() {
        return false;
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHULKER_AMBIENT;
    }
    
    public void playAmbientSound() {
        if (!this.isClosed()) {
            super.playAmbientSound();
        }
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        if (this.isClosed()) {
            return SoundEvents.SHULKER_HURT_CLOSED;
        }
        return SoundEvents.SHULKER_HURT;
    }
    
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Direction>define(Shulker.DATA_ATTACH_FACE_ID, Direction.DOWN);
        this.entityData.<Optional<BlockPos>>define(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.empty());
        this.entityData.<Byte>define(Shulker.DATA_PEEK_ID, (Byte)0);
        this.entityData.<Byte>define(Shulker.DATA_COLOR_ID, (Byte)16);
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
    }
    
    protected BodyRotationControl createBodyControl() {
        return new ShulkerBodyRotationControl(this);
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.entityData.<Direction>set(Shulker.DATA_ATTACH_FACE_ID, Direction.from3DDataValue(id.getByte("AttachFace")));
        this.entityData.<Byte>set(Shulker.DATA_PEEK_ID, id.getByte("Peek"));
        this.entityData.<Byte>set(Shulker.DATA_COLOR_ID, id.getByte("Color"));
        if (id.contains("APX")) {
            final int integer3 = id.getInt("APX");
            final int integer4 = id.getInt("APY");
            final int integer5 = id.getInt("APZ");
            this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.of(new BlockPos(integer3, integer4, integer5)));
        }
        else {
            this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.empty());
        }
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putByte("AttachFace", (byte)this.entityData.<Direction>get(Shulker.DATA_ATTACH_FACE_ID).get3DDataValue());
        id.putByte("Peek", (byte)this.entityData.<Byte>get(Shulker.DATA_PEEK_ID));
        id.putByte("Color", (byte)this.entityData.<Byte>get(Shulker.DATA_COLOR_ID));
        final BlockPos ew3 = this.getAttachPosition();
        if (ew3 != null) {
            id.putInt("APX", ew3.getX());
            id.putInt("APY", ew3.getY());
            id.putInt("APZ", ew3.getZ());
        }
    }
    
    public void tick() {
        super.tick();
        BlockPos ew2 = (BlockPos)this.entityData.<Optional<BlockPos>>get(Shulker.DATA_ATTACH_POS_ID).orElse(null);
        if (ew2 == null && !this.level.isClientSide) {
            ew2 = new BlockPos(this);
            this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.of(ew2));
        }
        if (this.isPassenger()) {
            ew2 = null;
            final float float3 = this.getVehicle().yRot;
            this.yRot = float3;
            this.yBodyRot = float3;
            this.yBodyRotO = float3;
            this.clientSideTeleportInterpolation = 0;
        }
        else if (!this.level.isClientSide) {
            final BlockState bvt3 = this.level.getBlockState(ew2);
            if (!bvt3.isAir()) {
                if (bvt3.getBlock() == Blocks.MOVING_PISTON) {
                    final Direction fb4 = bvt3.<Direction>getValue((Property<Direction>)PistonBaseBlock.FACING);
                    if (this.level.isEmptyBlock(ew2.relative(fb4))) {
                        ew2 = ew2.relative(fb4);
                        this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.of(ew2));
                    }
                    else {
                        this.teleportSomewhere();
                    }
                }
                else if (bvt3.getBlock() == Blocks.PISTON_HEAD) {
                    final Direction fb4 = bvt3.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING);
                    if (this.level.isEmptyBlock(ew2.relative(fb4))) {
                        ew2 = ew2.relative(fb4);
                        this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.of(ew2));
                    }
                    else {
                        this.teleportSomewhere();
                    }
                }
                else {
                    this.teleportSomewhere();
                }
            }
            BlockPos ew3 = ew2.relative(this.getAttachFace());
            if (!this.level.loadedAndEntityCanStandOn(ew3, this)) {
                boolean boolean5 = false;
                for (final Direction fb5 : Direction.values()) {
                    ew3 = ew2.relative(fb5);
                    if (this.level.loadedAndEntityCanStandOn(ew3, this)) {
                        this.entityData.<Direction>set(Shulker.DATA_ATTACH_FACE_ID, fb5);
                        boolean5 = true;
                        break;
                    }
                }
                if (!boolean5) {
                    this.teleportSomewhere();
                }
            }
            final BlockPos ew4 = ew2.relative(this.getAttachFace().getOpposite());
            if (this.level.loadedAndEntityCanStandOn(ew4, this)) {
                this.teleportSomewhere();
            }
        }
        final float float3 = this.getRawPeekAmount() * 0.01f;
        this.currentPeekAmountO = this.currentPeekAmount;
        if (this.currentPeekAmount > float3) {
            this.currentPeekAmount = Mth.clamp(this.currentPeekAmount - 0.05f, float3, 1.0f);
        }
        else if (this.currentPeekAmount < float3) {
            this.currentPeekAmount = Mth.clamp(this.currentPeekAmount + 0.05f, 0.0f, float3);
        }
        if (ew2 != null) {
            if (this.level.isClientSide) {
                if (this.clientSideTeleportInterpolation > 0 && this.oldAttachPosition != null) {
                    --this.clientSideTeleportInterpolation;
                }
                else {
                    this.oldAttachPosition = ew2;
                }
            }
            this.x = ew2.getX() + 0.5;
            this.y = ew2.getY();
            this.z = ew2.getZ() + 0.5;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.xOld = this.x;
            this.yOld = this.y;
            this.zOld = this.z;
            final double double4 = 0.5 - Mth.sin((0.5f + this.currentPeekAmount) * 3.1415927f) * 0.5;
            final double double5 = 0.5 - Mth.sin((0.5f + this.currentPeekAmountO) * 3.1415927f) * 0.5;
            final Direction fb6 = this.getAttachFace().getOpposite();
            this.setBoundingBox(new AABB(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5).expandTowards(fb6.getStepX() * double4, fb6.getStepY() * double4, fb6.getStepZ() * double4));
            final double double6 = double4 - double5;
            if (double6 > 0.0) {
                final List<Entity> list11 = this.level.getEntities(this, this.getBoundingBox());
                if (!list11.isEmpty()) {
                    for (final Entity aio13 : list11) {
                        if (!(aio13 instanceof Shulker) && !aio13.noPhysics) {
                            aio13.move(MoverType.SHULKER, new Vec3(double6 * fb6.getStepX(), double6 * fb6.getStepY(), double6 * fb6.getStepZ()));
                        }
                    }
                }
            }
        }
    }
    
    public void move(final MoverType ajc, final Vec3 csi) {
        if (ajc == MoverType.SHULKER_BOX) {
            this.teleportSomewhere();
        }
        else {
            super.move(ajc, csi);
        }
    }
    
    public void setPos(final double double1, final double double2, final double double3) {
        super.setPos(double1, double2, double3);
        if (this.entityData == null || this.tickCount == 0) {
            return;
        }
        final Optional<BlockPos> optional8 = this.entityData.<Optional<BlockPos>>get(Shulker.DATA_ATTACH_POS_ID);
        final Optional<BlockPos> optional9 = (Optional<BlockPos>)Optional.of(new BlockPos(double1, double2, double3));
        if (!optional9.equals(optional8)) {
            this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, optional9);
            this.entityData.<Byte>set(Shulker.DATA_PEEK_ID, (Byte)0);
            this.hasImpulse = true;
        }
    }
    
    protected boolean teleportSomewhere() {
        if (this.isNoAi() || !this.isAlive()) {
            return true;
        }
        final BlockPos ew2 = new BlockPos(this);
        for (int integer3 = 0; integer3 < 5; ++integer3) {
            final BlockPos ew3 = ew2.offset(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if (ew3.getY() > 0 && this.level.isEmptyBlock(ew3) && this.level.getWorldBorder().isWithinBounds(ew3) && this.level.noCollision(this, new AABB(ew3))) {
                boolean boolean5 = false;
                for (final Direction fb9 : Direction.values()) {
                    if (this.level.loadedAndEntityCanStandOn(ew3.relative(fb9), this)) {
                        this.entityData.<Direction>set(Shulker.DATA_ATTACH_FACE_ID, fb9);
                        boolean5 = true;
                        break;
                    }
                }
                if (boolean5) {
                    this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0f, 1.0f);
                    this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.of(ew3));
                    this.entityData.<Byte>set(Shulker.DATA_PEEK_ID, (Byte)0);
                    this.setTarget(null);
                    return true;
                }
            }
        }
        return false;
    }
    
    public void aiStep() {
        super.aiStep();
        this.setDeltaMovement(Vec3.ZERO);
        this.yBodyRotO = 180.0f;
        this.yBodyRot = 180.0f;
        this.yRot = 180.0f;
    }
    
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (Shulker.DATA_ATTACH_POS_ID.equals(qk) && this.level.isClientSide && !this.isPassenger()) {
            final BlockPos ew3 = this.getAttachPosition();
            if (ew3 != null) {
                if (this.oldAttachPosition == null) {
                    this.oldAttachPosition = ew3;
                }
                else {
                    this.clientSideTeleportInterpolation = 6;
                }
                this.x = ew3.getX() + 0.5;
                this.y = ew3.getY();
                this.z = ew3.getZ() + 0.5;
                this.xo = this.x;
                this.yo = this.y;
                this.zo = this.z;
                this.xOld = this.x;
                this.yOld = this.y;
                this.zOld = this.z;
            }
        }
        super.onSyncedDataUpdated(qk);
    }
    
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
        this.lerpSteps = 0;
    }
    
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isClosed()) {
            final Entity aio4 = ahx.getDirectEntity();
            if (aio4 instanceof AbstractArrow) {
                return false;
            }
        }
        if (super.hurt(ahx, float2)) {
            if (this.getHealth() < this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
                this.teleportSomewhere();
            }
            return true;
        }
        return false;
    }
    
    private boolean isClosed() {
        return this.getRawPeekAmount() == 0;
    }
    
    @Nullable
    public AABB getCollideBox() {
        return this.isAlive() ? this.getBoundingBox() : null;
    }
    
    public Direction getAttachFace() {
        return this.entityData.<Direction>get(Shulker.DATA_ATTACH_FACE_ID);
    }
    
    @Nullable
    public BlockPos getAttachPosition() {
        return (BlockPos)this.entityData.<Optional<BlockPos>>get(Shulker.DATA_ATTACH_POS_ID).orElse(null);
    }
    
    public void setAttachPosition(@Nullable final BlockPos ew) {
        this.entityData.<Optional<BlockPos>>set(Shulker.DATA_ATTACH_POS_ID, (Optional<BlockPos>)Optional.ofNullable(ew));
    }
    
    public int getRawPeekAmount() {
        return this.entityData.<Byte>get(Shulker.DATA_PEEK_ID);
    }
    
    public void setRawPeekAmount(final int integer) {
        if (!this.level.isClientSide) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(Shulker.COVERED_ARMOR_MODIFIER);
            if (integer == 0) {
                this.getAttribute(SharedMonsterAttributes.ARMOR).addModifier(Shulker.COVERED_ARMOR_MODIFIER);
                this.playSound(SoundEvents.SHULKER_CLOSE, 1.0f, 1.0f);
            }
            else {
                this.playSound(SoundEvents.SHULKER_OPEN, 1.0f, 1.0f);
            }
        }
        this.entityData.<Byte>set(Shulker.DATA_PEEK_ID, (byte)integer);
    }
    
    public float getClientPeekAmount(final float float1) {
        return Mth.lerp(float1, this.currentPeekAmountO, this.currentPeekAmount);
    }
    
    public int getClientSideTeleportInterpolation() {
        return this.clientSideTeleportInterpolation;
    }
    
    public BlockPos getOldAttachPosition() {
        return this.oldAttachPosition;
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.5f;
    }
    
    public int getMaxHeadXRot() {
        return 180;
    }
    
    public int getMaxHeadYRot() {
        return 180;
    }
    
    public void push(final Entity aio) {
    }
    
    public float getPickRadius() {
        return 0.0f;
    }
    
    public boolean hasValidInterpolationPositions() {
        return this.oldAttachPosition != null && this.getAttachPosition() != null;
    }
    
    @Nullable
    public DyeColor getColor() {
        final Byte byte2 = this.entityData.<Byte>get(Shulker.DATA_COLOR_ID);
        if (byte2 == 16 || byte2 > 15) {
            return null;
        }
        return DyeColor.byId(byte2);
    }
    
    static {
        COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
        COVERED_ARMOR_MODIFIER = new AttributeModifier(Shulker.COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0, AttributeModifier.Operation.ADDITION).setSerialize(false);
        DATA_ATTACH_FACE_ID = SynchedEntityData.<Direction>defineId(Shulker.class, EntityDataSerializers.DIRECTION);
        DATA_ATTACH_POS_ID = SynchedEntityData.<Optional<BlockPos>>defineId(Shulker.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
        DATA_PEEK_ID = SynchedEntityData.<Byte>defineId(Shulker.class, EntityDataSerializers.BYTE);
        DATA_COLOR_ID = SynchedEntityData.<Byte>defineId(Shulker.class, EntityDataSerializers.BYTE);
    }
    
    class ShulkerBodyRotationControl extends BodyRotationControl {
        public ShulkerBodyRotationControl(final Mob aiy) {
            super(aiy);
        }
        
        @Override
        public void clientTick() {
        }
    }
    
    class ShulkerPeekGoal extends Goal {
        private int peekTime;
        
        private ShulkerPeekGoal() {
        }
        
        @Override
        public boolean canUse() {
            return Shulker.this.getTarget() == null && Shulker.this.random.nextInt(40) == 0;
        }
        
        @Override
        public boolean canContinueToUse() {
            return Shulker.this.getTarget() == null && this.peekTime > 0;
        }
        
        @Override
        public void start() {
            this.peekTime = 20 * (1 + Shulker.this.random.nextInt(3));
            Shulker.this.setRawPeekAmount(30);
        }
        
        @Override
        public void stop() {
            if (Shulker.this.getTarget() == null) {
                Shulker.this.setRawPeekAmount(0);
            }
        }
        
        @Override
        public void tick() {
            --this.peekTime;
        }
    }
    
    class ShulkerAttackGoal extends Goal {
        private int attackTime;
        
        public ShulkerAttackGoal() {
            this.setFlags((EnumSet<Flag>)EnumSet.of((Enum)Flag.MOVE, (Enum)Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            final LivingEntity aix2 = Shulker.this.getTarget();
            return aix2 != null && aix2.isAlive() && Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL;
        }
        
        @Override
        public void start() {
            this.attackTime = 20;
            Shulker.this.setRawPeekAmount(100);
        }
        
        @Override
        public void stop() {
            Shulker.this.setRawPeekAmount(0);
        }
        
        @Override
        public void tick() {
            if (Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            --this.attackTime;
            final LivingEntity aix2 = Shulker.this.getTarget();
            Shulker.this.getLookControl().setLookAt(aix2, 180.0f, 180.0f);
            final double double3 = Shulker.this.distanceToSqr(aix2);
            if (double3 < 400.0) {
                if (this.attackTime <= 0) {
                    this.attackTime = 20 + Shulker.this.random.nextInt(10) * 20 / 2;
                    Shulker.this.level.addFreshEntity(new ShulkerBullet(Shulker.this.level, Shulker.this, aix2, Shulker.this.getAttachFace().getAxis()));
                    Shulker.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0f, (Shulker.this.random.nextFloat() - Shulker.this.random.nextFloat()) * 0.2f + 1.0f);
                }
            }
            else {
                Shulker.this.setTarget(null);
            }
            super.tick();
        }
    }
    
    class ShulkerNearestAttackGoal extends NearestAttackableTargetGoal<Player> {
        public ShulkerNearestAttackGoal(final Shulker avb2) {
            super(avb2, Player.class, true);
        }
        
        @Override
        public boolean canUse() {
            return Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL && super.canUse();
        }
        
        @Override
        protected AABB getTargetSearchArea(final double double1) {
            final Direction fb4 = ((Shulker)this.mob).getAttachFace();
            if (fb4.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, double1, double1);
            }
            if (fb4.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().inflate(double1, double1, 4.0);
            }
            return this.mob.getBoundingBox().inflate(double1, 4.0, double1);
        }
    }
    
    static class ShulkerDefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
        public ShulkerDefenseAttackGoal(final Shulker avb) {
            super(avb, LivingEntity.class, 10, true, false, (Predicate<LivingEntity>)(aix -> aix instanceof Enemy));
        }
        
        @Override
        public boolean canUse() {
            return this.mob.getTeam() != null && super.canUse();
        }
        
        @Override
        protected AABB getTargetSearchArea(final double double1) {
            final Direction fb4 = ((Shulker)this.mob).getAttachFace();
            if (fb4.getAxis() == Direction.Axis.X) {
                return this.mob.getBoundingBox().inflate(4.0, double1, double1);
            }
            if (fb4.getAxis() == Direction.Axis.Z) {
                return this.mob.getBoundingBox().inflate(double1, double1, 4.0);
            }
            return this.mob.getBoundingBox().inflate(double1, 4.0, double1);
        }
    }
}
