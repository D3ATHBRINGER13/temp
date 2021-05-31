package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeagrassFeatureConfiguration implements FeatureConfiguration {
    public final int count;
    public final double tallSeagrassProbability;
    
    public SeagrassFeatureConfiguration(final int integer, final double double2) {
        this.count = integer;
        this.tallSeagrassProbability = double2;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("tall_seagrass_probability"), dynamicOps.createDouble(this.tallSeagrassProbability))));
    }
    
    public static <T> SeagrassFeatureConfiguration deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final double double3 = dynamic.get("tall_seagrass_probability").asDouble(0.0);
        return new SeagrassFeatureConfiguration(integer2, double3);
    }
}
