package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.BlockPlaceContext;
import javax.annotation.Nullable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty OPEN;
    public static final EnumProperty<Half> HALF;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape EAST_OPEN_AABB;
    protected static final VoxelShape WEST_OPEN_AABB;
    protected static final VoxelShape SOUTH_OPEN_AABB;
    protected static final VoxelShape NORTH_OPEN_AABB;
    protected static final VoxelShape BOTTOM_AABB;
    protected static final VoxelShape TOP_AABB;
    
    protected TrapDoorBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)TrapDoorBlock.FACING, Direction.NORTH)).setValue((Property<Comparable>)TrapDoorBlock.OPEN, false)).setValue(TrapDoorBlock.HALF, Half.BOTTOM)).setValue((Property<Comparable>)TrapDoorBlock.POWERED, false)).<Comparable, Boolean>setValue((Property<Comparable>)TrapDoorBlock.WATERLOGGED, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.OPEN)) {
            return (bvt.<Half>getValue(TrapDoorBlock.HALF) == Half.TOP) ? TrapDoorBlock.TOP_AABB : TrapDoorBlock.BOTTOM_AABB;
        }
        switch (bvt.<Direction>getValue((Property<Direction>)TrapDoorBlock.FACING)) {
            default: {
                return TrapDoorBlock.NORTH_OPEN_AABB;
            }
            case SOUTH: {
                return TrapDoorBlock.SOUTH_OPEN_AABB;
            }
            case WEST: {
                return TrapDoorBlock.WEST_OPEN_AABB;
            }
            case EAST: {
                return TrapDoorBlock.EAST_OPEN_AABB;
            }
        }
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        switch (cns) {
            case LAND: {
                return bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.OPEN);
            }
            case WATER: {
                return bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.WATERLOGGED);
            }
            case AIR: {
                return bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.OPEN);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean use(BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (this.material == Material.METAL) {
            return false;
        }
        bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)TrapDoorBlock.OPEN);
        bhr.setBlock(ew, bvt, 2);
        if (bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.WATERLOGGED)) {
            bhr.getLiquidTicks().scheduleTick(ew, Fluids.WATER, Fluids.WATER.getTickDelay(bhr));
        }
        this.playSound(awg, bhr, ew, bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.OPEN));
        return true;
    }
    
    protected void playSound(@Nullable final Player awg, final Level bhr, final BlockPos ew, final boolean boolean4) {
        if (boolean4) {
            final int integer6 = (this.material == Material.METAL) ? 1037 : 1007;
            bhr.levelEvent(awg, integer6, ew, 0);
        }
        else {
            final int integer6 = (this.material == Material.METAL) ? 1036 : 1013;
            bhr.levelEvent(awg, integer6, ew, 0);
        }
    }
    
    @Override
    public void neighborChanged(BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        final boolean boolean7 = bhr.hasNeighborSignal(ew3);
        if (boolean7 != bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.POWERED)) {
            if (bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.OPEN) != boolean7) {
                bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)TrapDoorBlock.OPEN, boolean7);
                this.playSound(null, bhr, ew3, boolean7);
            }
            bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)TrapDoorBlock.POWERED, boolean7), 2);
            if (bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.WATERLOGGED)) {
                bhr.getLiquidTicks().scheduleTick(ew3, Fluids.WATER, Fluids.WATER.getTickDelay(bhr));
            }
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = this.defaultBlockState();
        final FluidState clk4 = ban.getLevel().getFluidState(ban.getClickedPos());
        final Direction fb5 = ban.getClickedFace();
        if (ban.replacingClickedOnBlock() || !fb5.getAxis().isHorizontal()) {
            bvt3 = (((AbstractStateHolder<O, BlockState>)bvt3).setValue((Property<Comparable>)TrapDoorBlock.FACING, ban.getHorizontalDirection().getOpposite())).<Comparable, Half>setValue((Property<Comparable>)TrapDoorBlock.HALF, (fb5 == Direction.UP) ? Half.BOTTOM : Half.TOP);
        }
        else {
            bvt3 = (((AbstractStateHolder<O, BlockState>)bvt3).setValue((Property<Comparable>)TrapDoorBlock.FACING, fb5)).<Comparable, Half>setValue((Property<Comparable>)TrapDoorBlock.HALF, (ban.getClickLocation().y - ban.getClickedPos().getY() > 0.5) ? Half.TOP : Half.BOTTOM);
        }
        if (ban.getLevel().hasNeighborSignal(ban.getClickedPos())) {
            bvt3 = (((AbstractStateHolder<O, BlockState>)bvt3).setValue((Property<Comparable>)TrapDoorBlock.OPEN, true)).<Comparable, Boolean>setValue((Property<Comparable>)TrapDoorBlock.POWERED, true);
        }
        return ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Boolean>setValue((Property<Comparable>)TrapDoorBlock.WATERLOGGED, clk4.getType() == Fluids.WATER);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(TrapDoorBlock.FACING, TrapDoorBlock.OPEN, TrapDoorBlock.HALF, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return false;
    }
    
    static {
        OPEN = BlockStateProperties.OPEN;
        HALF = BlockStateProperties.HALF;
        POWERED = BlockStateProperties.POWERED;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
        WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
        TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    }
}
