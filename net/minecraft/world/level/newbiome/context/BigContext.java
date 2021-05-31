package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;
import net.minecraft.world.level.newbiome.area.Area;

public interface BigContext<R extends Area> extends Context {
    void initRandom(final long long1, final long long2);
    
    R createResult(final PixelTransformer cnj);
    
    default R createResult(final PixelTransformer cnj, final R clt) {
        return this.createResult(cnj);
    }
    
    default R createResult(final PixelTransformer cnj, final R clt2, final R clt3) {
        return this.createResult(cnj);
    }
    
    default int random(final int integer1, final int integer2) {
        return (this.nextRandom(2) == 0) ? integer1 : integer2;
    }
    
    default int random(final int integer1, final int integer2, final int integer3, final int integer4) {
        final int integer5 = this.nextRandom(4);
        if (integer5 == 0) {
            return integer1;
        }
        if (integer5 == 1) {
            return integer2;
        }
        if (integer5 == 2) {
            return integer3;
        }
        return integer4;
    }
}
