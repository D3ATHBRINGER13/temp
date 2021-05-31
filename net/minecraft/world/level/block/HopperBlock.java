package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class HopperBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty ENABLED;
    private static final VoxelShape TOP;
    private static final VoxelShape FUNNEL;
    private static final VoxelShape CONVEX_BASE;
    private static final VoxelShape BASE;
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private static final VoxelShape DOWN_INTERACTION_SHAPE;
    private static final VoxelShape EAST_INTERACTION_SHAPE;
    private static final VoxelShape NORTH_INTERACTION_SHAPE;
    private static final VoxelShape SOUTH_INTERACTION_SHAPE;
    private static final VoxelShape WEST_INTERACTION_SHAPE;
    
    public HopperBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)HopperBlock.FACING, Direction.DOWN)).<Comparable, Boolean>setValue((Property<Comparable>)HopperBlock.ENABLED, true));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Direction>getValue((Property<Direction>)HopperBlock.FACING)) {
            case DOWN: {
                return HopperBlock.DOWN_SHAPE;
            }
            case NORTH: {
                return HopperBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return HopperBlock.SOUTH_SHAPE;
            }
            case WEST: {
                return HopperBlock.WEST_SHAPE;
            }
            case EAST: {
                return HopperBlock.EAST_SHAPE;
            }
            default: {
                return HopperBlock.BASE;
            }
        }
    }
    
    @Override
    public VoxelShape getInteractionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        switch (bvt.<Direction>getValue((Property<Direction>)HopperBlock.FACING)) {
            case DOWN: {
                return HopperBlock.DOWN_INTERACTION_SHAPE;
            }
            case NORTH: {
                return HopperBlock.NORTH_INTERACTION_SHAPE;
            }
            case SOUTH: {
                return HopperBlock.SOUTH_INTERACTION_SHAPE;
            }
            case WEST: {
                return HopperBlock.WEST_INTERACTION_SHAPE;
            }
            case EAST: {
                return HopperBlock.EAST_INTERACTION_SHAPE;
            }
            default: {
                return Hopper.INSIDE;
            }
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final Direction fb3 = ban.getClickedFace().getOpposite();
        return (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)HopperBlock.FACING, (fb3.getAxis() == Direction.Axis.Y) ? Direction.DOWN : fb3)).<Comparable, Boolean>setValue((Property<Comparable>)HopperBlock.ENABLED, true);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new HopperBlockEntity();
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof HopperBlockEntity) {
                ((HopperBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        this.checkPoweredState(bhr, ew, bvt1);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof HopperBlockEntity) {
            awg.openMenu((MenuProvider)btw8);
            awg.awardStat(Stats.INSPECT_HOPPER);
        }
        return true;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        this.checkPoweredState(bhr, ew3, bvt);
    }
    
    private void checkPoweredState(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final boolean boolean5 = !bhr.hasNeighborSignal(ew);
        if (boolean5 != bvt.<Boolean>getValue((Property<Boolean>)HopperBlock.ENABLED)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)HopperBlock.ENABLED, boolean5), 4);
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof HopperBlockEntity) {
            Containers.dropContents(bhr, ew, (Container)btw7);
            bhr.updateNeighbourForOutputSignal(ew, this);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(bhr.getBlockEntity(ew));
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT_MIPPED;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)HopperBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)HopperBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)HopperBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(HopperBlock.FACING, HopperBlock.ENABLED);
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        final BlockEntity btw6 = bhr.getBlockEntity(ew);
        if (btw6 instanceof HopperBlockEntity) {
            ((HopperBlockEntity)btw6).entityInside(aio);
        }
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = BlockStateProperties.FACING_HOPPER;
        ENABLED = BlockStateProperties.ENABLED;
        TOP = Block.box(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        FUNNEL = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        CONVEX_BASE = Shapes.or(HopperBlock.FUNNEL, HopperBlock.TOP);
        BASE = Shapes.join(HopperBlock.CONVEX_BASE, Hopper.INSIDE, BooleanOp.ONLY_FIRST);
        DOWN_SHAPE = Shapes.or(HopperBlock.BASE, Block.box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = Shapes.or(HopperBlock.BASE, Block.box(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = Shapes.or(HopperBlock.BASE, Block.box(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = Shapes.or(HopperBlock.BASE, Block.box(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = Shapes.or(HopperBlock.BASE, Block.box(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
        DOWN_INTERACTION_SHAPE = Hopper.INSIDE;
        EAST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}
