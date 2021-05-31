package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum AddDeepOceanLayer implements CastleTransformer {
    INSTANCE;
    
    public int apply(final Context cly, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        if (Layers.isShallowOcean(integer6)) {
            int integer7 = 0;
            if (Layers.isShallowOcean(integer2)) {
                ++integer7;
            }
            if (Layers.isShallowOcean(integer3)) {
                ++integer7;
            }
            if (Layers.isShallowOcean(integer5)) {
                ++integer7;
            }
            if (Layers.isShallowOcean(integer4)) {
                ++integer7;
            }
            if (integer7 > 3) {
                if (integer6 == Layers.WARM_OCEAN) {
                    return Layers.DEEP_WARM_OCEAN;
                }
                if (integer6 == Layers.LUKEWARM_OCEAN) {
                    return Layers.DEEP_LUKEWARM_OCEAN;
                }
                if (integer6 == Layers.OCEAN) {
                    return Layers.DEEP_OCEAN;
                }
                if (integer6 == Layers.COLD_OCEAN) {
                    return Layers.DEEP_COLD_OCEAN;
                }
                if (integer6 == Layers.FROZEN_OCEAN) {
                    return Layers.DEEP_FROZEN_OCEAN;
                }
                return Layers.DEEP_OCEAN;
            }
        }
        return integer6;
    }
}
