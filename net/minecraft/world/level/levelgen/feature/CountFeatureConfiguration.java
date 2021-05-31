package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountFeatureConfiguration implements FeatureConfiguration {
    public final int count;
    
    public CountFeatureConfiguration(final int integer) {
        this.count = integer;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
    }
    
    public static <T> CountFeatureConfiguration deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        return new CountFeatureConfiguration(integer2);
    }
}
