package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BellBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    private static final EnumProperty<BellAttachType> ATTACHMENT;
    private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE;
    private static final VoxelShape EAST_WEST_FLOOR_SHAPE;
    private static final VoxelShape BELL_TOP_SHAPE;
    private static final VoxelShape BELL_BOTTOM_SHAPE;
    private static final VoxelShape BELL_SHAPE;
    private static final VoxelShape NORTH_SOUTH_BETWEEN;
    private static final VoxelShape EAST_WEST_BETWEEN;
    private static final VoxelShape TO_WEST;
    private static final VoxelShape TO_EAST;
    private static final VoxelShape TO_NORTH;
    private static final VoxelShape TO_SOUTH;
    private static final VoxelShape CEILING_SHAPE;
    
    public BellBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)BellBlock.FACING, Direction.NORTH)).<BellAttachType, BellAttachType>setValue(BellBlock.ATTACHMENT, BellAttachType.FLOOR));
    }
    
    @Override
    public void onProjectileHit(final Level bhr, final BlockState bvt, final BlockHitResult csd, final Entity aio) {
        if (aio instanceof AbstractArrow) {
            final Entity aio2 = ((AbstractArrow)aio).getOwner();
            final Player awg7 = (aio2 instanceof Player) ? ((Player)aio2) : null;
            this.onHit(bhr, bvt, bhr.getBlockEntity(csd.getBlockPos()), csd, awg7, true);
        }
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        return this.onHit(bhr, bvt, bhr.getBlockEntity(ew), csd, awg, true);
    }
    
    public boolean onHit(final Level bhr, final BlockState bvt, @Nullable final BlockEntity btw, final BlockHitResult csd, @Nullable final Player awg, final boolean boolean6) {
        final Direction fb8 = csd.getDirection();
        final BlockPos ew9 = csd.getBlockPos();
        final boolean boolean7 = !boolean6 || this.isProperHit(bvt, fb8, csd.getLocation().y - ew9.getY());
        if (!bhr.isClientSide && btw instanceof BellBlockEntity && boolean7) {
            ((BellBlockEntity)btw).onHit(fb8);
            this.ring(bhr, ew9);
            if (awg != null) {
                awg.awardStat(Stats.BELL_RING);
            }
            return true;
        }
        return true;
    }
    
    private boolean isProperHit(final BlockState bvt, final Direction fb, final double double3) {
        if (fb.getAxis() == Direction.Axis.Y || double3 > 0.8123999834060669) {
            return false;
        }
        final Direction fb2 = bvt.<Direction>getValue((Property<Direction>)BellBlock.FACING);
        final BellAttachType bwj7 = bvt.<BellAttachType>getValue(BellBlock.ATTACHMENT);
        switch (bwj7) {
            case FLOOR: {
                return fb2.getAxis() == fb.getAxis();
            }
            case SINGLE_WALL:
            case DOUBLE_WALL: {
                return fb2.getAxis() != fb.getAxis();
            }
            case CEILING: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void ring(final Level bhr, final BlockPos ew) {
        bhr.playSound(null, ew, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0f, 1.0f);
    }
    
    private VoxelShape getVoxelShape(final BlockState bvt) {
        final Direction fb3 = bvt.<Direction>getValue((Property<Direction>)BellBlock.FACING);
        final BellAttachType bwj4 = bvt.<BellAttachType>getValue(BellBlock.ATTACHMENT);
        if (bwj4 == BellAttachType.FLOOR) {
            if (fb3 == Direction.NORTH || fb3 == Direction.SOUTH) {
                return BellBlock.NORTH_SOUTH_FLOOR_SHAPE;
            }
            return BellBlock.EAST_WEST_FLOOR_SHAPE;
        }
        else {
            if (bwj4 == BellAttachType.CEILING) {
                return BellBlock.CEILING_SHAPE;
            }
            if (bwj4 == BellAttachType.DOUBLE_WALL) {
                if (fb3 == Direction.NORTH || fb3 == Direction.SOUTH) {
                    return BellBlock.NORTH_SOUTH_BETWEEN;
                }
                return BellBlock.EAST_WEST_BETWEEN;
            }
            else {
                if (fb3 == Direction.NORTH) {
                    return BellBlock.TO_NORTH;
                }
                if (fb3 == Direction.SOUTH) {
                    return BellBlock.TO_SOUTH;
                }
                if (fb3 == Direction.EAST) {
                    return BellBlock.TO_EAST;
                }
                return BellBlock.TO_WEST;
            }
        }
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.getVoxelShape(bvt);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.getVoxelShape(bvt);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Direction fb4 = ban.getClickedFace();
        final BlockPos ew5 = ban.getClickedPos();
        final Level bhr6 = ban.getLevel();
        final Direction.Axis a7 = fb4.getAxis();
        if (a7 == Direction.Axis.Y) {
            final BlockState bvt3 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)BellBlock.ATTACHMENT, (fb4 == Direction.DOWN) ? BellAttachType.CEILING : BellAttachType.FLOOR)).<Comparable, Direction>setValue((Property<Comparable>)BellBlock.FACING, ban.getHorizontalDirection());
            if (bvt3.canSurvive(ban.getLevel(), ew5)) {
                return bvt3;
            }
        }
        else {
            final boolean boolean8 = (a7 == Direction.Axis.X && bhr6.getBlockState(ew5.west()).isFaceSturdy(bhr6, ew5.west(), Direction.EAST) && bhr6.getBlockState(ew5.east()).isFaceSturdy(bhr6, ew5.east(), Direction.WEST)) || (a7 == Direction.Axis.Z && bhr6.getBlockState(ew5.north()).isFaceSturdy(bhr6, ew5.north(), Direction.SOUTH) && bhr6.getBlockState(ew5.south()).isFaceSturdy(bhr6, ew5.south(), Direction.NORTH));
            BlockState bvt3 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)BellBlock.FACING, fb4.getOpposite())).<Comparable, BellAttachType>setValue((Property<Comparable>)BellBlock.ATTACHMENT, boolean8 ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
            if (bvt3.canSurvive(ban.getLevel(), ban.getClickedPos())) {
                return bvt3;
            }
            final boolean boolean9 = bhr6.getBlockState(ew5.below()).isFaceSturdy(bhr6, ew5.below(), Direction.UP);
            bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, BellAttachType>setValue((Property<Comparable>)BellBlock.ATTACHMENT, boolean9 ? BellAttachType.FLOOR : BellAttachType.CEILING);
            if (bvt3.canSurvive(ban.getLevel(), ban.getClickedPos())) {
                return bvt3;
            }
        }
        return null;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final BellAttachType bwj8 = bvt1.<BellAttachType>getValue(BellBlock.ATTACHMENT);
        final Direction fb2 = getConnectedDirection(bvt1).getOpposite();
        if (fb2 == fb && !bvt1.canSurvive(bhs, ew5) && bwj8 != BellAttachType.DOUBLE_WALL) {
            return Blocks.AIR.defaultBlockState();
        }
        if (fb.getAxis() == bvt1.<Direction>getValue((Property<Direction>)BellBlock.FACING).getAxis()) {
            if (bwj8 == BellAttachType.DOUBLE_WALL && !bvt3.isFaceSturdy(bhs, ew6, fb)) {
                return (((AbstractStateHolder<O, BlockState>)bvt1).setValue(BellBlock.ATTACHMENT, BellAttachType.SINGLE_WALL)).<Comparable, Direction>setValue((Property<Comparable>)BellBlock.FACING, fb.getOpposite());
            }
            if (bwj8 == BellAttachType.SINGLE_WALL && fb2.getOpposite() == fb && bvt3.isFaceSturdy(bhs, ew6, bvt1.<Direction>getValue((Property<Direction>)BellBlock.FACING))) {
                return ((AbstractStateHolder<O, BlockState>)bvt1).<BellAttachType, BellAttachType>setValue(BellBlock.ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return FaceAttachedHorizontalDirectionalBlock.canAttach(bhu, ew, getConnectedDirection(bvt).getOpposite());
    }
    
    private static Direction getConnectedDirection(final BlockState bvt) {
        switch (bvt.<BellAttachType>getValue(BellBlock.ATTACHMENT)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
            default: {
                return bvt.<Direction>getValue((Property<Direction>)BellBlock.FACING).getOpposite();
            }
        }
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.DESTROY;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BellBlock.FACING, BellBlock.ATTACHMENT);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new BellBlockEntity();
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
        NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
        EAST_WEST_FLOOR_SHAPE = Block.box(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        BELL_TOP_SHAPE = Block.box(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
        BELL_BOTTOM_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
        BELL_SHAPE = Shapes.or(BellBlock.BELL_BOTTOM_SHAPE, BellBlock.BELL_TOP_SHAPE);
        NORTH_SOUTH_BETWEEN = Shapes.or(BellBlock.BELL_SHAPE, Block.box(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
        EAST_WEST_BETWEEN = Shapes.or(BellBlock.BELL_SHAPE, Block.box(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
        TO_WEST = Shapes.or(BellBlock.BELL_SHAPE, Block.box(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
        TO_EAST = Shapes.or(BellBlock.BELL_SHAPE, Block.box(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
        TO_NORTH = Shapes.or(BellBlock.BELL_SHAPE, Block.box(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
        TO_SOUTH = Shapes.or(BellBlock.BELL_SHAPE, Block.box(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
        CEILING_SHAPE = Shapes.or(BellBlock.BELL_SHAPE, Block.box(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));
    }
}
