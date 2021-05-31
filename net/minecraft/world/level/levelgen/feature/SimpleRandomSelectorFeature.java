package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SimpleRandomSelectorFeature extends Feature<SimpleRandomFeatureConfig> {
    public SimpleRandomSelectorFeature(final Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final SimpleRandomFeatureConfig cei) {
        final int integer7 = random.nextInt(cei.features.size());
        final ConfiguredFeature<?> cal8 = cei.features.get(integer7);
        return cal8.place(bhs, bxi, random, ew);
    }
}
