package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import java.util.function.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class EndPortalFrameBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty HAS_EYE;
    protected static final VoxelShape BASE_SHAPE;
    protected static final VoxelShape EYE_SHAPE;
    protected static final VoxelShape FULL_SHAPE;
    private static BlockPattern portalShape;
    
    public EndPortalFrameBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)EndPortalFrameBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, false));
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return bvt.<Boolean>getValue((Property<Boolean>)EndPortalFrameBlock.HAS_EYE) ? EndPortalFrameBlock.FULL_SHAPE : EndPortalFrameBlock.BASE_SHAPE;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)EndPortalFrameBlock.FACING, ban.getHorizontalDirection().getOpposite())).<Comparable, Boolean>setValue((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, false);
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (bvt.<Boolean>getValue((Property<Boolean>)EndPortalFrameBlock.HAS_EYE)) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)EndPortalFrameBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)EndPortalFrameBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)EndPortalFrameBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(EndPortalFrameBlock.FACING, EndPortalFrameBlock.HAS_EYE);
    }
    
    public static BlockPattern getOrCreatePortalShape() {
        if (EndPortalFrameBlock.portalShape == null) {
            EndPortalFrameBlock.portalShape = BlockPatternBuilder.start().aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?").where('?', BlockInWorld.hasState(BlockStatePredicate.ANY)).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).<Comparable>where((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, (Predicate<Object>)Predicates.equalTo(true)).where((Property<Comparable>)EndPortalFrameBlock.FACING, (Predicate<Object>)Predicates.equalTo(Direction.SOUTH)))).where('>', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).<Comparable>where((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, (Predicate<Object>)Predicates.equalTo(true)).where((Property<Comparable>)EndPortalFrameBlock.FACING, (Predicate<Object>)Predicates.equalTo(Direction.WEST)))).where('v', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).<Comparable>where((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, (Predicate<Object>)Predicates.equalTo(true)).where((Property<Comparable>)EndPortalFrameBlock.FACING, (Predicate<Object>)Predicates.equalTo(Direction.NORTH)))).where('<', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).<Comparable>where((Property<Comparable>)EndPortalFrameBlock.HAS_EYE, (Predicate<Object>)Predicates.equalTo(true)).where((Property<Comparable>)EndPortalFrameBlock.FACING, (Predicate<Object>)Predicates.equalTo(Direction.EAST)))).build();
        }
        return EndPortalFrameBlock.portalShape;
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        HAS_EYE = BlockStateProperties.EYE;
        BASE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
        EYE_SHAPE = Block.box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
        FULL_SHAPE = Shapes.or(EndPortalFrameBlock.BASE_SHAPE, EndPortalFrameBlock.EYE_SHAPE);
    }
}
