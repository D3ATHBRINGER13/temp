package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HellFireFeature extends Feature<NoneFeatureConfiguration> {
    public HellFireFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (int integer7 = 0; integer7 < 64; ++integer7) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2)) {
                if (bhs.getBlockState(ew2.below()).getBlock() == Blocks.NETHERRACK) {
                    bhs.setBlock(ew2, Blocks.FIRE.defaultBlockState(), 2);
                }
            }
        }
        return true;
    }
}
