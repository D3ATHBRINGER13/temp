package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DecoratorChanceRange implements DecoratorConfiguration {
    public final float chance;
    public final int bottomOffset;
    public final int topOffset;
    public final int top;
    
    public DecoratorChanceRange(final float float1, final int integer2, final int integer3, final int integer4) {
        this.chance = float1;
        this.bottomOffset = integer2;
        this.topOffset = integer3;
        this.top = integer4;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createFloat(this.chance), dynamicOps.createString("bottom_offset"), dynamicOps.createInt(this.bottomOffset), dynamicOps.createString("top_offset"), dynamicOps.createInt(this.topOffset), dynamicOps.createString("top"), dynamicOps.createInt(this.top))));
    }
    
    public static DecoratorChanceRange deserialize(final Dynamic<?> dynamic) {
        final float float2 = dynamic.get("chance").asFloat(0.0f);
        final int integer3 = dynamic.get("bottom_offset").asInt(0);
        final int integer4 = dynamic.get("top_offset").asInt(0);
        final int integer5 = dynamic.get("top").asInt(0);
        return new DecoratorChanceRange(float2, integer3, integer4, integer5);
    }
}
