package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DecoratorCountRange implements DecoratorConfiguration {
    public final int count;
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;
    
    public DecoratorCountRange(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.count = integer1;
        this.bottomOffset = integer2;
        this.topOffset = integer3;
        this.maximum = integer4;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("bottom_offset"), dynamicOps.createInt(this.bottomOffset), dynamicOps.createString("top_offset"), dynamicOps.createInt(this.topOffset), dynamicOps.createString("maximum"), dynamicOps.createInt(this.maximum))));
    }
    
    public static DecoratorCountRange deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final int integer3 = dynamic.get("bottom_offset").asInt(0);
        final int integer4 = dynamic.get("top_offset").asInt(0);
        final int integer5 = dynamic.get("maximum").asInt(0);
        return new DecoratorCountRange(integer2, integer3, integer4, integer5);
    }
}
