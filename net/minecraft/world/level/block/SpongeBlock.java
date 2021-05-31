package net.minecraft.world.level.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import java.util.Queue;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SpongeBlock extends Block {
    protected SpongeBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        this.tryAbsorbWater(bhr, ew);
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        this.tryAbsorbWater(bhr, ew3);
        super.neighborChanged(bvt, bhr, ew3, bmv, ew5, boolean6);
    }
    
    protected void tryAbsorbWater(final Level bhr, final BlockPos ew) {
        if (this.removeWaterBreadthFirstSearch(bhr, ew)) {
            bhr.setBlock(ew, Blocks.WET_SPONGE.defaultBlockState(), 2);
            bhr.levelEvent(2001, ew, Block.getId(Blocks.WATER.defaultBlockState()));
        }
    }
    
    private boolean removeWaterBreadthFirstSearch(final Level bhr, final BlockPos ew) {
        final Queue<Tuple<BlockPos, Integer>> queue4 = (Queue<Tuple<BlockPos, Integer>>)Lists.newLinkedList();
        queue4.add(new Tuple(ew, 0));
        int integer5 = 0;
        while (!queue4.isEmpty()) {
            final Tuple<BlockPos, Integer> aaf6 = (Tuple<BlockPos, Integer>)queue4.poll();
            final BlockPos ew2 = aaf6.getA();
            final int integer6 = aaf6.getB();
            for (final Direction fb12 : Direction.values()) {
                final BlockPos ew3 = ew2.relative(fb12);
                final BlockState bvt14 = bhr.getBlockState(ew3);
                final FluidState clk15 = bhr.getFluidState(ew3);
                final Material clo16 = bvt14.getMaterial();
                if (clk15.is(FluidTags.WATER)) {
                    if (bvt14.getBlock() instanceof BucketPickup && ((BucketPickup)bvt14.getBlock()).takeLiquid(bhr, ew3, bvt14) != Fluids.EMPTY) {
                        ++integer5;
                        if (integer6 < 6) {
                            queue4.add(new Tuple(ew3, integer6 + 1));
                        }
                    }
                    else if (bvt14.getBlock() instanceof LiquidBlock) {
                        bhr.setBlock(ew3, Blocks.AIR.defaultBlockState(), 3);
                        ++integer5;
                        if (integer6 < 6) {
                            queue4.add(new Tuple(ew3, integer6 + 1));
                        }
                    }
                    else if (clo16 == Material.WATER_PLANT || clo16 == Material.REPLACEABLE_WATER_PLANT) {
                        final BlockEntity btw17 = bvt14.getBlock().isEntityBlock() ? bhr.getBlockEntity(ew3) : null;
                        Block.dropResources(bvt14, bhr, ew3, btw17);
                        bhr.setBlock(ew3, Blocks.AIR.defaultBlockState(), 3);
                        ++integer5;
                        if (integer6 < 6) {
                            queue4.add(new Tuple(ew3, integer6 + 1));
                        }
                    }
                }
            }
            if (integer5 > 64) {
                break;
            }
        }
        return integer5 > 0;
    }
}
