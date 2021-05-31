package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class RandomBooleanSelectorFeature extends Feature<RandomBooleanFeatureConfig> {
    public RandomBooleanSelectorFeature(final Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final RandomBooleanFeatureConfig cdp) {
        final boolean boolean7 = random.nextBoolean();
        if (boolean7) {
            return cdp.featureTrue.place(bhs, bxi, random, ew);
        }
        return cdp.featureFalse.place(bhs, bxi, random, ew);
    }
}
