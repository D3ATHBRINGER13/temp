package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum VoronoiZoom implements AreaTransformer1 {
    INSTANCE;
    
    public int applyPixel(final BigContext<?> clx, final Area clt, final int integer3, final int integer4) {
        final int integer5 = integer3 - 2;
        final int integer6 = integer4 - 2;
        final int integer7 = integer5 >> 2;
        final int integer8 = integer6 >> 2;
        final int integer9 = integer7 << 2;
        final int integer10 = integer8 << 2;
        clx.initRandom(integer9, integer10);
        final double double12 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6;
        final double double13 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6;
        clx.initRandom(integer9 + 4, integer10);
        final double double14 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        final double double15 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6;
        clx.initRandom(integer9, integer10 + 4);
        final double double16 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6;
        final double double17 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        clx.initRandom(integer9 + 4, integer10 + 4);
        final double double18 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        final double double19 = (clx.nextRandom(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
        final int integer11 = integer5 & 0x3;
        final int integer12 = integer6 & 0x3;
        final double double20 = (integer12 - double13) * (integer12 - double13) + (integer11 - double12) * (integer11 - double12);
        final double double21 = (integer12 - double15) * (integer12 - double15) + (integer11 - double14) * (integer11 - double14);
        final double double22 = (integer12 - double17) * (integer12 - double17) + (integer11 - double16) * (integer11 - double16);
        final double double23 = (integer12 - double19) * (integer12 - double19) + (integer11 - double18) * (integer11 - double18);
        if (double20 < double21 && double20 < double22 && double20 < double23) {
            return clt.get(this.getParentX(integer9), this.getParentY(integer10));
        }
        if (double21 < double20 && double21 < double22 && double21 < double23) {
            return clt.get(this.getParentX(integer9 + 4), this.getParentY(integer10)) & 0xFF;
        }
        if (double22 < double20 && double22 < double21 && double22 < double23) {
            return clt.get(this.getParentX(integer9), this.getParentY(integer10 + 4));
        }
        return clt.get(this.getParentX(integer9 + 4), this.getParentY(integer10 + 4)) & 0xFF;
    }
    
    public int getParentX(final int integer) {
        return integer >> 2;
    }
    
    public int getParentY(final int integer) {
        return integer >> 2;
    }
}
