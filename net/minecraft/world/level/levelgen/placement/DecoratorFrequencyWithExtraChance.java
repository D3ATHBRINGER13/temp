package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorFrequencyWithExtraChance implements DecoratorConfiguration {
    public final int count;
    public final float extraChance;
    public final int extraCount;
    
    public DecoratorFrequencyWithExtraChance(final int integer1, final float float2, final int integer3) {
        this.count = integer1;
        this.extraChance = float2;
        this.extraCount = integer3;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("extra_chance"), dynamicOps.createFloat(this.extraChance), dynamicOps.createString("extra_count"), dynamicOps.createInt(this.extraCount))));
    }
    
    public static DecoratorFrequencyWithExtraChance deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final float float3 = dynamic.get("extra_chance").asFloat(0.0f);
        final int integer3 = dynamic.get("extra_count").asInt(0);
        return new DecoratorFrequencyWithExtraChance(integer2, float3, integer3);
    }
}
