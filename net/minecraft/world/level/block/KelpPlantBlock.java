package net.minecraft.world.level.block;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockLayer;

public class KelpPlantBlock extends Block implements LiquidBlockContainer {
    private final KelpBlock top;
    
    protected KelpPlantBlock(final KelpBlock bps, final Properties c) {
        super(c);
        this.top = bps;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        return Fluids.WATER.getSource(false);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
        }
        super.tick(bvt, bhr, ew, random);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        if (fb == Direction.UP) {
            final Block bmv8 = bvt3.getBlock();
            if (bmv8 != this && bmv8 != this.top) {
                return this.top.getStateForPlacement(bhs);
            }
        }
        bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhu.getBlockState(ew2);
        final Block bmv7 = bvt2.getBlock();
        return bmv7 != Blocks.MAGMA_BLOCK && (bmv7 == this || bvt2.isFaceSturdy(bhu, ew2, Direction.UP));
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(Blocks.KELP);
    }
    
    @Override
    public boolean canPlaceLiquid(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final Fluid clj) {
        return false;
    }
    
    @Override
    public boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        return false;
    }
}
