package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.TallSeagrass;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SeagrassFeature extends Feature<SeagrassFeatureConfiguration> {
    public SeagrassFeature(final Function<Dynamic<?>, ? extends SeagrassFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final SeagrassFeatureConfiguration ced) {
        int integer7 = 0;
        for (int integer8 = 0; integer8 < ced.count; ++integer8) {
            final int integer9 = random.nextInt(8) - random.nextInt(8);
            final int integer10 = random.nextInt(8) - random.nextInt(8);
            final int integer11 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR, ew.getX() + integer9, ew.getZ() + integer10);
            final BlockPos ew2 = new BlockPos(ew.getX() + integer9, integer11, ew.getZ() + integer10);
            if (bhs.getBlockState(ew2).getBlock() == Blocks.WATER) {
                final boolean boolean13 = random.nextDouble() < ced.tallSeagrassProbability;
                final BlockState bvt14 = boolean13 ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
                if (bvt14.canSurvive(bhs, ew2)) {
                    if (boolean13) {
                        final BlockState bvt15 = ((AbstractStateHolder<O, BlockState>)bvt14).<DoubleBlockHalf, DoubleBlockHalf>setValue(TallSeagrass.HALF, DoubleBlockHalf.UPPER);
                        final BlockPos ew3 = ew2.above();
                        if (bhs.getBlockState(ew3).getBlock() == Blocks.WATER) {
                            bhs.setBlock(ew2, bvt14, 2);
                            bhs.setBlock(ew3, bvt15, 2);
                        }
                    }
                    else {
                        bhs.setBlock(ew2, bvt14, 2);
                    }
                    ++integer7;
                }
            }
        }
        return integer7 > 0;
    }
}
