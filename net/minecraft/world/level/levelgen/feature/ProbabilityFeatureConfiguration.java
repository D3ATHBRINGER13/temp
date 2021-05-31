package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

public class ProbabilityFeatureConfiguration implements CarverConfiguration, FeatureConfiguration {
    public final float probability;
    
    public ProbabilityFeatureConfiguration(final float float1) {
        this.probability = float1;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
    
    public static <T> ProbabilityFeatureConfiguration deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        return new ProbabilityFeatureConfiguration(float2);
    }
}
