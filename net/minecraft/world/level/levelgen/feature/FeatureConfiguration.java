package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface FeatureConfiguration {
    public static final NoneFeatureConfiguration NONE = new NoneFeatureConfiguration();
    
     <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps);
}
