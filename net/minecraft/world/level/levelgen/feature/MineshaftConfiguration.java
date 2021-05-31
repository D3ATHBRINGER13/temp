package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class MineshaftConfiguration implements FeatureConfiguration {
    public final double probability;
    public final MineshaftFeature.Type type;
    
    public MineshaftConfiguration(final double double1, final MineshaftFeature.Type b) {
        this.probability = double1;
        this.type = b;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.probability), dynamicOps.createString("type"), dynamicOps.createString(this.type.getName()))));
    }
    
    public static <T> MineshaftConfiguration deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        final MineshaftFeature.Type b3 = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
        return new MineshaftConfiguration(float2, b3);
    }
}
