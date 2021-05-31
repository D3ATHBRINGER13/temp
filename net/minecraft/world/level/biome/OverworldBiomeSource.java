package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import java.util.Random;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.Layer;

public class OverworldBiomeSource extends BiomeSource {
    private final Layer noiseBiomeLayer;
    private final Layer blockBiomeLayer;
    private final Biome[] possibleBiomes;
    
    public OverworldBiomeSource(final OverworldBiomeSourceSettings bkp) {
        this.possibleBiomes = new Biome[] { Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU };
        final LevelData com3 = bkp.getLevelData();
        final OverworldGeneratorSettings bze4 = bkp.getGeneratorSettings();
        final Layer[] arr5 = Layers.getDefaultLayers(com3.getSeed(), com3.getGeneratorType(), bze4);
        this.noiseBiomeLayer = arr5[0];
        this.blockBiomeLayer = arr5[1];
    }
    
    @Override
    public Biome getBiome(final int integer1, final int integer2) {
        return this.blockBiomeLayer.get(integer1, integer2);
    }
    
    @Override
    public Biome getNoiseBiome(final int integer1, final int integer2) {
        return this.noiseBiomeLayer.get(integer1, integer2);
    }
    
    @Override
    public Biome[] getBiomeBlock(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        return this.blockBiomeLayer.getArea(integer1, integer2, integer3, integer4);
    }
    
    @Override
    public Set<Biome> getBiomesWithin(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer1 - integer3 >> 2;
        final int integer5 = integer2 - integer3 >> 2;
        final int integer6 = integer1 + integer3 >> 2;
        final int integer7 = integer2 + integer3 >> 2;
        final int integer8 = integer6 - integer4 + 1;
        final int integer9 = integer7 - integer5 + 1;
        final Set<Biome> set11 = (Set<Biome>)Sets.newHashSet();
        Collections.addAll((Collection)set11, (Object[])this.noiseBiomeLayer.getArea(integer4, integer5, integer8, integer9));
        return set11;
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
        final Biome[] arr13 = this.noiseBiomeLayer.getArea(integer4, integer5, integer8, integer9);
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
