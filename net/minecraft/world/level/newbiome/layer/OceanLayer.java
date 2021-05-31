package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum OceanLayer implements AreaTransformer0 {
    INSTANCE;
    
    public int applyPixel(final Context cly, final int integer2, final int integer3) {
        final ImprovedNoise ckn5 = cly.getBiomeNoise();
        final double double6 = ckn5.noise(integer2 / 8.0, integer3 / 8.0, 0.0, 0.0, 0.0);
        if (double6 > 0.4) {
            return Layers.WARM_OCEAN;
        }
        if (double6 > 0.2) {
            return Layers.LUKEWARM_OCEAN;
        }
        if (double6 < -0.4) {
            return Layers.FROZEN_OCEAN;
        }
        if (double6 < -0.2) {
            return Layers.COLD_OCEAN;
        }
        return Layers.OCEAN;
    }
}
