package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;

public enum OceanMixerLayer implements AreaTransformer2, DimensionOffset0Transformer {
    INSTANCE;
    
    public int applyPixel(final Context cly, final Area clt2, final Area clt3, final int integer4, final int integer5) {
        final int integer6 = clt2.get(this.getParentX(integer4), this.getParentY(integer5));
        final int integer7 = clt3.get(this.getParentX(integer4), this.getParentY(integer5));
        if (!Layers.isOcean(integer6)) {
            return integer6;
        }
        final int integer8 = 8;
        final int integer9 = 4;
        for (int integer10 = -8; integer10 <= 8; integer10 += 4) {
            for (int integer11 = -8; integer11 <= 8; integer11 += 4) {
                final int integer12 = clt2.get(this.getParentX(integer4 + integer10), this.getParentY(integer5 + integer11));
                if (!Layers.isOcean(integer12)) {
                    if (integer7 == Layers.WARM_OCEAN) {
                        return Layers.LUKEWARM_OCEAN;
                    }
                    if (integer7 == Layers.FROZEN_OCEAN) {
                        return Layers.COLD_OCEAN;
                    }
                }
            }
        }
        if (integer6 == Layers.DEEP_OCEAN) {
            if (integer7 == Layers.LUKEWARM_OCEAN) {
                return Layers.DEEP_LUKEWARM_OCEAN;
            }
            if (integer7 == Layers.OCEAN) {
                return Layers.DEEP_OCEAN;
            }
            if (integer7 == Layers.COLD_OCEAN) {
                return Layers.DEEP_COLD_OCEAN;
            }
            if (integer7 == Layers.FROZEN_OCEAN) {
                return Layers.DEEP_FROZEN_OCEAN;
            }
        }
        return integer7;
    }
}
