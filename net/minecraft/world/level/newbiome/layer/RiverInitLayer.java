package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public enum RiverInitLayer implements C0Transformer {
    INSTANCE;
    
    public int apply(final Context cly, final int integer) {
        return Layers.isShallowOcean(integer) ? integer : (cly.nextRandom(299999) + 2);
    }
}
