package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class LadderBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    
    protected LadderBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)LadderBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)LadderBlock.WATERLOGGED, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        switch (bvt.<Direction>getValue((Property<Direction>)LadderBlock.FACING)) {
            case NORTH: {
                return LadderBlock.NORTH_AABB;
            }
            case SOUTH: {
                return LadderBlock.SOUTH_AABB;
            }
            case WEST: {
                return LadderBlock.WEST_AABB;
            }
            default: {
                return LadderBlock.EAST_AABB;
            }
        }
    }
    
    private boolean canAttachTo(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockState bvt5 = bhb.getBlockState(ew);
        return !bvt5.isSignalSource() && bvt5.isFaceSturdy(bhb, ew, fb);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)LadderBlock.FACING);
        return this.canAttachTo(bhu, ew.relative(fb5.getOpposite()), fb5);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb.getOpposite() == bvt1.<Comparable>getValue((Property<Comparable>)LadderBlock.FACING) && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (bvt1.<Boolean>getValue((Property<Boolean>)LadderBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        if (!ban.replacingClickedOnBlock()) {
            final BlockState bvt3 = ban.getLevel().getBlockState(ban.getClickedPos().relative(ban.getClickedFace().getOpposite()));
            if (bvt3.getBlock() == this && bvt3.<Comparable>getValue((Property<Comparable>)LadderBlock.FACING) == ban.getClickedFace()) {
                return null;
            }
        }
        BlockState bvt3 = this.defaultBlockState();
        final LevelReader bhu4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        final FluidState clk6 = ban.getLevel().getFluidState(ban.getClickedPos());
        for (final Direction fb10 : ban.getNearestLookingDirections()) {
            if (fb10.getAxis().isHorizontal()) {
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)LadderBlock.FACING, fb10.getOpposite());
                if (bvt3.canSurvive(bhu4, ew5)) {
                    return ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Boolean>setValue((Property<Comparable>)LadderBlock.WATERLOGGED, clk6.getType() == Fluids.WATER);
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)LadderBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)LadderBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)LadderBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(LadderBlock.FACING, LadderBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)LadderBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
        WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    }
}
