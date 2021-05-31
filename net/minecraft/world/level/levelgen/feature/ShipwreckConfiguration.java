package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ShipwreckConfiguration implements FeatureConfiguration {
    public final boolean isBeached;
    
    public ShipwreckConfiguration(final boolean boolean1) {
        this.isBeached = boolean1;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("is_beached"), dynamicOps.createBoolean(this.isBeached))));
    }
    
    public static <T> ShipwreckConfiguration deserialize(final Dynamic<T> dynamic) {
        final boolean boolean2 = dynamic.get("is_beached").asBoolean(false);
        return new ShipwreckConfiguration(boolean2);
    }
}
