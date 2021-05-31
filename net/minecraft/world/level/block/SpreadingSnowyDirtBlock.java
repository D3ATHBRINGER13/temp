package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SpreadingSnowyDirtBlock extends SnowyDirtBlock {
    protected SpreadingSnowyDirtBlock(final Properties c) {
        super(c);
    }
    
    private static boolean canBeGrass(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.above();
        final BlockState bvt2 = bhu.getBlockState(ew2);
        if (bvt2.getBlock() == Blocks.SNOW && bvt2.<Integer>getValue((Property<Integer>)SnowLayerBlock.LAYERS) == 1) {
            return true;
        }
        final int integer6 = LayerLightEngine.getLightBlockInto(bhu, bvt, ew, bvt2, ew2, Direction.UP, bvt2.getLightBlock(bhu, ew2));
        return integer6 < bhu.getMaxLightLevel();
    }
    
    private static boolean canPropagate(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockPos ew2 = ew.above();
        return canBeGrass(bvt, bhu, ew) && !bhu.getFluidState(ew2).is(FluidTags.WATER);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        if (!canBeGrass(bvt, bhr, ew)) {
            bhr.setBlockAndUpdate(ew, Blocks.DIRT.defaultBlockState());
            return;
        }
        if (bhr.getMaxLocalRawBrightness(ew.above()) >= 9) {
            final BlockState bvt2 = this.defaultBlockState();
            for (int integer7 = 0; integer7 < 4; ++integer7) {
                final BlockPos ew2 = ew.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (bhr.getBlockState(ew2).getBlock() == Blocks.DIRT && canPropagate(bvt2, bhr, ew2)) {
                    bhr.setBlockAndUpdate(ew2, ((AbstractStateHolder<O, BlockState>)bvt2).<Comparable, Boolean>setValue((Property<Comparable>)SpreadingSnowyDirtBlock.SNOWY, bhr.getBlockState(ew2.above()).getBlock() == Blocks.SNOW));
                }
            }
        }
    }
}
