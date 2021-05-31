package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorFrequencyChance implements DecoratorConfiguration {
    public final int count;
    public final float chance;
    
    public DecoratorFrequencyChance(final int integer, final float float2) {
        this.count = integer;
        this.chance = float2;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("chance"), dynamicOps.createFloat(this.chance))));
    }
    
    public static DecoratorFrequencyChance deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final float float3 = dynamic.get("chance").asFloat(0.0f);
        return new DecoratorFrequencyChance(integer2, float3);
    }
}
