package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;

public interface AreaTransformer0 {
    default <R extends Area> AreaFactory<R> run(final BigContext<R> clx) {
        return () -> clx.createResult((integer2, integer3) -> {
            clx.initRandom(integer2, integer3);
            return this.applyPixel(clx, integer2, integer3);
        });
    }
    
    int applyPixel(final Context cly, final int integer2, final int integer3);
}
