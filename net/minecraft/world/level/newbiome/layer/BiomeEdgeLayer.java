package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum BiomeEdgeLayer implements CastleTransformer {
    INSTANCE;
    
    private static final int DESERT;
    private static final int MOUNTAINS;
    private static final int WOODED_MOUNTAINS;
    private static final int SNOWY_TUNDRA;
    private static final int JUNGLE;
    private static final int BAMBOO_JUNGLE;
    private static final int JUNGLE_EDGE;
    private static final int BADLANDS;
    private static final int BADLANDS_PLATEAU;
    private static final int WOODED_BADLANDS_PLATEAU;
    private static final int PLAINS;
    private static final int GIANT_TREE_TAIGA;
    private static final int MOUNTAIN_EDGE;
    private static final int SWAMP;
    private static final int TAIGA;
    private static final int SNOWY_TAIGA;
    
    public int apply(final Context cly, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int[] arr8 = { 0 };
        if (this.checkEdge(arr8, integer2, integer3, integer4, integer5, integer6, BiomeEdgeLayer.MOUNTAINS, BiomeEdgeLayer.MOUNTAIN_EDGE) || this.checkEdgeStrict(arr8, integer2, integer3, integer4, integer5, integer6, BiomeEdgeLayer.WOODED_BADLANDS_PLATEAU, BiomeEdgeLayer.BADLANDS) || this.checkEdgeStrict(arr8, integer2, integer3, integer4, integer5, integer6, BiomeEdgeLayer.BADLANDS_PLATEAU, BiomeEdgeLayer.BADLANDS) || this.checkEdgeStrict(arr8, integer2, integer3, integer4, integer5, integer6, BiomeEdgeLayer.GIANT_TREE_TAIGA, BiomeEdgeLayer.TAIGA)) {
            return arr8[0];
        }
        if (integer6 == BiomeEdgeLayer.DESERT && (integer2 == BiomeEdgeLayer.SNOWY_TUNDRA || integer3 == BiomeEdgeLayer.SNOWY_TUNDRA || integer5 == BiomeEdgeLayer.SNOWY_TUNDRA || integer4 == BiomeEdgeLayer.SNOWY_TUNDRA)) {
            return BiomeEdgeLayer.WOODED_MOUNTAINS;
        }
        if (integer6 == BiomeEdgeLayer.SWAMP) {
            if (integer2 == BiomeEdgeLayer.DESERT || integer3 == BiomeEdgeLayer.DESERT || integer5 == BiomeEdgeLayer.DESERT || integer4 == BiomeEdgeLayer.DESERT || integer2 == BiomeEdgeLayer.SNOWY_TAIGA || integer3 == BiomeEdgeLayer.SNOWY_TAIGA || integer5 == BiomeEdgeLayer.SNOWY_TAIGA || integer4 == BiomeEdgeLayer.SNOWY_TAIGA || integer2 == BiomeEdgeLayer.SNOWY_TUNDRA || integer3 == BiomeEdgeLayer.SNOWY_TUNDRA || integer5 == BiomeEdgeLayer.SNOWY_TUNDRA || integer4 == BiomeEdgeLayer.SNOWY_TUNDRA) {
                return BiomeEdgeLayer.PLAINS;
            }
            if (integer2 == BiomeEdgeLayer.JUNGLE || integer4 == BiomeEdgeLayer.JUNGLE || integer3 == BiomeEdgeLayer.JUNGLE || integer5 == BiomeEdgeLayer.JUNGLE || integer2 == BiomeEdgeLayer.BAMBOO_JUNGLE || integer4 == BiomeEdgeLayer.BAMBOO_JUNGLE || integer3 == BiomeEdgeLayer.BAMBOO_JUNGLE || integer5 == BiomeEdgeLayer.BAMBOO_JUNGLE) {
                return BiomeEdgeLayer.JUNGLE_EDGE;
            }
        }
        return integer6;
    }
    
    private boolean checkEdge(final int[] arr, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        if (!Layers.isSame(integer6, integer7)) {
            return false;
        }
        if (this.isValidTemperatureEdge(integer2, integer7) && this.isValidTemperatureEdge(integer3, integer7) && this.isValidTemperatureEdge(integer5, integer7) && this.isValidTemperatureEdge(integer4, integer7)) {
            arr[0] = integer6;
        }
        else {
            arr[0] = integer8;
        }
        return true;
    }
    
    private boolean checkEdgeStrict(final int[] arr, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        if (integer6 != integer7) {
            return false;
        }
        if (Layers.isSame(integer2, integer7) && Layers.isSame(integer3, integer7) && Layers.isSame(integer5, integer7) && Layers.isSame(integer4, integer7)) {
            arr[0] = integer6;
        }
        else {
            arr[0] = integer8;
        }
        return true;
    }
    
    private boolean isValidTemperatureEdge(final int integer1, final int integer2) {
        if (Layers.isSame(integer1, integer2)) {
            return true;
        }
        final Biome bio4 = Registry.BIOME.byId(integer1);
        final Biome bio5 = Registry.BIOME.byId(integer2);
        if (bio4 != null && bio5 != null) {
            final Biome.BiomeTempCategory c6 = bio4.getTemperatureCategory();
            final Biome.BiomeTempCategory c7 = bio5.getTemperatureCategory();
            return c6 == c7 || c6 == Biome.BiomeTempCategory.MEDIUM || c7 == Biome.BiomeTempCategory.MEDIUM;
        }
        return false;
    }
    
    static {
        DESERT = Registry.BIOME.getId(Biomes.DESERT);
        MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
        SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
        JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
        BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
        JUNGLE_EDGE = Registry.BIOME.getId(Biomes.JUNGLE_EDGE);
        BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
        BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
        WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
        PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
        GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
        MOUNTAIN_EDGE = Registry.BIOME.getId(Biomes.MOUNTAIN_EDGE);
        SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
        TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
        SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
    }
}
