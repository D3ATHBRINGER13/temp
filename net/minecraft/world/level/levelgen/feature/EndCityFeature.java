package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndCityFeature extends StructureFeature<NoneFeatureConfiguration> {
    public EndCityFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorSettings)bxi.getSettings()).getEndCitySpacing();
        final int integer8 = ((ChunkGeneratorSettings)bxi.getSettings()).getEndCitySeparation();
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((WorldgenRandom)random).setLargeFeatureWithSalt(bxi.getSeed(), integer13, integer14, 10387313);
        integer13 *= integer7;
        integer14 *= integer7;
        integer13 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        integer14 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        return new ChunkPos(integer13, integer14);
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        final ChunkPos bhd6 = this.getPotentialFeatureChunkFromLocationWithOffset(bxi, random, integer3, integer4, 0, 0);
        if (integer3 != bhd6.x || integer4 != bhd6.z) {
            return false;
        }
        final Biome bio7 = bxi.getBiomeSource().getBiome(new BlockPos((integer3 << 4) + 9, 0, (integer4 << 4) + 9));
        if (!bxi.isBiomeValidStartForStructure(bio7, Feature.END_CITY)) {
            return false;
        }
        final int integer5 = getYPositionForFeature(integer3, integer4, bxi);
        return integer5 >= 60;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return EndCityStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "EndCity";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    private static int getYPositionForFeature(final int integer1, final int integer2, final ChunkGenerator<?> bxi) {
        final Random random4 = new Random((long)(integer1 + integer2 * 10387313));
        final Rotation brg5 = Rotation.values()[random4.nextInt(Rotation.values().length)];
        int integer3 = 5;
        int integer4 = 5;
        if (brg5 == Rotation.CLOCKWISE_90) {
            integer3 = -5;
        }
        else if (brg5 == Rotation.CLOCKWISE_180) {
            integer3 = -5;
            integer4 = -5;
        }
        else if (brg5 == Rotation.COUNTERCLOCKWISE_90) {
            integer4 = -5;
        }
        final int integer5 = (integer1 << 4) + 7;
        final int integer6 = (integer2 << 4) + 7;
        final int integer7 = bxi.getFirstOccupiedHeight(integer5, integer6, Heightmap.Types.WORLD_SURFACE_WG);
        final int integer8 = bxi.getFirstOccupiedHeight(integer5, integer6 + integer4, Heightmap.Types.WORLD_SURFACE_WG);
        final int integer9 = bxi.getFirstOccupiedHeight(integer5 + integer3, integer6, Heightmap.Types.WORLD_SURFACE_WG);
        final int integer10 = bxi.getFirstOccupiedHeight(integer5 + integer3, integer6 + integer4, Heightmap.Types.WORLD_SURFACE_WG);
        return Math.min(Math.min(integer7, integer8), Math.min(integer9, integer10));
    }
    
    public static class EndCityStart extends StructureStart {
        public EndCityStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final Rotation brg7 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            final int integer5 = getYPositionForFeature(integer3, integer4, bxi);
            if (integer5 < 60) {
                return;
            }
            final BlockPos ew9 = new BlockPos(integer3 * 16 + 8, integer5, integer4 * 16 + 8);
            EndCityPieces.startHouseTower(cjp, ew9, brg7, this.pieces, this.random);
            this.calculateBoundingBox();
        }
    }
}
