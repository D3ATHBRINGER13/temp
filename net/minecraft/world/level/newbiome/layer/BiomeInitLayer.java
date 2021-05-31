package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public class BiomeInitLayer implements C0Transformer {
    private static final int BIRCH_FOREST;
    private static final int DESERT;
    private static final int MOUNTAINS;
    private static final int FOREST;
    private static final int SNOWY_TUNDRA;
    private static final int JUNGLE;
    private static final int BADLANDS_PLATEAU;
    private static final int WOODED_BADLANDS_PLATEAU;
    private static final int MUSHROOM_FIELDS;
    private static final int PLAINS;
    private static final int GIANT_TREE_TAIGA;
    private static final int DARK_FOREST;
    private static final int SAVANNA;
    private static final int SWAMP;
    private static final int TAIGA;
    private static final int SNOWY_TAIGA;
    private static final int[] LEGACY_WARM_BIOMES;
    private static final int[] WARM_BIOMES;
    private static final int[] MEDIUM_BIOMES;
    private static final int[] COLD_BIOMES;
    private static final int[] ICE_BIOMES;
    private final OverworldGeneratorSettings settings;
    private int[] warmBiomes;
    
    public BiomeInitLayer(final LevelType bhy, final OverworldGeneratorSettings bze) {
        this.warmBiomes = BiomeInitLayer.WARM_BIOMES;
        if (bhy == LevelType.NORMAL_1_1) {
            this.warmBiomes = BiomeInitLayer.LEGACY_WARM_BIOMES;
            this.settings = null;
        }
        else {
            this.settings = bze;
        }
    }
    
    public int apply(final Context cly, int integer) {
        if (this.settings != null && this.settings.getFixedBiome() >= 0) {
            return this.settings.getFixedBiome();
        }
        final int integer2 = (integer & 0xF00) >> 8;
        integer &= 0xFFFFF0FF;
        if (Layers.isOcean(integer) || integer == BiomeInitLayer.MUSHROOM_FIELDS) {
            return integer;
        }
        switch (integer) {
            case 1: {
                if (integer2 > 0) {
                    return (cly.nextRandom(3) == 0) ? BiomeInitLayer.BADLANDS_PLATEAU : BiomeInitLayer.WOODED_BADLANDS_PLATEAU;
                }
                return this.warmBiomes[cly.nextRandom(this.warmBiomes.length)];
            }
            case 2: {
                if (integer2 > 0) {
                    return BiomeInitLayer.JUNGLE;
                }
                return BiomeInitLayer.MEDIUM_BIOMES[cly.nextRandom(BiomeInitLayer.MEDIUM_BIOMES.length)];
            }
            case 3: {
                if (integer2 > 0) {
                    return BiomeInitLayer.GIANT_TREE_TAIGA;
                }
                return BiomeInitLayer.COLD_BIOMES[cly.nextRandom(BiomeInitLayer.COLD_BIOMES.length)];
            }
            case 4: {
                return BiomeInitLayer.ICE_BIOMES[cly.nextRandom(BiomeInitLayer.ICE_BIOMES.length)];
            }
            default: {
                return BiomeInitLayer.MUSHROOM_FIELDS;
            }
        }
    }
    
    static {
        BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
        DESERT = Registry.BIOME.getId(Biomes.DESERT);
        MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
        FOREST = Registry.BIOME.getId(Biomes.FOREST);
        SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
        JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
        BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
        WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
        MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
        PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
        GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
        DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
        SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
        SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
        TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
        SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
        LEGACY_WARM_BIOMES = new int[] { BiomeInitLayer.DESERT, BiomeInitLayer.FOREST, BiomeInitLayer.MOUNTAINS, BiomeInitLayer.SWAMP, BiomeInitLayer.PLAINS, BiomeInitLayer.TAIGA };
        WARM_BIOMES = new int[] { BiomeInitLayer.DESERT, BiomeInitLayer.DESERT, BiomeInitLayer.DESERT, BiomeInitLayer.SAVANNA, BiomeInitLayer.SAVANNA, BiomeInitLayer.PLAINS };
        MEDIUM_BIOMES = new int[] { BiomeInitLayer.FOREST, BiomeInitLayer.DARK_FOREST, BiomeInitLayer.MOUNTAINS, BiomeInitLayer.PLAINS, BiomeInitLayer.BIRCH_FOREST, BiomeInitLayer.SWAMP };
        COLD_BIOMES = new int[] { BiomeInitLayer.FOREST, BiomeInitLayer.MOUNTAINS, BiomeInitLayer.TAIGA, BiomeInitLayer.PLAINS };
        ICE_BIOMES = new int[] { BiomeInitLayer.SNOWY_TUNDRA, BiomeInitLayer.SNOWY_TUNDRA, BiomeInitLayer.SNOWY_TUNDRA, BiomeInitLayer.SNOWY_TAIGA };
    }
}
