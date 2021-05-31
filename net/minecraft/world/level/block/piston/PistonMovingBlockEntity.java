package net.minecraft.world.level.block.piston;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PistonMovingBlockEntity extends BlockEntity implements TickableBlockEntity {
    private BlockState movedState;
    private Direction direction;
    private boolean extending;
    private boolean isSourcePiston;
    private static final ThreadLocal<Direction> NOCLIP;
    private float progress;
    private float progressO;
    private long lastTicked;
    
    public PistonMovingBlockEntity() {
        super(BlockEntityType.PISTON);
    }
    
    public PistonMovingBlockEntity(final BlockState bvt, final Direction fb, final boolean boolean3, final boolean boolean4) {
        this();
        this.movedState = bvt;
        this.direction = fb;
        this.extending = boolean3;
        this.isSourcePiston = boolean4;
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    public boolean isExtending() {
        return this.extending;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public boolean isSourcePiston() {
        return this.isSourcePiston;
    }
    
    public float getProgress(float float1) {
        if (float1 > 1.0f) {
            float1 = 1.0f;
        }
        return Mth.lerp(float1, this.progressO, this.progress);
    }
    
    public float getXOff(final float float1) {
        return this.direction.getStepX() * this.getExtendedProgress(this.getProgress(float1));
    }
    
    public float getYOff(final float float1) {
        return this.direction.getStepY() * this.getExtendedProgress(this.getProgress(float1));
    }
    
    public float getZOff(final float float1) {
        return this.direction.getStepZ() * this.getExtendedProgress(this.getProgress(float1));
    }
    
    private float getExtendedProgress(final float float1) {
        return this.extending ? (float1 - 1.0f) : (1.0f - float1);
    }
    
    private BlockState getCollisionRelatedBlockState() {
        if (!this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock) {
            return (((AbstractStateHolder<O, BlockState>)Blocks.PISTON_HEAD.defaultBlockState()).setValue((Property<Comparable>)PistonHeadBlock.TYPE, (this.movedState.getBlock() == Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)).<Comparable, Comparable>setValue((Property<Comparable>)PistonHeadBlock.FACING, (Comparable)this.movedState.<V>getValue((Property<V>)PistonBaseBlock.FACING));
        }
        return this.movedState;
    }
    
    private void moveCollidedEntities(final float float1) {
        final Direction fb3 = this.getMovementDirection();
        final double double4 = float1 - this.progress;
        final VoxelShape ctc6 = this.getCollisionRelatedBlockState().getCollisionShape(this.level, this.getBlockPos());
        if (ctc6.isEmpty()) {
            return;
        }
        final List<AABB> list7 = ctc6.toAabbs();
        final AABB csc8 = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list7));
        final List<Entity> list8 = this.level.getEntities(null, this.getMovementArea(csc8, fb3, double4).minmax(csc8));
        if (list8.isEmpty()) {
            return;
        }
        final boolean boolean10 = this.movedState.getBlock() == Blocks.SLIME_BLOCK;
        for (int integer11 = 0; integer11 < list8.size(); ++integer11) {
            final Entity aio12 = (Entity)list8.get(integer11);
            if (aio12.getPistonPushReaction() != PushReaction.IGNORE) {
                if (boolean10) {
                    final Vec3 csi13 = aio12.getDeltaMovement();
                    double double5 = csi13.x;
                    double double6 = csi13.y;
                    double double7 = csi13.z;
                    switch (fb3.getAxis()) {
                        case X: {
                            double5 = fb3.getStepX();
                            break;
                        }
                        case Y: {
                            double6 = fb3.getStepY();
                            break;
                        }
                        case Z: {
                            double7 = fb3.getStepZ();
                            break;
                        }
                    }
                    aio12.setDeltaMovement(double5, double6, double7);
                }
                double double8 = 0.0;
                for (int integer12 = 0; integer12 < list7.size(); ++integer12) {
                    final AABB csc9 = this.getMovementArea(this.moveByPositionAndProgress((AABB)list7.get(integer12)), fb3, double4);
                    final AABB csc10 = aio12.getBoundingBox();
                    if (csc9.intersects(csc10)) {
                        double8 = Math.max(double8, this.getMovement(csc9, fb3, csc10));
                        if (double8 >= double4) {
                            break;
                        }
                    }
                }
                if (double8 > 0.0) {
                    double8 = Math.min(double8, double4) + 0.01;
                    PistonMovingBlockEntity.NOCLIP.set(fb3);
                    aio12.move(MoverType.PISTON, new Vec3(double8 * fb3.getStepX(), double8 * fb3.getStepY(), double8 * fb3.getStepZ()));
                    PistonMovingBlockEntity.NOCLIP.set(null);
                    if (!this.extending && this.isSourcePiston) {
                        this.fixEntityWithinPistonBase(aio12, fb3, double4);
                    }
                }
            }
        }
    }
    
    public Direction getMovementDirection() {
        return this.extending ? this.direction : this.direction.getOpposite();
    }
    
    private AABB getMinMaxPiecesAABB(final List<AABB> list) {
        double double3 = 0.0;
        double double4 = 0.0;
        double double5 = 0.0;
        double double6 = 1.0;
        double double7 = 1.0;
        double double8 = 1.0;
        for (final AABB csc16 : list) {
            double3 = Math.min(csc16.minX, double3);
            double4 = Math.min(csc16.minY, double4);
            double5 = Math.min(csc16.minZ, double5);
            double6 = Math.max(csc16.maxX, double6);
            double7 = Math.max(csc16.maxY, double7);
            double8 = Math.max(csc16.maxZ, double8);
        }
        return new AABB(double3, double4, double5, double6, double7, double8);
    }
    
    private double getMovement(final AABB csc1, final Direction fb, final AABB csc3) {
        switch (fb.getAxis()) {
            case X: {
                return getDeltaX(csc1, fb, csc3);
            }
            default: {
                return getDeltaY(csc1, fb, csc3);
            }
            case Z: {
                return getDeltaZ(csc1, fb, csc3);
            }
        }
    }
    
    private AABB moveByPositionAndProgress(final AABB csc) {
        final double double3 = this.getExtendedProgress(this.progress);
        return csc.move(this.worldPosition.getX() + double3 * this.direction.getStepX(), this.worldPosition.getY() + double3 * this.direction.getStepY(), this.worldPosition.getZ() + double3 * this.direction.getStepZ());
    }
    
    private AABB getMovementArea(final AABB csc, final Direction fb, final double double3) {
        final double double4 = double3 * fb.getAxisDirection().getStep();
        final double double5 = Math.min(double4, 0.0);
        final double double6 = Math.max(double4, 0.0);
        switch (fb) {
            case WEST: {
                return new AABB(csc.minX + double5, csc.minY, csc.minZ, csc.minX + double6, csc.maxY, csc.maxZ);
            }
            case EAST: {
                return new AABB(csc.maxX + double5, csc.minY, csc.minZ, csc.maxX + double6, csc.maxY, csc.maxZ);
            }
            case DOWN: {
                return new AABB(csc.minX, csc.minY + double5, csc.minZ, csc.maxX, csc.minY + double6, csc.maxZ);
            }
            default: {
                return new AABB(csc.minX, csc.maxY + double5, csc.minZ, csc.maxX, csc.maxY + double6, csc.maxZ);
            }
            case NORTH: {
                return new AABB(csc.minX, csc.minY, csc.minZ + double5, csc.maxX, csc.maxY, csc.minZ + double6);
            }
            case SOUTH: {
                return new AABB(csc.minX, csc.minY, csc.maxZ + double5, csc.maxX, csc.maxY, csc.maxZ + double6);
            }
        }
    }
    
    private void fixEntityWithinPistonBase(final Entity aio, final Direction fb, final double double3) {
        final AABB csc6 = aio.getBoundingBox();
        final AABB csc7 = Shapes.block().bounds().move(this.worldPosition);
        if (csc6.intersects(csc7)) {
            final Direction fb2 = fb.getOpposite();
            double double4 = this.getMovement(csc7, fb2, csc6) + 0.01;
            final double double5 = this.getMovement(csc7, fb2, csc6.intersect(csc7)) + 0.01;
            if (Math.abs(double4 - double5) < 0.01) {
                double4 = Math.min(double4, double3) + 0.01;
                PistonMovingBlockEntity.NOCLIP.set(fb);
                aio.move(MoverType.PISTON, new Vec3(double4 * fb2.getStepX(), double4 * fb2.getStepY(), double4 * fb2.getStepZ()));
                PistonMovingBlockEntity.NOCLIP.set(null);
            }
        }
    }
    
    private static double getDeltaX(final AABB csc1, final Direction fb, final AABB csc3) {
        if (fb.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return csc1.maxX - csc3.minX;
        }
        return csc3.maxX - csc1.minX;
    }
    
    private static double getDeltaY(final AABB csc1, final Direction fb, final AABB csc3) {
        if (fb.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return csc1.maxY - csc3.minY;
        }
        return csc3.maxY - csc1.minY;
    }
    
    private static double getDeltaZ(final AABB csc1, final Direction fb, final AABB csc3) {
        if (fb.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return csc1.maxZ - csc3.minZ;
        }
        return csc3.maxZ - csc1.minZ;
    }
    
    public BlockState getMovedState() {
        return this.movedState;
    }
    
    public void finalTick() {
        if (this.progressO < 1.0f && this.level != null) {
            this.progress = 1.0f;
            this.progressO = this.progress;
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();
            if (this.level.getBlockState(this.worldPosition).getBlock() == Blocks.MOVING_PISTON) {
                BlockState bvt2;
                if (this.isSourcePiston) {
                    bvt2 = Blocks.AIR.defaultBlockState();
                }
                else {
                    bvt2 = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
                }
                this.level.setBlock(this.worldPosition, bvt2, 3);
                this.level.neighborChanged(this.worldPosition, bvt2.getBlock(), this.worldPosition);
            }
        }
    }
    
    @Override
    public void tick() {
        this.lastTicked = this.level.getGameTime();
        this.progressO = this.progress;
        if (this.progressO >= 1.0f) {
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();
            if (this.movedState != null && this.level.getBlockState(this.worldPosition).getBlock() == Blocks.MOVING_PISTON) {
                BlockState bvt2 = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
                if (bvt2.isAir()) {
                    this.level.setBlock(this.worldPosition, this.movedState, 84);
                    Block.updateOrDestroy(this.movedState, bvt2, this.level, this.worldPosition, 3);
                }
                else {
                    if (bvt2.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.WATERLOGGED) && bvt2.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED)) {
                        bvt2 = ((AbstractStateHolder<O, BlockState>)bvt2).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.WATERLOGGED, false);
                    }
                    this.level.setBlock(this.worldPosition, bvt2, 67);
                    this.level.neighborChanged(this.worldPosition, bvt2.getBlock(), this.worldPosition);
                }
            }
            return;
        }
        final float float2 = this.progress + 0.5f;
        this.moveCollidedEntities(float2);
        this.progress = float2;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
        }
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.movedState = NbtUtils.readBlockState(id.getCompound("blockState"));
        this.direction = Direction.from3DDataValue(id.getInt("facing"));
        this.progress = id.getFloat("progress");
        this.progressO = this.progress;
        this.extending = id.getBoolean("extending");
        this.isSourcePiston = id.getBoolean("source");
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.put("blockState", (Tag)NbtUtils.writeBlockState(this.movedState));
        id.putInt("facing", this.direction.get3DDataValue());
        id.putFloat("progress", this.progressO);
        id.putBoolean("extending", this.extending);
        id.putBoolean("source", this.isSourcePiston);
        return id;
    }
    
    public VoxelShape getCollisionShape(final BlockGetter bhb, final BlockPos ew) {
        VoxelShape ctc4;
        if (!this.extending && this.isSourcePiston) {
            ctc4 = ((AbstractStateHolder<O, BlockState>)this.movedState).<Comparable, Boolean>setValue((Property<Comparable>)PistonBaseBlock.EXTENDED, true).getCollisionShape(bhb, ew);
        }
        else {
            ctc4 = Shapes.empty();
        }
        final Direction fb5 = (Direction)PistonMovingBlockEntity.NOCLIP.get();
        if (this.progress < 1.0 && fb5 == this.getMovementDirection()) {
            return ctc4;
        }
        BlockState bvt6;
        if (this.isSourcePiston()) {
            bvt6 = (((AbstractStateHolder<O, BlockState>)Blocks.PISTON_HEAD.defaultBlockState()).setValue((Property<Comparable>)PistonHeadBlock.FACING, this.direction)).<Comparable, Boolean>setValue((Property<Comparable>)PistonHeadBlock.SHORT, this.extending != 1.0f - this.progress < 4.0f);
        }
        else {
            bvt6 = this.movedState;
        }
        final float float7 = this.getExtendedProgress(this.progress);
        final double double8 = this.direction.getStepX() * float7;
        final double double9 = this.direction.getStepY() * float7;
        final double double10 = this.direction.getStepZ() * float7;
        return Shapes.or(ctc4, bvt6.getCollisionShape(bhb, ew).move(double8, double9, double10));
    }
    
    public long getLastTicked() {
        return this.lastTicked;
    }
    
    static {
        NOCLIP = new ThreadLocal<Direction>() {
            protected Direction initialValue() {
                return null;
            }
        };
    }
}
