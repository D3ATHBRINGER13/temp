package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import java.util.stream.IntStream;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class StairBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<Half> HALF;
    public static final EnumProperty<StairsShape> SHAPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape TOP_AABB;
    protected static final VoxelShape BOTTOM_AABB;
    protected static final VoxelShape OCTET_NNN;
    protected static final VoxelShape OCTET_NNP;
    protected static final VoxelShape OCTET_NPN;
    protected static final VoxelShape OCTET_NPP;
    protected static final VoxelShape OCTET_PNN;
    protected static final VoxelShape OCTET_PNP;
    protected static final VoxelShape OCTET_PPN;
    protected static final VoxelShape OCTET_PPP;
    protected static final VoxelShape[] TOP_SHAPES;
    protected static final VoxelShape[] BOTTOM_SHAPES;
    private static final int[] SHAPE_BY_STATE;
    private final Block base;
    private final BlockState baseState;
    
    private static VoxelShape[] makeShapes(final VoxelShape ctc1, final VoxelShape ctc2, final VoxelShape ctc3, final VoxelShape ctc4, final VoxelShape ctc5) {
        return (VoxelShape[])IntStream.range(0, 16).mapToObj(integer -> makeStairShape(integer, ctc1, ctc2, ctc3, ctc4, ctc5)).toArray(VoxelShape[]::new);
    }
    
    private static VoxelShape makeStairShape(final int integer, final VoxelShape ctc2, final VoxelShape ctc3, final VoxelShape ctc4, final VoxelShape ctc5, final VoxelShape ctc6) {
        VoxelShape ctc7 = ctc2;
        if ((integer & 0x1) != 0x0) {
            ctc7 = Shapes.or(ctc7, ctc3);
        }
        if ((integer & 0x2) != 0x0) {
            ctc7 = Shapes.or(ctc7, ctc4);
        }
        if ((integer & 0x4) != 0x0) {
            ctc7 = Shapes.or(ctc7, ctc5);
        }
        if ((integer & 0x8) != 0x0) {
            ctc7 = Shapes.or(ctc7, ctc6);
        }
        return ctc7;
    }
    
    protected StairBlock(final BlockState bvt, final Properties c) {
        super(c);
        this.registerDefaultState((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)StairBlock.FACING, Direction.NORTH)).setValue(StairBlock.HALF, Half.BOTTOM)).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT)).<Comparable, Boolean>setValue((Property<Comparable>)StairBlock.WATERLOGGED, false));
        this.base = bvt.getBlock();
        this.baseState = bvt;
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return ((bvt.<Half>getValue(StairBlock.HALF) == Half.TOP) ? StairBlock.TOP_SHAPES : StairBlock.BOTTOM_SHAPES)[StairBlock.SHAPE_BY_STATE[this.getShapeIndex(bvt)]];
    }
    
    private int getShapeIndex(final BlockState bvt) {
        return bvt.<StairsShape>getValue(StairBlock.SHAPE).ordinal() * 4 + bvt.<Direction>getValue((Property<Direction>)StairBlock.FACING).get2DDataValue();
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        this.base.animateTick(bvt, bhr, ew, random);
    }
    
    @Override
    public void attack(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
        this.baseState.attack(bhr, ew, awg);
    }
    
    @Override
    public void destroy(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        this.base.destroy(bhs, ew, bvt);
    }
    
    @Override
    public float getExplosionResistance() {
        return this.base.getExplosionResistance();
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return this.base.getRenderLayer();
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return this.base.getTickDelay(bhu);
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt1.getBlock()) {
            return;
        }
        this.baseState.neighborChanged(bhr, ew, Blocks.AIR, ew, false);
        this.base.onPlace(this.baseState, bhr, ew, bvt4, false);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        this.baseState.onRemove(bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public void stepOn(final Level bhr, final BlockPos ew, final Entity aio) {
        this.base.stepOn(bhr, ew, aio);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        this.base.tick(bvt, bhr, ew, random);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        return this.baseState.use(bhr, awg, ahi, csd);
    }
    
    @Override
    public void wasExploded(final Level bhr, final BlockPos ew, final Explosion bhk) {
        this.base.wasExploded(bhr, ew, bhk);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Direction fb3 = ban.getClickedFace();
        final BlockPos ew4 = ban.getClickedPos();
        final FluidState clk5 = ban.getLevel().getFluidState(ew4);
        final BlockState bvt6 = ((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)StairBlock.FACING, ban.getHorizontalDirection())).setValue((Property<Comparable>)StairBlock.HALF, (fb3 == Direction.DOWN || (fb3 != Direction.UP && ban.getClickLocation().y - ew4.getY() > 0.5)) ? Half.TOP : Half.BOTTOM)).<Comparable, Boolean>setValue((Property<Comparable>)StairBlock.WATERLOGGED, clk5.getType() == Fluids.WATER);
        return ((AbstractStateHolder<O, BlockState>)bvt6).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, getStairsShape(bvt6, ban.getLevel(), ew4));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)StairBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb.getAxis().isHorizontal()) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, getStairsShape(bvt1, bhs, ew5));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    private static StairsShape getStairsShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final Direction fb4 = bvt.<Direction>getValue((Property<Direction>)StairBlock.FACING);
        final BlockState bvt2 = bhb.getBlockState(ew.relative(fb4));
        if (isStairs(bvt2) && bvt.<Half>getValue(StairBlock.HALF) == bvt2.<Half>getValue(StairBlock.HALF)) {
            final Direction fb5 = bvt2.<Direction>getValue((Property<Direction>)StairBlock.FACING);
            if (fb5.getAxis() != bvt.<Direction>getValue((Property<Direction>)StairBlock.FACING).getAxis() && canTakeShape(bvt, bhb, ew, fb5.getOpposite())) {
                if (fb5 == fb4.getCounterClockWise()) {
                    return StairsShape.OUTER_LEFT;
                }
                return StairsShape.OUTER_RIGHT;
            }
        }
        final BlockState bvt3 = bhb.getBlockState(ew.relative(fb4.getOpposite()));
        if (isStairs(bvt3) && bvt.<Half>getValue(StairBlock.HALF) == bvt3.<Half>getValue(StairBlock.HALF)) {
            final Direction fb6 = bvt3.<Direction>getValue((Property<Direction>)StairBlock.FACING);
            if (fb6.getAxis() != bvt.<Direction>getValue((Property<Direction>)StairBlock.FACING).getAxis() && canTakeShape(bvt, bhb, ew, fb6)) {
                if (fb6 == fb4.getCounterClockWise()) {
                    return StairsShape.INNER_LEFT;
                }
                return StairsShape.INNER_RIGHT;
            }
        }
        return StairsShape.STRAIGHT;
    }
    
    private static boolean canTakeShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockState bvt2 = bhb.getBlockState(ew.relative(fb));
        return !isStairs(bvt2) || bvt2.<Comparable>getValue((Property<Comparable>)StairBlock.FACING) != bvt.<Comparable>getValue((Property<Comparable>)StairBlock.FACING) || bvt2.<Half>getValue(StairBlock.HALF) != bvt.<Half>getValue(StairBlock.HALF);
    }
    
    public static boolean isStairs(final BlockState bvt) {
        return bvt.getBlock() instanceof StairBlock;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)StairBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)StairBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        final Direction fb4 = bvt.<Direction>getValue((Property<Direction>)StairBlock.FACING);
        final StairsShape bxa5 = bvt.<StairsShape>getValue(StairBlock.SHAPE);
        Label_0335: {
            switch (bqg) {
                case LEFT_RIGHT: {
                    if (fb4.getAxis() != Direction.Axis.Z) {
                        break;
                    }
                    switch (bxa5) {
                        case INNER_LEFT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
                        }
                        case INNER_RIGHT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT);
                        }
                        case OUTER_LEFT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT);
                        }
                        case OUTER_RIGHT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT);
                        }
                        default: {
                            return bvt.rotate(Rotation.CLOCKWISE_180);
                        }
                    }
                    break;
                }
                case FRONT_BACK: {
                    if (fb4.getAxis() != Direction.Axis.X) {
                        break;
                    }
                    switch (bxa5) {
                        case INNER_LEFT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT);
                        }
                        case INNER_RIGHT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT);
                        }
                        case OUTER_LEFT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT);
                        }
                        case OUTER_RIGHT: {
                            return ((AbstractStateHolder<O, BlockState>)bvt.rotate(Rotation.CLOCKWISE_180)).<StairsShape, StairsShape>setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT);
                        }
                        case STRAIGHT: {
                            return bvt.rotate(Rotation.CLOCKWISE_180);
                        }
                        default: {
                            break Label_0335;
                        }
                    }
                    break;
                }
            }
        }
        return super.mirror(bvt, bqg);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(StairBlock.FACING, StairBlock.HALF, StairBlock.SHAPE, StairBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)StairBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        HALF = BlockStateProperties.HALF;
        SHAPE = BlockStateProperties.STAIRS_SHAPE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        TOP_AABB = SlabBlock.TOP_AABB;
        BOTTOM_AABB = SlabBlock.BOTTOM_AABB;
        OCTET_NNN = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
        OCTET_NNP = Block.box(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
        OCTET_NPN = Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
        OCTET_NPP = Block.box(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
        OCTET_PNN = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
        OCTET_PNP = Block.box(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
        OCTET_PPN = Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
        OCTET_PPP = Block.box(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
        TOP_SHAPES = makeShapes(StairBlock.TOP_AABB, StairBlock.OCTET_NNN, StairBlock.OCTET_PNN, StairBlock.OCTET_NNP, StairBlock.OCTET_PNP);
        BOTTOM_SHAPES = makeShapes(StairBlock.BOTTOM_AABB, StairBlock.OCTET_NPN, StairBlock.OCTET_PPN, StairBlock.OCTET_NPP, StairBlock.OCTET_PPP);
        SHAPE_BY_STATE = new int[] { 12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8 };
    }
}
