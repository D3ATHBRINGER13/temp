package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class FeatureRadius implements FeatureConfiguration {
    public final int radius;
    
    public FeatureRadius(final int integer) {
        this.radius = integer;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius))));
    }
    
    public static <T> FeatureRadius deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("radius").asInt(0);
        return new FeatureRadius(integer2);
    }
}
