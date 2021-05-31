package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoneFeatureConfiguration implements FeatureConfiguration {
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    public static <T> NoneFeatureConfiguration deserialize(final Dynamic<T> dynamic) {
        return FeatureConfiguration.NONE;
    }
}
