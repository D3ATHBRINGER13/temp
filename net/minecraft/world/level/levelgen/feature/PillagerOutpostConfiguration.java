package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class PillagerOutpostConfiguration implements FeatureConfiguration {
    public final double probability;
    
    public PillagerOutpostConfiguration(final double double1) {
        this.probability = double1;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.probability))));
    }
    
    public static <T> PillagerOutpostConfiguration deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        return new PillagerOutpostConfiguration(float2);
    }
}
