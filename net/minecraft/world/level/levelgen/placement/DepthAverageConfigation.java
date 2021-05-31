package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DepthAverageConfigation implements DecoratorConfiguration {
    public final int count;
    public final int baseline;
    public final int spread;
    
    public DepthAverageConfigation(final int integer1, final int integer2, final int integer3) {
        this.count = integer1;
        this.baseline = integer2;
        this.spread = integer3;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("baseline"), dynamicOps.createInt(this.baseline), dynamicOps.createString("spread"), dynamicOps.createInt(this.spread))));
    }
    
    public static DepthAverageConfigation deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final int integer3 = dynamic.get("baseline").asInt(0);
        final int integer4 = dynamic.get("spread").asInt(0);
        return new DepthAverageConfigation(integer2, integer3, integer4);
    }
}
