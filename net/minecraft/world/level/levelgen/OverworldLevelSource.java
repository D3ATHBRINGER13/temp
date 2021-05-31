package net.minecraft.world.level.levelgen;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelType;
import java.util.Random;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class OverworldLevelSource extends NoiseBasedChunkGenerator<OverworldGeneratorSettings> {
    private static final float[] BIOME_WEIGHTS;
    private final PerlinNoise depthNoise;
    private final boolean isAmplified;
    private final PhantomSpawner phantomSpawner;
    private final PatrolSpawner patrolSpawner;
    private final CatSpawner catSpawner;
    private final VillageSiege villageSiege;
    
    public OverworldLevelSource(final LevelAccessor bhs, final BiomeSource biq, final OverworldGeneratorSettings bze) {
        super(bhs, biq, 4, 8, 256, bze, true);
        this.phantomSpawner = new PhantomSpawner();
        this.patrolSpawner = new PatrolSpawner();
        this.catSpawner = new CatSpawner();
        this.villageSiege = new VillageSiege();
        this.random.consumeCount(2620);
        this.depthNoise = new PerlinNoise(this.random, 16);
        this.isAmplified = (bhs.getLevelData().getGeneratorType() == LevelType.AMPLIFIED);
    }
    
    @Override
    public void spawnOriginalMobs(final WorldGenRegion vq) {
        final int integer3 = vq.getCenterX();
        final int integer4 = vq.getCenterZ();
        final Biome bio5 = vq.getChunk(integer3, integer4).getBiomes()[0];
        final WorldgenRandom bzk6 = new WorldgenRandom();
        bzk6.setDecorationSeed(vq.getSeed(), integer3 << 4, integer4 << 4);
        NaturalSpawner.spawnMobsForChunkGeneration(vq, bio5, integer3, integer4, bzk6);
    }
    
    @Override
    protected void fillNoiseColumn(final double[] arr, final int integer2, final int integer3) {
        final double double5 = 684.4119873046875;
        final double double6 = 684.4119873046875;
        final double double7 = 8.555149841308594;
        final double double8 = 4.277574920654297;
        final int integer4 = -10;
        final int integer5 = 3;
        this.fillNoiseColumn(arr, integer2, integer3, 684.4119873046875, 684.4119873046875, 8.555149841308594, 4.277574920654297, 3, -10);
    }
    
    @Override
    protected double getYOffset(final double double1, final double double2, final int integer) {
        final double double3 = 8.5;
        double double4 = (integer - (8.5 + double1 * 8.5 / 8.0 * 4.0)) * 12.0 * 128.0 / 256.0 / double2;
        if (double4 < 0.0) {
            double4 *= 4.0;
        }
        return double4;
    }
    
    @Override
    protected double[] getDepthAndScale(final int integer1, final int integer2) {
        final double[] arr4 = new double[2];
        float float5 = 0.0f;
        float float6 = 0.0f;
        float float7 = 0.0f;
        final int integer3 = 2;
        final float float8 = this.biomeSource.getNoiseBiome(integer1, integer2).getDepth();
        for (int integer4 = -2; integer4 <= 2; ++integer4) {
            for (int integer5 = -2; integer5 <= 2; ++integer5) {
                final Biome bio12 = this.biomeSource.getNoiseBiome(integer1 + integer4, integer2 + integer5);
                float float9 = bio12.getDepth();
                float float10 = bio12.getScale();
                if (this.isAmplified && float9 > 0.0f) {
                    float9 = 1.0f + float9 * 2.0f;
                    float10 = 1.0f + float10 * 4.0f;
                }
                float float11 = OverworldLevelSource.BIOME_WEIGHTS[integer4 + 2 + (integer5 + 2) * 5] / (float9 + 2.0f);
                if (bio12.getDepth() > float8) {
                    float11 /= 2.0f;
                }
                float5 += float10 * float11;
                float6 += float9 * float11;
                float7 += float11;
            }
        }
        float5 /= float7;
        float6 /= float7;
        float5 = float5 * 0.9f + 0.1f;
        float6 = (float6 * 4.0f - 1.0f) / 8.0f;
        arr4[0] = float6 + this.getRdepth(integer1, integer2);
        arr4[1] = float5;
        return arr4;
    }
    
    private double getRdepth(final int integer1, final int integer2) {
        double double4 = this.depthNoise.getValue(integer1 * 200, 10.0, integer2 * 200, 1.0, 0.0, true) / 8000.0;
        if (double4 < 0.0) {
            double4 = -double4 * 0.3;
        }
        double4 = double4 * 3.0 - 2.0;
        if (double4 < 0.0) {
            double4 /= 28.0;
        }
        else {
            if (double4 > 1.0) {
                double4 = 1.0;
            }
            double4 /= 40.0;
        }
        return double4;
    }
    
    @Override
    public List<Biome.SpawnerData> getMobsAt(final MobCategory aiz, final BlockPos ew) {
        if (Feature.SWAMP_HUT.isSwamphut(this.level, ew)) {
            if (aiz == MobCategory.MONSTER) {
                return Feature.SWAMP_HUT.getSpecialEnemies();
            }
            if (aiz == MobCategory.CREATURE) {
                return Feature.SWAMP_HUT.getSpecialAnimals();
            }
        }
        else if (aiz == MobCategory.MONSTER) {
            if (Feature.PILLAGER_OUTPOST.isInsideBoundingFeature(this.level, ew)) {
                return Feature.PILLAGER_OUTPOST.getSpecialEnemies();
            }
            if (Feature.OCEAN_MONUMENT.isInsideBoundingFeature(this.level, ew)) {
                return Feature.OCEAN_MONUMENT.getSpecialEnemies();
            }
        }
        return super.getMobsAt(aiz, ew);
    }
    
    @Override
    public void tickCustomSpawners(final ServerLevel vk, final boolean boolean2, final boolean boolean3) {
        this.phantomSpawner.tick(vk, boolean2, boolean3);
        this.patrolSpawner.tick(vk, boolean2, boolean3);
        this.catSpawner.tick(vk, boolean2, boolean3);
        this.villageSiege.tick(vk, boolean2, boolean3);
    }
    
    @Override
    public int getSpawnHeight() {
        return this.level.getSeaLevel() + 1;
    }
    
    @Override
    public int getSeaLevel() {
        return 63;
    }
    
    static {
        BIOME_WEIGHTS = Util.<float[]>make(new float[25], (java.util.function.Consumer<float[]>)(arr -> {
            for (int integer2 = -2; integer2 <= 2; ++integer2) {
                for (int integer3 = -2; integer3 <= 2; ++integer3) {
                    final float float4 = 10.0f / Mth.sqrt(integer2 * integer2 + integer3 * integer3 + 0.2f);
                    arr[integer2 + 2 + (integer3 + 2) * 5] = float4;
                }
            }
        }));
    }
}
