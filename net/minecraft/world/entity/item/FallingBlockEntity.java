package net.minecraft.world.entity.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.CrashReportCategory;
import net.minecraft.nbt.NbtUtils;
import java.util.List;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.BlockTags;
import com.google.common.collect.Lists;
import net.minecraft.util.Mth;
import net.minecraft.nbt.Tag;
import java.util.Iterator;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DirectionalPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;

public class FallingBlockEntity extends Entity {
    private BlockState blockState;
    public int time;
    public boolean dropItem;
    private boolean cancelDrop;
    private boolean hurtEntities;
    private int fallDamageMax;
    private float fallDamageAmount;
    public CompoundTag blockData;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS;
    
    public FallingBlockEntity(final EntityType<? extends FallingBlockEntity> ais, final Level bhr) {
        super(ais, bhr);
        this.blockState = Blocks.SAND.defaultBlockState();
        this.dropItem = true;
        this.fallDamageMax = 40;
        this.fallDamageAmount = 2.0f;
    }
    
    public FallingBlockEntity(final Level bhr, final double double2, final double double3, final double double4, final BlockState bvt) {
        this(EntityType.FALLING_BLOCK, bhr);
        this.blockState = bvt;
        this.blocksBuilding = true;
        this.setPos(double2, double3 + (1.0f - this.getBbHeight()) / 2.0f, double4);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = double2;
        this.yo = double3;
        this.zo = double4;
        this.setStartPos(new BlockPos(this));
    }
    
    @Override
    public boolean isAttackable() {
        return false;
    }
    
    public void setStartPos(final BlockPos ew) {
        this.entityData.<BlockPos>set(FallingBlockEntity.DATA_START_POS, ew);
    }
    
    public BlockPos getStartPos() {
        return this.entityData.<BlockPos>get(FallingBlockEntity.DATA_START_POS);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<BlockPos>define(FallingBlockEntity.DATA_START_POS, BlockPos.ZERO);
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.remove();
            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        final Block bmv2 = this.blockState.getBlock();
        if (this.time++ == 0) {
            final BlockPos ew3 = new BlockPos(this);
            if (this.level.getBlockState(ew3).getBlock() == bmv2) {
                this.level.removeBlock(ew3, false);
            }
            else if (!this.level.isClientSide) {
                this.remove();
                return;
            }
        }
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.level.isClientSide) {
            BlockPos ew3 = new BlockPos(this);
            final boolean boolean4 = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean boolean5 = boolean4 && this.level.getFluidState(ew3).is(FluidTags.WATER);
            final double double6 = this.getDeltaMovement().lengthSqr();
            if (boolean4 && double6 > 1.0) {
                final BlockHitResult csd8 = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), new Vec3(this.x, this.y, this.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                if (csd8.getType() != HitResult.Type.MISS && this.level.getFluidState(csd8.getBlockPos()).is(FluidTags.WATER)) {
                    ew3 = csd8.getBlockPos();
                    boolean5 = true;
                }
            }
            if (this.onGround || boolean5) {
                final BlockState bvt8 = this.level.getBlockState(ew3);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                if (bvt8.getBlock() != Blocks.MOVING_PISTON) {
                    this.remove();
                    if (!this.cancelDrop) {
                        final boolean boolean6 = bvt8.canBeReplaced(new DirectionalPlaceContext(this.level, ew3, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        final boolean boolean7 = this.blockState.canSurvive(this.level, ew3);
                        if (boolean6 && boolean7) {
                            if (this.blockState.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.WATERLOGGED) && this.level.getFluidState(ew3).getType() == Fluids.WATER) {
                                this.blockState = ((AbstractStateHolder<O, BlockState>)this.blockState).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.WATERLOGGED, true);
                            }
                            if (this.level.setBlock(ew3, this.blockState, 3)) {
                                if (bmv2 instanceof FallingBlock) {
                                    ((FallingBlock)bmv2).onLand(this.level, ew3, this.blockState, bvt8);
                                }
                                if (this.blockData != null && bmv2 instanceof EntityBlock) {
                                    final BlockEntity btw11 = this.level.getBlockEntity(ew3);
                                    if (btw11 != null) {
                                        final CompoundTag id12 = btw11.save(new CompoundTag());
                                        for (final String string14 : this.blockData.getAllKeys()) {
                                            final Tag iu15 = this.blockData.get(string14);
                                            if (!"x".equals(string14) && !"y".equals(string14)) {
                                                if ("z".equals(string14)) {
                                                    continue;
                                                }
                                                id12.put(string14, iu15.copy());
                                            }
                                        }
                                        btw11.load(id12);
                                        btw11.setChanged();
                                    }
                                }
                            }
                            else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.spawnAtLocation(bmv2);
                            }
                        }
                        else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(bmv2);
                        }
                    }
                    else if (bmv2 instanceof FallingBlock) {
                        ((FallingBlock)bmv2).onBroken(this.level, ew3);
                    }
                }
            }
            else if (!this.level.isClientSide && ((this.time > 100 && (ew3.getY() < 1 || ew3.getY() > 256)) || this.time > 600)) {
                if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(bmv2);
                }
                this.remove();
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
        if (this.hurtEntities) {
            final int integer4 = Mth.ceil(float1 - 1.0f);
            if (integer4 > 0) {
                final List<Entity> list5 = (List<Entity>)Lists.newArrayList((Iterable)this.level.getEntities(this, this.getBoundingBox()));
                final boolean boolean6 = this.blockState.is(BlockTags.ANVIL);
                final DamageSource ahx7 = boolean6 ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
                for (final Entity aio9 : list5) {
                    aio9.hurt(ahx7, (float)Math.min(Mth.floor(integer4 * this.fallDamageAmount), this.fallDamageMax));
                }
                if (boolean6 && this.random.nextFloat() < 0.05000000074505806 + integer4 * 0.05) {
                    final BlockState bvt8 = AnvilBlock.damage(this.blockState);
                    if (bvt8 == null) {
                        this.cancelDrop = true;
                    }
                    else {
                        this.blockState = bvt8;
                    }
                }
            }
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        id.put("BlockState", (Tag)NbtUtils.writeBlockState(this.blockState));
        id.putInt("Time", this.time);
        id.putBoolean("DropItem", this.dropItem);
        id.putBoolean("HurtEntities", this.hurtEntities);
        id.putFloat("FallHurtAmount", this.fallDamageAmount);
        id.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            id.put("TileEntityData", (Tag)this.blockData);
        }
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        this.blockState = NbtUtils.readBlockState(id.getCompound("BlockState"));
        this.time = id.getInt("Time");
        if (id.contains("HurtEntities", 99)) {
            this.hurtEntities = id.getBoolean("HurtEntities");
            this.fallDamageAmount = id.getFloat("FallHurtAmount");
            this.fallDamageMax = id.getInt("FallHurtMax");
        }
        else if (this.blockState.is(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }
        if (id.contains("DropItem", 99)) {
            this.dropItem = id.getBoolean("DropItem");
        }
        if (id.contains("TileEntityData", 10)) {
            this.blockData = id.getCompound("TileEntityData");
        }
        if (this.blockState.isAir()) {
            this.blockState = Blocks.SAND.defaultBlockState();
        }
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public void setHurtsEntities(final boolean boolean1) {
        this.hurtEntities = boolean1;
    }
    
    @Override
    public boolean displayFireAnimation() {
        return false;
    }
    
    @Override
    public void fillCrashReportCategory(final CrashReportCategory e) {
        super.fillCrashReportCategory(e);
        e.setDetail("Immitating BlockState", this.blockState.toString());
    }
    
    public BlockState getBlockState() {
        return this.blockState;
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
    }
    
    static {
        DATA_START_POS = SynchedEntityData.<BlockPos>defineId(FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
    }
}
