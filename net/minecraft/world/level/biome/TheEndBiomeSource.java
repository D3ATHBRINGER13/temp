package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import java.util.List;
import com.google.common.collect.Sets;
import java.util.Set;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.level.ChunkPos;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Random;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource extends BiomeSource {
    private final SimplexNoise islandNoise;
    private final WorldgenRandom random;
    private final Biome[] possibleBiomes;
    
    public TheEndBiomeSource(final TheEndBiomeSourceSettings blo) {
        this.possibleBiomes = new Biome[] { Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS };
        (this.random = new WorldgenRandom(blo.getSeed())).consumeCount(17292);
        this.islandNoise = new SimplexNoise(this.random);
    }
    
    @Override
    public Biome getBiome(final int integer1, final int integer2) {
        final int integer3 = integer1 >> 4;
        final int integer4 = integer2 >> 4;
        if (integer3 * (long)integer3 + integer4 * (long)integer4 <= 4096L) {
            return Biomes.THE_END;
        }
        final float float6 = this.getHeightValue(integer3 * 2 + 1, integer4 * 2 + 1);
        if (float6 > 40.0f) {
            return Biomes.END_HIGHLANDS;
        }
        if (float6 >= 0.0f) {
            return Biomes.END_MIDLANDS;
        }
        if (float6 < -20.0f) {
            return Biomes.SMALL_END_ISLANDS;
        }
        return Biomes.END_BARRENS;
    }
    
    @Override
    public Biome[] getBiomeBlock(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        final Biome[] arr7 = new Biome[integer3 * integer4];
        final Long2ObjectMap<Biome> long2ObjectMap8 = (Long2ObjectMap<Biome>)new Long2ObjectOpenHashMap();
        for (int integer5 = 0; integer5 < integer3; ++integer5) {
            for (int integer6 = 0; integer6 < integer4; ++integer6) {
                final int integer7 = integer5 + integer1;
                final int integer8 = integer6 + integer2;
                final long long13 = ChunkPos.asLong(integer7, integer8);
                Biome bio15 = (Biome)long2ObjectMap8.get(long13);
                if (bio15 == null) {
                    bio15 = this.getBiome(integer7, integer8);
                    long2ObjectMap8.put(long13, bio15);
                }
                arr7[integer5 + integer6 * integer3] = bio15;
            }
        }
        return arr7;
    }
    
    @Override
    public Set<Biome> getBiomesWithin(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer1 - integer3 >> 2;
        final int integer5 = integer2 - integer3 >> 2;
        final int integer6 = integer1 + integer3 >> 2;
        final int integer7 = integer2 + integer3 >> 2;
        final int integer8 = integer6 - integer4 + 1;
        final int integer9 = integer7 - integer5 + 1;
        return (Set<Biome>)Sets.newHashSet((Object[])this.getBiomeBlock(integer4, integer5, integer8, integer9));
    }
    
    @Nullable
    @Override
    public BlockPos findBiome(final int integer1, final int integer2, final int integer3, final List<Biome> list, final Random random) {
        final int integer4 = integer1 - integer3 >> 2;
        final int integer5 = integer2 - integer3 >> 2;
        final int integer6 = integer1 + integer3 >> 2;
        final int integer7 = integer2 + integer3 >> 2;
        final int integer8 = integer6 - integer4 + 1;
        final int integer9 = integer7 - integer5 + 1;
        final Biome[] arr13 = this.getBiomeBlock(integer4, integer5, integer8, integer9);
        BlockPos ew14 = null;
        int integer10 = 0;
        for (int integer11 = 0; integer11 < integer8 * integer9; ++integer11) {
            final int integer12 = integer4 + integer11 % integer8 << 2;
            final int integer13 = integer5 + integer11 / integer8 << 2;
            if (list.contains(arr13[integer11])) {
                if (ew14 == null || random.nextInt(integer10 + 1) == 0) {
                    ew14 = new BlockPos(integer12, 0, integer13);
                }
                ++integer10;
            }
        }
        return ew14;
    }
    
    @Override
    public float getHeightValue(final int integer1, final int integer2) {
        final int integer3 = integer1 / 2;
        final int integer4 = integer2 / 2;
        final int integer5 = integer1 % 2;
        final int integer6 = integer2 % 2;
        float float8 = 100.0f - Mth.sqrt((float)(integer1 * integer1 + integer2 * integer2)) * 8.0f;
        float8 = Mth.clamp(float8, -100.0f, 80.0f);
        for (int integer7 = -12; integer7 <= 12; ++integer7) {
            for (int integer8 = -12; integer8 <= 12; ++integer8) {
                final long long11 = integer3 + integer7;
                final long long12 = integer4 + integer8;
                if (long11 * long11 + long12 * long12 > 4096L && this.islandNoise.getValue((double)long11, (double)long12) < -0.8999999761581421) {
                    final float float9 = (Mth.abs((float)long11) * 3439.0f + Mth.abs((float)long12) * 147.0f) % 13.0f + 9.0f;
                    final float float10 = (float)(integer5 - integer7 * 2);
                    final float float11 = (float)(integer6 - integer8 * 2);
                    float float12 = 100.0f - Mth.sqrt(float10 * float10 + float11 * float11) * float9;
                    float12 = Mth.clamp(float12, -100.0f, 80.0f);
                    float8 = Math.max(float8, float12);
                }
            }
        }
        return float8;
    }
    
    @Override
    public boolean canGenerateStructure(final StructureFeature<?> ceu) {
        return (boolean)this.supportedStructures.computeIfAbsent(ceu, ceu -> {
            for (final Biome bio6 : this.possibleBiomes) {
                if (bio6.<FeatureConfiguration>isValidStart((StructureFeature<FeatureConfiguration>)ceu)) {
                    return true;
                }
            }
            return false;
        });
    }
    
    @Override
    public Set<BlockState> getSurfaceBlocks() {
        if (this.surfaceBlocks.isEmpty()) {
            for (final Biome bio5 : this.possibleBiomes) {
                this.surfaceBlocks.add(bio5.getSurfaceBuilderConfig().getTopMaterial());
            }
        }
        return this.surfaceBlocks;
    }
}
