package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.stream.Collector;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.Shapes;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.block.state.BlockState;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CrossCollisionBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    protected final VoxelShape[] collisionShapeByIndex;
    protected final VoxelShape[] shapeByIndex;
    private final Object2IntMap<BlockState> stateToIndex;
    
    protected CrossCollisionBlock(final float float1, final float float2, final float float3, final float float4, final float float5, final Properties c) {
        super(c);
        this.stateToIndex = (Object2IntMap<BlockState>)new Object2IntOpenHashMap();
        this.collisionShapeByIndex = this.makeShapes(float1, float2, float5, 0.0f, float5);
        this.shapeByIndex = this.makeShapes(float1, float2, float3, 0.0f, float4);
    }
    
    protected VoxelShape[] makeShapes(final float float1, final float float2, final float float3, final float float4, final float float5) {
        final float float6 = 8.0f - float1;
        final float float7 = 8.0f + float1;
        final float float8 = 8.0f - float2;
        final float float9 = 8.0f + float2;
        final VoxelShape ctc11 = Block.box(float6, 0.0, float6, float7, float3, float7);
        final VoxelShape ctc12 = Block.box(float8, float4, 0.0, float9, float5, float9);
        final VoxelShape ctc13 = Block.box(float8, float4, float8, float9, float5, 16.0);
        final VoxelShape ctc14 = Block.box(0.0, float4, float8, float9, float5, float9);
        final VoxelShape ctc15 = Block.box(float8, float4, float8, 16.0, float5, float9);
        final VoxelShape ctc16 = Shapes.or(ctc12, ctc15);
        final VoxelShape ctc17 = Shapes.or(ctc13, ctc14);
        final VoxelShape[] arr18 = { Shapes.empty(), ctc13, ctc14, ctc17, ctc12, Shapes.or(ctc13, ctc12), Shapes.or(ctc14, ctc12), Shapes.or(ctc17, ctc12), ctc15, Shapes.or(ctc13, ctc15), Shapes.or(ctc14, ctc15), Shapes.or(ctc17, ctc15), ctc16, Shapes.or(ctc13, ctc16), Shapes.or(ctc14, ctc16), Shapes.or(ctc17, ctc16) };
        for (int integer19 = 0; integer19 < 16; ++integer19) {
            arr18[integer19] = Shapes.or(ctc11, arr18[integer19]);
        }
        return arr18;
    }
    
    @Override
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return !bvt.<Boolean>getValue((Property<Boolean>)CrossCollisionBlock.WATERLOGGED);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.shapeByIndex[this.getAABBIndex(bvt)];
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.collisionShapeByIndex[this.getAABBIndex(bvt)];
    }
    
    private static int indexFor(final Direction fb) {
        return 1 << fb.get2DDataValue();
    }
    
    protected int getAABBIndex(final BlockState bvt) {
        return this.stateToIndex.computeIntIfAbsent(bvt, bvt -> {
            int integer2 = 0;
            if (bvt.<Boolean>getValue((Property<Boolean>)CrossCollisionBlock.NORTH)) {
                integer2 |= indexFor(Direction.NORTH);
            }
            if (bvt.<Boolean>getValue((Property<Boolean>)CrossCollisionBlock.EAST)) {
                integer2 |= indexFor(Direction.EAST);
            }
            if (bvt.<Boolean>getValue((Property<Boolean>)CrossCollisionBlock.SOUTH)) {
                integer2 |= indexFor(Direction.SOUTH);
            }
            if (bvt.<Boolean>getValue((Property<Boolean>)CrossCollisionBlock.WEST)) {
                integer2 |= indexFor(Direction.WEST);
            }
            return integer2;
        });
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)CrossCollisionBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        switch (brg) {
            case CLOCKWISE_180: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)CrossCollisionBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.SOUTH))).setValue((Property<Comparable>)CrossCollisionBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.WEST))).setValue((Property<Comparable>)CrossCollisionBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.NORTH))).<Comparable, Comparable>setValue((Property<Comparable>)CrossCollisionBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)CrossCollisionBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.EAST))).setValue((Property<Comparable>)CrossCollisionBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.SOUTH))).setValue((Property<Comparable>)CrossCollisionBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.WEST))).<Comparable, Comparable>setValue((Property<Comparable>)CrossCollisionBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.NORTH));
            }
            case CLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)CrossCollisionBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.WEST))).setValue((Property<Comparable>)CrossCollisionBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.NORTH))).setValue((Property<Comparable>)CrossCollisionBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.EAST))).<Comparable, Comparable>setValue((Property<Comparable>)CrossCollisionBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.SOUTH));
            }
            default: {
                return bvt;
            }
        }
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        switch (bqg) {
            case LEFT_RIGHT: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)CrossCollisionBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.SOUTH))).<Comparable, Comparable>setValue((Property<Comparable>)CrossCollisionBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)CrossCollisionBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.WEST))).<Comparable, Comparable>setValue((Property<Comparable>)CrossCollisionBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)CrossCollisionBlock.EAST));
            }
            default: {
                return super.mirror(bvt, bqg);
            }
        }
    }
    
    static {
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect((Collector)Util.toMap());
    }
}
