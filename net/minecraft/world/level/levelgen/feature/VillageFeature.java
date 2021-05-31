package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class VillageFeature extends StructureFeature<VillageConfiguration> {
    public VillageFeature(final Function<Dynamic<?>, ? extends VillageConfiguration> function) {
        super(function);
    }
    
    @Override
    protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorSettings)bxi.getSettings()).getVillagesSpacing();
        final int integer8 = ((ChunkGeneratorSettings)bxi.getSettings()).getVillagesSeparation();
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((WorldgenRandom)random).setLargeFeatureWithSalt(bxi.getSeed(), integer13, integer14, 10387312);
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
            final Biome bio7 = bxi.getBiomeSource().getBiome(new BlockPos((integer3 << 4) + 9, 0, (integer4 << 4) + 9));
            return bxi.isBiomeValidStartForStructure(bio7, Feature.VILLAGE);
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return FeatureStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Village";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    public static class FeatureStart extends BeardedStructureStart {
        public FeatureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final VillageConfiguration cfc7 = bxi.<VillageConfiguration>getStructureConfiguration(bio, Feature.VILLAGE);
            final BlockPos ew8 = new BlockPos(integer3 * 16, 0, integer4 * 16);
            VillagePieces.addPieces(bxi, cjp, ew8, this.pieces, this.random, cfc7);
            this.calculateBoundingBox();
        }
    }
}
