package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HellSpringConfiguration implements FeatureConfiguration {
    public final boolean insideRock;
    
    public HellSpringConfiguration(final boolean boolean1) {
        this.insideRock = boolean1;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("inside_rock"), dynamicOps.createBoolean(this.insideRock))));
    }
    
    public static <T> HellSpringConfiguration deserialize(final Dynamic<T> dynamic) {
        final boolean boolean2 = dynamic.get("inside_rock").asBoolean(false);
        return new HellSpringConfiguration(boolean2);
    }
}
