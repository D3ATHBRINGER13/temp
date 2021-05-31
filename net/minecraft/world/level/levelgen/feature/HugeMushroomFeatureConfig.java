package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HugeMushroomFeatureConfig implements FeatureConfiguration {
    public final boolean planted;
    
    public HugeMushroomFeatureConfig(final boolean boolean1) {
        this.planted = boolean1;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("planted"), dynamicOps.createBoolean(this.planted))));
    }
    
    public static <T> HugeMushroomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final boolean boolean2 = dynamic.get("planted").asBoolean(false);
        return new HugeMushroomFeatureConfig(boolean2);
    }
}
