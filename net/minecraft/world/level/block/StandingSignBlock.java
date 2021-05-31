package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class StandingSignBlock extends SignBlock {
    public static final IntegerProperty ROTATION;
    
    public StandingSignBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)StandingSignBlock.ROTATION, 0)).<Comparable, Boolean>setValue((Property<Comparable>)StandingSignBlock.WATERLOGGED, false));
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return bhu.getBlockState(ew.below()).getMaterial().isSolid();
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        return (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)StandingSignBlock.ROTATION, Mth.floor((180.0f + ban.getRotation()) * 16.0f / 360.0f + 0.5) & 0xF)).<Comparable, Boolean>setValue((Property<Comparable>)StandingSignBlock.WATERLOGGED, clk3.getType() == Fluids.WATER);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !this.canSurvive(bvt1, bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)StandingSignBlock.ROTATION, brg.rotate(bvt.<Integer>getValue((Property<Integer>)StandingSignBlock.ROTATION), 16));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)StandingSignBlock.ROTATION, bqg.mirror(bvt.<Integer>getValue((Property<Integer>)StandingSignBlock.ROTATION), 16));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(StandingSignBlock.ROTATION, StandingSignBlock.WATERLOGGED);
    }
    
    static {
        ROTATION = BlockStateProperties.ROTATION_16;
    }
}
