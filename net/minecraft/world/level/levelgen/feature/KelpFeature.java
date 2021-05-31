package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.KelpBlock;
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

public class KelpFeature extends Feature<NoneFeatureConfiguration> {
    public KelpFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        int integer7 = 0;
        final int integer8 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR, ew.getX(), ew.getZ());
        BlockPos ew2 = new BlockPos(ew.getX(), integer8, ew.getZ());
        if (bhs.getBlockState(ew2).getBlock() == Blocks.WATER) {
            final BlockState bvt10 = Blocks.KELP.defaultBlockState();
            final BlockState bvt11 = Blocks.KELP_PLANT.defaultBlockState();
            for (int integer9 = 1 + random.nextInt(10), integer10 = 0; integer10 <= integer9; ++integer10) {
                if (bhs.getBlockState(ew2).getBlock() == Blocks.WATER && bhs.getBlockState(ew2.above()).getBlock() == Blocks.WATER && bvt11.canSurvive(bhs, ew2)) {
                    if (integer10 == integer9) {
                        bhs.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)bvt10).<Comparable, Integer>setValue((Property<Comparable>)KelpBlock.AGE, random.nextInt(23)), 2);
                        ++integer7;
                    }
                    else {
                        bhs.setBlock(ew2, bvt11, 2);
                    }
                }
                else if (integer10 > 0) {
                    final BlockPos ew3 = ew2.below();
                    if (bvt10.canSurvive(bhs, ew3) && bhs.getBlockState(ew3.below()).getBlock() != Blocks.KELP) {
                        bhs.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)bvt10).<Comparable, Integer>setValue((Property<Comparable>)KelpBlock.AGE, random.nextInt(23)), 2);
                        ++integer7;
                        break;
                    }
                    break;
                }
                ew2 = ew2.above();
            }
        }
        return integer7 > 0;
    }
}
