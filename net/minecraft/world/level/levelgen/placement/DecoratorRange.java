package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorRange implements DecoratorConfiguration {
    public final int min;
    public final int max;
    
    public DecoratorRange(final int integer1, final int integer2) {
        this.min = integer1;
        this.max = integer2;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("min"), dynamicOps.createInt(this.min), dynamicOps.createString("max"), dynamicOps.createInt(this.max))));
    }
    
    public static DecoratorRange deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("min").asInt(0);
        final int integer3 = dynamic.get("max").asInt(0);
        return new DecoratorRange(integer2, integer3);
    }
}
