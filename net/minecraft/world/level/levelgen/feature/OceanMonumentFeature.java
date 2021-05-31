package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.EntityType;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import java.util.List;

public class OceanMonumentFeature extends StructureFeature<NoneFeatureConfiguration> {
    private static final List<Biome.SpawnerData> MONUMENT_ENEMIES;
    
    public OceanMonumentFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorSettings)bxi.getSettings()).getMonumentsSpacing();
        final int integer8 = ((ChunkGeneratorSettings)bxi.getSettings()).getMonumentsSeparation();
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
        if (integer3 == bhd6.x && integer4 == bhd6.z) {
            final Set<Biome> set7 = bxi.getBiomeSource().getBiomesWithin(integer3 * 16 + 9, integer4 * 16 + 9, 16);
            for (final Biome bio9 : set7) {
                if (!bxi.isBiomeValidStartForStructure(bio9, Feature.OCEAN_MONUMENT)) {
                    return false;
                }
            }
            final Set<Biome> set8 = bxi.getBiomeSource().getBiomesWithin(integer3 * 16 + 9, integer4 * 16 + 9, 29);
            for (final Biome bio10 : set8) {
                if (bio10.getBiomeCategory() != Biome.BiomeCategory.OCEAN && bio10.getBiomeCategory() != Biome.BiomeCategory.RIVER) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return OceanMonumentStart::new;
    }
    
    @Override
    public String getFeatureName() {
        return "Monument";
    }
    
    @Override
    public int getLookupRange() {
        return 8;
    }
    
    @Override
    public List<Biome.SpawnerData> getSpecialEnemies() {
        return OceanMonumentFeature.MONUMENT_ENEMIES;
    }
    
    static {
        MONUMENT_ENEMIES = (List)Lists.newArrayList((Object[])new Biome.SpawnerData[] { new Biome.SpawnerData(EntityType.GUARDIAN, 1, 2, 4) });
    }
    
    public static class OceanMonumentStart extends StructureStart {
        private boolean isCreated;
        
        public OceanMonumentStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            this.generatePieces(integer3, integer4);
        }
        
        private void generatePieces(final int integer1, final int integer2) {
            final int integer3 = integer1 * 16 - 29;
            final int integer4 = integer2 * 16 - 29;
            final Direction fb6 = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
            this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, integer3, integer4, fb6));
            this.calculateBoundingBox();
            this.isCreated = true;
        }
        
        @Override
        public void postProcess(final LevelAccessor bhs, final Random random, final BoundingBox cic, final ChunkPos bhd) {
            if (!this.isCreated) {
                this.pieces.clear();
                this.generatePieces(this.getChunkX(), this.getChunkZ());
            }
            super.postProcess(bhs, random, cic, bhd);
        }
    }
}
