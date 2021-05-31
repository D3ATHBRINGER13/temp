package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;

public enum RiverMixerLayer implements AreaTransformer2, DimensionOffset0Transformer {
    INSTANCE;
    
    private static final int FROZEN_RIVER;
    private static final int SNOWY_TUNDRA;
    private static final int MUSHROOM_FIELDS;
    private static final int MUSHROOM_FIELD_SHORE;
    private static final int RIVER;
    
    public int applyPixel(final Context cly, final Area clt2, final Area clt3, final int integer4, final int integer5) {
        final int integer6 = clt2.get(this.getParentX(integer4), this.getParentY(integer5));
        final int integer7 = clt3.get(this.getParentX(integer4), this.getParentY(integer5));
        if (Layers.isOcean(integer6)) {
            return integer6;
        }
        if (integer7 != RiverMixerLayer.RIVER) {
            return integer6;
        }
        if (integer6 == RiverMixerLayer.SNOWY_TUNDRA) {
            return RiverMixerLayer.FROZEN_RIVER;
        }
        if (integer6 == RiverMixerLayer.MUSHROOM_FIELDS || integer6 == RiverMixerLayer.MUSHROOM_FIELD_SHORE) {
            return RiverMixerLayer.MUSHROOM_FIELD_SHORE;
        }
        return integer7 & 0xFF;
    }
    
    static {
        FROZEN_RIVER = Registry.BIOME.getId(Biomes.FROZEN_RIVER);
        SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
        MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
        MUSHROOM_FIELD_SHORE = Registry.BIOME.getId(Biomes.MUSHROOM_FIELD_SHORE);
        RIVER = Registry.BIOME.getId(Biomes.RIVER);
    }
}
