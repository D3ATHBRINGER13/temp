package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RiverLayer implements CastleTransformer {
    INSTANCE;
    
    public static final int RIVER;
    
    public int apply(final Context cly, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = riverFilter(integer6);
        if (integer7 == riverFilter(integer5) && integer7 == riverFilter(integer2) && integer7 == riverFilter(integer3) && integer7 == riverFilter(integer4)) {
            return -1;
        }
        return RiverLayer.RIVER;
    }
    
    private static int riverFilter(final int integer) {
        if (integer >= 2) {
            return 2 + (integer & 0x1);
        }
        return integer;
    }
    
    static {
        RIVER = Registry.BIOME.getId(Biomes.RIVER);
    }
}
