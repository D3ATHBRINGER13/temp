package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum ShoreLayer implements CastleTransformer {
    INSTANCE;
    
    private static final int BEACH;
    private static final int SNOWY_BEACH;
    private static final int DESERT;
    private static final int MOUNTAINS;
    private static final int WOODED_MOUNTAINS;
    private static final int FOREST;
    private static final int JUNGLE;
    private static final int JUNGLE_EDGE;
    private static final int JUNGLE_HILLS;
    private static final int BADLANDS;
    private static final int WOODED_BADLANDS_PLATEAU;
    private static final int BADLANDS_PLATEAU;
    private static final int ERODED_BADLANDS;
    private static final int MODIFIED_WOODED_BADLANDS_PLATEAU;
    private static final int MODIFIED_BADLANDS_PLATEAU;
    private static final int MUSHROOM_FIELDS;
    private static final int MUSHROOM_FIELD_SHORE;
    private static final int RIVER;
    private static final int MOUNTAIN_EDGE;
    private static final int STONE_SHORE;
    private static final int SWAMP;
    private static final int TAIGA;
    
    public int apply(final Context cly, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        final Biome bio8 = Registry.BIOME.byId(integer6);
        if (integer6 == ShoreLayer.MUSHROOM_FIELDS) {
            if (Layers.isShallowOcean(integer2) || Layers.isShallowOcean(integer3) || Layers.isShallowOcean(integer4) || Layers.isShallowOcean(integer5)) {
                return ShoreLayer.MUSHROOM_FIELD_SHORE;
            }
        }
        else if (bio8 != null && bio8.getBiomeCategory() == Biome.BiomeCategory.JUNGLE) {
            if (!isJungleCompatible(integer2) || !isJungleCompatible(integer3) || !isJungleCompatible(integer4) || !isJungleCompatible(integer5)) {
                return ShoreLayer.JUNGLE_EDGE;
            }
            if (Layers.isOcean(integer2) || Layers.isOcean(integer3) || Layers.isOcean(integer4) || Layers.isOcean(integer5)) {
                return ShoreLayer.BEACH;
            }
        }
        else if (integer6 == ShoreLayer.MOUNTAINS || integer6 == ShoreLayer.WOODED_MOUNTAINS || integer6 == ShoreLayer.MOUNTAIN_EDGE) {
            if (!Layers.isOcean(integer6) && (Layers.isOcean(integer2) || Layers.isOcean(integer3) || Layers.isOcean(integer4) || Layers.isOcean(integer5))) {
                return ShoreLayer.STONE_SHORE;
            }
        }
        else if (bio8 != null && bio8.getPrecipitation() == Biome.Precipitation.SNOW) {
            if (!Layers.isOcean(integer6) && (Layers.isOcean(integer2) || Layers.isOcean(integer3) || Layers.isOcean(integer4) || Layers.isOcean(integer5))) {
                return ShoreLayer.SNOWY_BEACH;
            }
        }
        else if (integer6 == ShoreLayer.BADLANDS || integer6 == ShoreLayer.WOODED_BADLANDS_PLATEAU) {
            if (!Layers.isOcean(integer2) && !Layers.isOcean(integer3) && !Layers.isOcean(integer4) && !Layers.isOcean(integer5) && (!this.isMesa(integer2) || !this.isMesa(integer3) || !this.isMesa(integer4) || !this.isMesa(integer5))) {
                return ShoreLayer.DESERT;
            }
        }
        else if (!Layers.isOcean(integer6) && integer6 != ShoreLayer.RIVER && integer6 != ShoreLayer.SWAMP && (Layers.isOcean(integer2) || Layers.isOcean(integer3) || Layers.isOcean(integer4) || Layers.isOcean(integer5))) {
            return ShoreLayer.BEACH;
        }
        return integer6;
    }
    
    private static boolean isJungleCompatible(final int integer) {
        return (Registry.BIOME.byId(integer) != null && Registry.BIOME.byId(integer).getBiomeCategory() == Biome.BiomeCategory.JUNGLE) || integer == ShoreLayer.JUNGLE_EDGE || integer == ShoreLayer.JUNGLE || integer == ShoreLayer.JUNGLE_HILLS || integer == ShoreLayer.FOREST || integer == ShoreLayer.TAIGA || Layers.isOcean(integer);
    }
    
    private boolean isMesa(final int integer) {
        return integer == ShoreLayer.BADLANDS || integer == ShoreLayer.WOODED_BADLANDS_PLATEAU || integer == ShoreLayer.BADLANDS_PLATEAU || integer == ShoreLayer.ERODED_BADLANDS || integer == ShoreLayer.MODIFIED_WOODED_BADLANDS_PLATEAU || integer == ShoreLayer.MODIFIED_BADLANDS_PLATEAU;
    }
    
    static {
        BEACH = Registry.BIOME.getId(Biomes.BEACH);
        SNOWY_BEACH = Registry.BIOME.getId(Biomes.SNOWY_BEACH);
        DESERT = Registry.BIOME.getId(Biomes.DESERT);
        MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
        FOREST = Registry.BIOME.getId(Biomes.FOREST);
        JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
        JUNGLE_EDGE = Registry.BIOME.getId(Biomes.JUNGLE_EDGE);
        JUNGLE_HILLS = Registry.BIOME.getId(Biomes.JUNGLE_HILLS);
        BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
        WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
        BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
        ERODED_BADLANDS = Registry.BIOME.getId(Biomes.ERODED_BADLANDS);
        MODIFIED_WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
        MODIFIED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.MODIFIED_BADLANDS_PLATEAU);
        MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
        MUSHROOM_FIELD_SHORE = Registry.BIOME.getId(Biomes.MUSHROOM_FIELD_SHORE);
        RIVER = Registry.BIOME.getId(Biomes.RIVER);
        MOUNTAIN_EDGE = Registry.BIOME.getId(Biomes.MOUNTAIN_EDGE);
        STONE_SHORE = Registry.BIOME.getId(Biomes.STONE_SHORE);
        SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
        TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
    }
}
