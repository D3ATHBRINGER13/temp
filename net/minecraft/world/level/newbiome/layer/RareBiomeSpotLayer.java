package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeSpotLayer implements C1Transformer {
    INSTANCE;
    
    private static final int PLAINS;
    private static final int SUNFLOWER_PLAINS;
    
    public int apply(final Context cly, final int integer) {
        if (cly.nextRandom(57) == 0 && integer == RareBiomeSpotLayer.PLAINS) {
            return RareBiomeSpotLayer.SUNFLOWER_PLAINS;
        }
        return integer;
    }
    
    static {
        PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
        SUNFLOWER_PLAINS = Registry.BIOME.getId(Biomes.SUNFLOWER_PLAINS);
    }
}
