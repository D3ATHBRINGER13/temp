package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CactusFeature extends Feature<NoneFeatureConfiguration> {
    public CactusFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (int integer7 = 0; integer7 < 10; ++integer7) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2)) {
                for (int integer8 = 1 + random.nextInt(random.nextInt(3) + 1), integer9 = 0; integer9 < integer8; ++integer9) {
                    if (Blocks.CACTUS.defaultBlockState().canSurvive(bhs, ew2)) {
                        bhs.setBlock(ew2.above(integer9), Blocks.CACTUS.defaultBlockState(), 2);
                    }
                }
            }
        }
        return true;
    }
}
