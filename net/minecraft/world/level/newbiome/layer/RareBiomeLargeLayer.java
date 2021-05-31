package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeLargeLayer implements C1Transformer {
    INSTANCE;
    
    private static final int JUNGLE;
    private static final int BAMBOO_JUNGLE;
    
    public int apply(final Context cly, final int integer) {
        if (cly.nextRandom(10) == 0 && integer == RareBiomeLargeLayer.JUNGLE) {
            return RareBiomeLargeLayer.BAMBOO_JUNGLE;
        }
        return integer;
    }
    
    static {
        JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
        BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
    }
}
