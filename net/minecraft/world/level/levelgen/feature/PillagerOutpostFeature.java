package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.levelgen.structure.PillagerOutpostPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import java.util.List;

public class PillagerOutpostFeature extends RandomScatteredFeature<PillagerOutpostConfiguration> {
    private static final List<Biome.SpawnerData> OUTPOST_ENEMIES;
    
    public PillagerOutpostFeature(final Function<Dynamic<?>, ? extends PillagerOutpostConfiguration> function) {
        super(function);
    }
    
    @Override
    public String getFeatureName() {
        return "Pillager_Outpost";
    }
    
    @Override
    public int getLookupRange() {
        return 3;
    }
    
    @Override
    public List<Biome.SpawnerData> getSpecialEnemies() {
        return PillagerOutpostFeature.OUTPOST_ENEMIES;
    }
    
    @Override
    public boolean isFeatureChunk(final ChunkGenerator<?> bxi, final Random random, final int integer3, final int integer4) {
        final ChunkPos bhd6 = this.getPotentialFeatureChunkFromLocationWithOffset(bxi, random, integer3, integer4, 0, 0);
        if (integer3 == bhd6.x && integer4 == bhd6.z) {
            final int integer5 = integer3 >> 4;
            final int integer6 = integer4 >> 4;
            random.setSeed((long)(integer5 ^ integer6 << 4) ^ bxi.getSeed());
            random.nextInt();
            if (random.nextInt(5) != 0) {
                return false;
            }
            final Biome bio9 = bxi.getBiomeSource().getBiome(new BlockPos((integer3 << 4) + 9, 0, (integer4 << 4) + 9));
            if (bxi.isBiomeValidStartForStructure(bio9, Feature.PILLAGER_OUTPOST)) {
                for (int integer7 = integer3 - 10; integer7 <= integer3 + 10; ++integer7) {
                    for (int integer8 = integer4 - 10; integer8 <= integer4 + 10; ++integer8) {
                        if (Feature.VILLAGE.isFeatureChunk(bxi, random, integer7, integer8)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStartFactory() {
        return FeatureStart::new;
    }
    
    @Override
    protected int getRandomSalt() {
        return 165745296;
    }
    
    static {
        OUTPOST_ENEMIES = (List)Lists.newArrayList((Object[])new Biome.SpawnerData[] { new Biome.SpawnerData(EntityType.PILLAGER, 1, 1, 1) });
    }
    
    public static class FeatureStart extends BeardedStructureStart {
        public FeatureStart(final StructureFeature<?> ceu, final int integer2, final int integer3, final Biome bio, final BoundingBox cic, final int integer6, final long long7) {
            super(ceu, integer2, integer3, bio, cic, integer6, long7);
        }
        
        @Override
        public void generatePieces(final ChunkGenerator<?> bxi, final StructureManager cjp, final int integer3, final int integer4, final Biome bio) {
            final BlockPos ew7 = new BlockPos(integer3 * 16, 90, integer4 * 16);
            PillagerOutpostPieces.addPieces(bxi, cjp, ew7, this.pieces, this.random);
            this.calculateBoundingBox();
        }
    }
}
