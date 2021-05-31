package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class RandomScatteredFeature<C extends FeatureConfiguration> extends StructureFeature<C> {
    public RandomScatteredFeature(final Function<Dynamic<?>, ? extends C> function) {
        super(function);
    }
    
    @Override
    protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = this.getSpacing(bxi);
        final int integer8 = this.getSeparation(bxi);
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((WorldgenRandom)random).setLargeFeatureWithSalt(bxi.getSeed(), integer13, integer14, this.getRandomSalt());
        integer13 *= integer7;
        integer14 *= integer7;
        integer13 += random.nextInt(integer7 - integer8);
        integer14 += random.nextInt(integer7 - integer8);
        return new ChunkPos(integer13, integer14);
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        final ChunkPos bhd6 = this.getPotentialFeatureChunkFromLocationWithOffset(bxi, random, integer3, integer4, 0, 0);
        if (integer3 == bhd6.x && integer4 == bhd6.z) {
            final Biome bio7 = bxi.getBiomeSource().getBiome(new BlockPos(integer3 * 16 + 9, 0, integer4 * 16 + 9));
            if (bxi.isBiomeValidStartForStructure(bio7, this)) {
                return true;
            }
        }
        return false;
    }
    
    protected int getSpacing(final ChunkGenerator<?> bxi) {
        return ((ChunkGeneratorSettings)bxi.getSettings()).getTemplesSpacing();
    }
    
    protected int getSeparation(final ChunkGenerator<?> bxi) {
        return ((ChunkGeneratorSettings)bxi.getSettings()).getTemplesSeparation();
    }
    
    protected abstract int getRandomSalt();
}
