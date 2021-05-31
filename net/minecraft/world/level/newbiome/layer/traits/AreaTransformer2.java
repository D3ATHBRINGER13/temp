package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;

public interface AreaTransformer2 extends DimensionTransformer {
    default <R extends Area> AreaFactory<R> run(final BigContext<R> clx, final AreaFactory<R> clu2, final AreaFactory<R> clu3) {
        final Area clt5;
        final Area clt6;
        final Area clt7;
        final Area clt8;
        return () -> {
            clt5 = clu2.make();
            clt6 = clu3.make();
            return clx.createResult((integer4, integer5) -> {
                clx.initRandom(integer4, integer5);
                return this.applyPixel(clx, clt7, clt8, integer4, integer5);
            }, (R)clt5, (R)clt6);
        };
    }
    
    int applyPixel(final Context cly, final Area clt2, final Area clt3, final int integer4, final int integer5);
}
