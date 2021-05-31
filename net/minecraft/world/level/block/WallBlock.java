package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class WallBlock extends CrossCollisionBlock {
    public static final BooleanProperty UP;
    private final VoxelShape[] shapeWithPostByIndex;
    private final VoxelShape[] collisionShapeWithPostByIndex;
    
    public WallBlock(final Properties c) {
        super(0.0f, 3.0f, 0.0f, 14.0f, 24.0f, c);
        this.registerDefaultState((((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)WallBlock.UP, true)).setValue((Property<Comparable>)WallBlock.NORTH, false)).setValue((Property<Comparable>)WallBlock.EAST, false)).setValue((Property<Comparable>)WallBlock.SOUTH, false)).setValue((Property<Comparable>)WallBlock.WEST, false)).<Comparable, Boolean>setValue((Property<Comparable>)WallBlock.WATERLOGGED, false));
        this.shapeWithPostByIndex = this.makeShapes(4.0f, 3.0f, 16.0f, 0.0f, 14.0f);
        this.collisionShapeWithPostByIndex = this.makeShapes(4.0f, 3.0f, 24.0f, 0.0f, 24.0f);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<Boolean>getValue((Property<Boolean>)WallBlock.UP)) {
            return this.shapeWithPostByIndex[this.getAABBIndex(bvt)];
        }
        return super.getShape(bvt, bhb, ew, csn);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<Boolean>getValue((Property<Boolean>)WallBlock.UP)) {
            return this.collisionShapeWithPostByIndex[this.getAABBIndex(bvt)];
        }
        return super.getCollisionShape(bvt, bhb, ew, csn);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    private boolean connectsTo(final BlockState bvt, final boolean boolean2, final Direction fb) {
        final Block bmv5 = bvt.getBlock();
        final boolean boolean3 = bmv5.is(BlockTags.WALLS) || (bmv5 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(bvt, fb));
        return (!Block.isExceptionForConnection(bmv5) && boolean2) || boolean3;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final LevelReader bhu3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        final FluidState clk5 = ban.getLevel().getFluidState(ban.getClickedPos());
        final BlockPos ew5 = ew4.north();
        final BlockPos ew6 = ew4.east();
        final BlockPos ew7 = ew4.south();
        final BlockPos ew8 = ew4.west();
        final BlockState bvt10 = bhu3.getBlockState(ew5);
        final BlockState bvt11 = bhu3.getBlockState(ew6);
        final BlockState bvt12 = bhu3.getBlockState(ew7);
        final BlockState bvt13 = bhu3.getBlockState(ew8);
        final boolean boolean14 = this.connectsTo(bvt10, bvt10.isFaceSturdy(bhu3, ew5, Direction.SOUTH), Direction.SOUTH);
        final boolean boolean15 = this.connectsTo(bvt11, bvt11.isFaceSturdy(bhu3, ew6, Direction.WEST), Direction.WEST);
        final boolean boolean16 = this.connectsTo(bvt12, bvt12.isFaceSturdy(bhu3, ew7, Direction.NORTH), Direction.NORTH);
        final boolean boolean17 = this.connectsTo(bvt13, bvt13.isFaceSturdy(bhu3, ew8, Direction.EAST), Direction.EAST);
        final boolean boolean18 = (!boolean14 || boolean15 || !boolean16 || boolean17) && (boolean14 || !boolean15 || boolean16 || !boolean17);
        return (((((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)WallBlock.UP, boolean18 || !bhu3.isEmptyBlock(ew4.above()))).setValue((Property<Comparable>)WallBlock.NORTH, boolean14)).setValue((Property<Comparable>)WallBlock.EAST, boolean15)).setValue((Property<Comparable>)WallBlock.SOUTH, boolean16)).setValue((Property<Comparable>)WallBlock.WEST, boolean17)).<Comparable, Boolean>setValue((Property<Comparable>)WallBlock.WATERLOGGED, clk5.getType() == Fluids.WATER);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)WallBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb == Direction.DOWN) {
            return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        }
        final Direction fb2 = fb.getOpposite();
        final boolean boolean9 = (fb == Direction.NORTH) ? this.connectsTo(bvt3, bvt3.isFaceSturdy(bhs, ew6, fb2), fb2) : bvt1.<Boolean>getValue((Property<Boolean>)WallBlock.NORTH);
        final boolean boolean10 = (fb == Direction.EAST) ? this.connectsTo(bvt3, bvt3.isFaceSturdy(bhs, ew6, fb2), fb2) : bvt1.<Boolean>getValue((Property<Boolean>)WallBlock.EAST);
        final boolean boolean11 = (fb == Direction.SOUTH) ? this.connectsTo(bvt3, bvt3.isFaceSturdy(bhs, ew6, fb2), fb2) : bvt1.<Boolean>getValue((Property<Boolean>)WallBlock.SOUTH);
        final boolean boolean12 = (fb == Direction.WEST) ? this.connectsTo(bvt3, bvt3.isFaceSturdy(bhs, ew6, fb2), fb2) : bvt1.<Boolean>getValue((Property<Boolean>)WallBlock.WEST);
        final boolean boolean13 = (!boolean9 || boolean10 || !boolean11 || boolean12) && (boolean9 || !boolean10 || boolean11 || !boolean12);
        return ((((((AbstractStateHolder<O, BlockState>)bvt1).setValue((Property<Comparable>)WallBlock.UP, boolean13 || !bhs.isEmptyBlock(ew5.above()))).setValue((Property<Comparable>)WallBlock.NORTH, boolean9)).setValue((Property<Comparable>)WallBlock.EAST, boolean10)).setValue((Property<Comparable>)WallBlock.SOUTH, boolean11)).<Comparable, Boolean>setValue((Property<Comparable>)WallBlock.WEST, boolean12);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(WallBlock.UP, WallBlock.NORTH, WallBlock.EAST, WallBlock.WEST, WallBlock.SOUTH, WallBlock.WATERLOGGED);
    }
    
    static {
        UP = BlockStateProperties.UP;
    }
}
