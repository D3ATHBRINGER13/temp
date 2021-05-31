package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class BuriedTreasureConfiguration implements FeatureConfiguration {
    public final float probability;
    
    public BuriedTreasureConfiguration(final float float1) {
        this.probability = float1;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
    
    public static <T> BuriedTreasureConfiguration deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        return new BuriedTreasureConfiguration(float2);
    }
}
