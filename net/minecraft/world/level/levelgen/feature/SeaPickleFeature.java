package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SeaPickleFeature extends Feature<CountFeatureConfiguration> {
    public SeaPickleFeature(final Function<Dynamic<?>, ? extends CountFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<?> bxi, final Random random, final BlockPos ew, final CountFeatureConfiguration caq) {
        int integer7 = 0;
        for (int integer8 = 0; integer8 < caq.count; ++integer8) {
            final int integer9 = random.nextInt(8) - random.nextInt(8);
            final int integer10 = random.nextInt(8) - random.nextInt(8);
            final int integer11 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR, ew.getX() + integer9, ew.getZ() + integer10);
            final BlockPos ew2 = new BlockPos(ew.getX() + integer9, integer11, ew.getZ() + integer10);
            final BlockState bvt13 = ((AbstractStateHolder<O, BlockState>)Blocks.SEA_PICKLE.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (bhs.getBlockState(ew2).getBlock() == Blocks.WATER && bvt13.canSurvive(bhs, ew2)) {
                bhs.setBlock(ew2, bvt13, 2);
                ++integer7;
            }
        }
        return integer7 > 0;
    }
}
