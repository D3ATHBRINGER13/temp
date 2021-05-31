package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomSelectorFeature extends Feature<RandomFeatureConfig> {
    public RandomSelectorFeature(final Function<Dynamic<?>, ? extends RandomFeatureConfig> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final RandomFeatureConfig cdr) {
        for (final WeightedConfiguredFeature<?> cfi8 : cdr.features) {
            if (random.nextFloat() < cfi8.chance) {
                return cfi8.place(bhs, bxi, random, ew);
            }
        }
        return cdr.defaultFeature.place(bhs, bxi, random, ew);
    }
}
