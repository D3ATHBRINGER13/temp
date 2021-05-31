package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class StructureTemplatePools {
    private final Map<ResourceLocation, StructureTemplatePool> pools;
    
    public StructureTemplatePools() {
        this.pools = (Map<ResourceLocation, StructureTemplatePool>)Maps.newHashMap();
        this.register(StructureTemplatePool.EMPTY);
    }
    
    public void register(final StructureTemplatePool cft) {
        this.pools.put(cft.getName(), cft);
    }
    
    public StructureTemplatePool getPool(final ResourceLocation qv) {
        final StructureTemplatePool cft3 = (StructureTemplatePool)this.pools.get(qv);
        return (cft3 != null) ? cft3 : StructureTemplatePool.INVALID;
    }
}
