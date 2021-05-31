package net.minecraft.world.level.levelgen.feature.structures;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class JigsawJunction {
    private final int sourceX;
    private final int sourceGroundY;
    private final int sourceZ;
    private final int deltaY;
    private final StructureTemplatePool.Projection destProjection;
    
    public JigsawJunction(final int integer1, final int integer2, final int integer3, final int integer4, final StructureTemplatePool.Projection a) {
        this.sourceX = integer1;
        this.sourceGroundY = integer2;
        this.sourceZ = integer3;
        this.deltaY = integer4;
        this.destProjection = a;
    }
    
    public int getSourceX() {
        return this.sourceX;
    }
    
    public int getSourceGroundY() {
        return this.sourceGroundY;
    }
    
    public int getSourceZ() {
        return this.sourceZ;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        final ImmutableMap.Builder<T, T> builder3 = (ImmutableMap.Builder<T, T>)ImmutableMap.builder();
        builder3.put(dynamicOps.createString("source_x"), dynamicOps.createInt(this.sourceX)).put(dynamicOps.createString("source_ground_y"), dynamicOps.createInt(this.sourceGroundY)).put(dynamicOps.createString("source_z"), dynamicOps.createInt(this.sourceZ)).put(dynamicOps.createString("delta_y"), dynamicOps.createInt(this.deltaY)).put(dynamicOps.createString("dest_proj"), dynamicOps.createString(this.destProjection.getName()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)builder3.build()));
    }
    
    public static <T> JigsawJunction deserialize(final Dynamic<T> dynamic) {
        return new JigsawJunction(dynamic.get("source_x").asInt(0), dynamic.get("source_ground_y").asInt(0), dynamic.get("source_z").asInt(0), dynamic.get("delta_y").asInt(0), StructureTemplatePool.Projection.byName(dynamic.get("dest_proj").asString("")));
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final JigsawJunction cfn3 = (JigsawJunction)object;
        return this.sourceX == cfn3.sourceX && this.sourceZ == cfn3.sourceZ && this.deltaY == cfn3.deltaY && this.destProjection == cfn3.destProjection;
    }
    
    public int hashCode() {
        int integer2 = this.sourceX;
        integer2 = 31 * integer2 + this.sourceGroundY;
        integer2 = 31 * integer2 + this.sourceZ;
        integer2 = 31 * integer2 + this.deltaY;
        integer2 = 31 * integer2 + this.destProjection.hashCode();
        return integer2;
    }
    
    public String toString() {
        return new StringBuilder().append("JigsawJunction{sourceX=").append(this.sourceX).append(", sourceGroundY=").append(this.sourceGroundY).append(", sourceZ=").append(this.sourceZ).append(", deltaY=").append(this.deltaY).append(", destProjection=").append(this.destProjection).append('}').toString();
    }
}
