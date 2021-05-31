package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FenceGateBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty OPEN;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty IN_WALL;
    protected static final VoxelShape Z_SHAPE;
    protected static final VoxelShape X_SHAPE;
    protected static final VoxelShape Z_SHAPE_LOW;
    protected static final VoxelShape X_SHAPE_LOW;
    protected static final VoxelShape Z_COLLISION_SHAPE;
    protected static final VoxelShape X_COLLISION_SHAPE;
    protected static final VoxelShape Z_OCCLUSION_SHAPE;
    protected static final VoxelShape X_OCCLUSION_SHAPE;
    protected static final VoxelShape Z_OCCLUSION_SHAPE_LOW;
    protected static final VoxelShape X_OCCLUSION_SHAPE_LOW;
    
    public FenceGateBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)FenceGateBlock.OPEN, false)).setValue((Property<Comparable>)FenceGateBlock.POWERED, false)).<Comparable, Boolean>setValue((Property<Comparable>)FenceGateBlock.IN_WALL, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.IN_WALL)) {
            return (bvt.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.X_SHAPE_LOW : FenceGateBlock.Z_SHAPE_LOW;
        }
        return (bvt.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.X_SHAPE : FenceGateBlock.Z_SHAPE;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final Direction.Axis a8 = fb.getAxis();
        if (bvt1.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getClockWise().getAxis() == a8) {
            final boolean boolean9 = this.isWall(bvt3) || this.isWall(bhs.getBlockState(ew5.relative(fb.getOpposite())));
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)FenceGateBlock.IN_WALL, boolean9);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN)) {
            return Shapes.empty();
        }
        return (bvt.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.Z) ? FenceGateBlock.Z_COLLISION_SHAPE : FenceGateBlock.X_COLLISION_SHAPE;
    }
    
    @Override
    public VoxelShape getOcclusionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        if (bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.IN_WALL)) {
            return (bvt.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.X_OCCLUSION_SHAPE_LOW : FenceGateBlock.Z_OCCLUSION_SHAPE_LOW;
        }
        return (bvt.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.X_OCCLUSION_SHAPE : FenceGateBlock.Z_OCCLUSION_SHAPE;
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        switch (cns) {
            case LAND: {
                return bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Level bhr3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        final boolean boolean5 = bhr3.hasNeighborSignal(ew4);
        final Direction fb6 = ban.getHorizontalDirection();
        final Direction.Axis a7 = fb6.getAxis();
        final boolean boolean6 = (a7 == Direction.Axis.Z && (this.isWall(bhr3.getBlockState(ew4.west())) || this.isWall(bhr3.getBlockState(ew4.east())))) || (a7 == Direction.Axis.X && (this.isWall(bhr3.getBlockState(ew4.north())) || this.isWall(bhr3.getBlockState(ew4.south()))));
        return (((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)FenceGateBlock.FACING, fb6)).setValue((Property<Comparable>)FenceGateBlock.OPEN, boolean5)).setValue((Property<Comparable>)FenceGateBlock.POWERED, boolean5)).<Comparable, Boolean>setValue((Property<Comparable>)FenceGateBlock.IN_WALL, boolean6);
    }
    
    private boolean isWall(final BlockState bvt) {
        return bvt.getBlock().is(BlockTags.WALLS);
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN)) {
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)FenceGateBlock.OPEN, false);
            bhr.setBlock(ew, bvt, 10);
        }
        else {
            final Direction fb8 = awg.getDirection();
            if (bvt.<Comparable>getValue((Property<Comparable>)FenceGateBlock.FACING) == fb8.getOpposite()) {
                bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)FenceGateBlock.FACING, fb8);
            }
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)FenceGateBlock.OPEN, true);
            bhr.setBlock(ew, bvt, 10);
        }
        bhr.levelEvent(awg, ((boolean)bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN)) ? 1008 : 1014, ew, 0);
        return true;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        final boolean boolean7 = bhr.hasNeighborSignal(ew3);
        if (bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.POWERED) != boolean7) {
            bhr.setBlock(ew3, (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)FenceGateBlock.POWERED, boolean7)).<Comparable, Boolean>setValue((Property<Comparable>)FenceGateBlock.OPEN, boolean7), 2);
            if (bvt.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN) != boolean7) {
                bhr.levelEvent(null, boolean7 ? 1008 : 1014, ew3, 0);
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(FenceGateBlock.FACING, FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);
    }
    
    public static boolean connectsToDirection(final BlockState bvt, final Direction fb) {
        return bvt.<Direction>getValue((Property<Direction>)FenceGateBlock.FACING).getAxis() == fb.getClockWise().getAxis();
    }
    
    static {
        OPEN = BlockStateProperties.OPEN;
        POWERED = BlockStateProperties.POWERED;
        IN_WALL = BlockStateProperties.IN_WALL;
        Z_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
        X_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
        Z_SHAPE_LOW = Block.box(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
        X_SHAPE_LOW = Block.box(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
        Z_COLLISION_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
        X_COLLISION_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
        Z_OCCLUSION_SHAPE = Shapes.or(Block.box(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.box(14.0, 5.0, 7.0, 16.0, 16.0, 9.0));
        X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.box(7.0, 5.0, 14.0, 9.0, 16.0, 16.0));
        Z_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.box(14.0, 2.0, 7.0, 16.0, 13.0, 9.0));
        X_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.box(7.0, 2.0, 14.0, 9.0, 13.0, 16.0));
    }
}
