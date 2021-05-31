package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Seagrass extends BushBlock implements BonemealableBlock, LiquidBlockContainer {
    protected static final VoxelShape SHAPE;
    
    protected Seagrass(final Properties c) {
        super(c);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Seagrass.SHAPE;
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.isFaceSturdy(bhb, ew, Direction.UP) && bvt.getBlock() != Blocks.MAGMA_BLOCK;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        if (clk3.is(FluidTags.WATER) && clk3.getAmount() == 8) {
            return super.getStateForPlacement(ban);
        }
        return null;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final BlockState bvt4 = super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        if (!bvt4.isAir()) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return bvt4;
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        return Fluids.WATER.getSource(false);
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        final BlockState bvt2 = Blocks.TALL_SEAGRASS.defaultBlockState();
        final BlockState bvt3 = ((AbstractStateHolder<O, BlockState>)bvt2).<DoubleBlockHalf, DoubleBlockHalf>setValue(TallSeagrass.HALF, DoubleBlockHalf.UPPER);
        final BlockPos ew2 = ew.above();
        if (bhr.getBlockState(ew2).getBlock() == Blocks.WATER) {
            bhr.setBlock(ew, bvt2, 2);
            bhr.setBlock(ew2, bvt3, 2);
        }
    }
    
    @Override
    public boolean canPlaceLiquid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        return false;
    }
    
    @Override
    public boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        return false;
    }
    
    static {
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    }
}
