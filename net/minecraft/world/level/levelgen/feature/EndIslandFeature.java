package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndIslandFeature extends Feature<NoneFeatureConfiguration> {
    public EndIslandFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        float float7 = (float)(random.nextInt(3) + 4);
        for (int integer8 = 0; float7 > 0.5f; float7 -= (float)(random.nextInt(2) + 0.5), --integer8) {
            for (int integer9 = Mth.floor(-float7); integer9 <= Mth.ceil(float7); ++integer9) {
                for (int integer10 = Mth.floor(-float7); integer10 <= Mth.ceil(float7); ++integer10) {
                    if (integer9 * integer9 + integer10 * integer10 <= (float7 + 1.0f) * (float7 + 1.0f)) {
                        this.setBlock(bhs, ew.offset(integer9, integer8, integer10), Blocks.END_STONE.defaultBlockState());
                    }
                }
            }
        }
        return true;
    }
}
