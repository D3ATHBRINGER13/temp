package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;

public interface BishopTransformer extends AreaTransformer1, DimensionOffset1Transformer {
    int apply(final Context cly, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6);
    
    default int applyPixel(final BigContext<?> clx, final Area clt, final int integer3, final int integer4) {
        return this.apply(clx, clt.get(this.getParentX(integer3 + 0), this.getParentY(integer4 + 2)), clt.get(this.getParentX(integer3 + 2), this.getParentY(integer4 + 2)), clt.get(this.getParentX(integer3 + 2), this.getParentY(integer4 + 0)), clt.get(this.getParentX(integer3 + 0), this.getParentY(integer4 + 0)), clt.get(this.getParentX(integer3 + 1), this.getParentY(integer4 + 1)));
    }
}
