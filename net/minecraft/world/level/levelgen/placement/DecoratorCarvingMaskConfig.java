package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorCarvingMaskConfig implements DecoratorConfiguration {
    protected final GenerationStep.Carving step;
    protected final float probability;
    
    public DecoratorCarvingMaskConfig(final GenerationStep.Carving a, final float float2) {
        this.step = a;
        this.probability = float2;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("step"), dynamicOps.createString(this.step.toString()), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
    
    public static DecoratorCarvingMaskConfig deserialize(final Dynamic<?> dynamic) {
        final GenerationStep.Carving a2 = GenerationStep.Carving.valueOf(dynamic.get("step").asString(""));
        final float float3 = dynamic.get("probability").asFloat(0.0f);
        return new DecoratorCarvingMaskConfig(a2, float3);
    }
}
