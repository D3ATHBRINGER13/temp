package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomRandomFeature extends Feature<RandomRandomFeatureConfig> {
    public RandomRandomFeature(final Function<Dynamic<?>, ? extends RandomRandomFeatureConfig> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final RandomRandomFeatureConfig cdt) {
        for (int integer7 = random.nextInt(5) - 3 + cdt.count, integer8 = 0; integer8 < integer7; ++integer8) {
            final int integer9 = random.nextInt(cdt.features.size());
            final ConfiguredFeature<?> cal10 = cdt.features.get(integer9);
            cal10.place(bhs, bxi, random, ew);
        }
        return true;
    }
}
