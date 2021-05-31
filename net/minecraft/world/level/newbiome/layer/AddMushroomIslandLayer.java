package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddMushroomIslandLayer implements BishopTransformer {
    INSTANCE;
    
    private static final int MUSHROOM_FIELDS;
    
    public int apply(final Context cly, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        if (Layers.isShallowOcean(integer6) && Layers.isShallowOcean(integer5) && Layers.isShallowOcean(integer2) && Layers.isShallowOcean(integer4) && Layers.isShallowOcean(integer3) && cly.nextRandom(100) == 0) {
            return AddMushroomIslandLayer.MUSHROOM_FIELDS;
        }
        return integer6;
    }
    
    static {
        MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
    }
}
