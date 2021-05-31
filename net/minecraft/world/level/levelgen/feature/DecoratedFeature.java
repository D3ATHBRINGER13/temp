package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DecoratedFeature extends Feature<DecoratedFeatureConfiguration> {
    public DecoratedFeature(final Function<Dynamic<?>, ? extends DecoratedFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final DecoratedFeatureConfiguration cau) {
        return cau.decorator.place(bhs, bxi, random, ew, cau.feature);
    }
    
    public String toString() {
        return String.format("< %s [%s] >", new Object[] { this.getClass().getSimpleName(), Registry.FEATURE.getKey(this) });
    }
}
