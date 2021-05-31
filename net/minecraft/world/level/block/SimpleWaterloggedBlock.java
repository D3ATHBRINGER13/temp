package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface SimpleWaterloggedBlock extends BucketPickup, LiquidBlockContainer {
    default boolean canPlaceLiquid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        return !bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED) && clj == Fluids.WATER;
    }
    
    default boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED) && clk.getType() == Fluids.WATER) {
            if (!bhs.isClientSide()) {
                bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.WATERLOGGED, true), 3);
                bhs.getLiquidTicks().scheduleTick(ew, clk.getType(), clk.getType().getTickDelay(bhs));
            }
            return true;
        }
        return false;
    }
    
    default Fluid takeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED)) {
            bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.WATERLOGGED, false), 3);
            return Fluids.WATER;
        }
        return Fluids.EMPTY;
    }
}
