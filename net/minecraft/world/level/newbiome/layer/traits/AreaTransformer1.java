package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;

public interface AreaTransformer1 extends DimensionTransformer {
    default <R extends Area> AreaFactory<R> run(final BigContext<R> clx, final AreaFactory<R> clu) {
        final Area clt4;
        final Area clt5;
        return () -> {
            clt4 = clu.make();
            return clx.createResult((integer3, integer4) -> {
                clx.initRandom(integer3, integer4);
                return this.applyPixel(clx, clt5, integer3, integer4);
            }, (R)clt4);
        };
    }
    
    int applyPixel(final BigContext<?> clx, final Area clt, final int integer3, final int integer4);
}
