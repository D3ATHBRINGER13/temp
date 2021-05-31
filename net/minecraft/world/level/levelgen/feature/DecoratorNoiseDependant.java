package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DecoratorNoiseDependant implements DecoratorConfiguration {
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;
    
    public DecoratorNoiseDependant(final double double1, final int integer2, final int integer3) {
        this.noiseLevel = double1;
        this.belowNoise = integer2;
        this.aboveNoise = integer3;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("noise_level"), dynamicOps.createDouble(this.noiseLevel), dynamicOps.createString("below_noise"), dynamicOps.createInt(this.belowNoise), dynamicOps.createString("above_noise"), dynamicOps.createInt(this.aboveNoise))));
    }
    
    public static DecoratorNoiseDependant deserialize(final Dynamic<?> dynamic) {
        final double double2 = dynamic.get("noise_level").asDouble(0.0);
        final int integer4 = dynamic.get("below_noise").asInt(0);
        final int integer5 = dynamic.get("above_noise").asInt(0);
        return new DecoratorNoiseDependant(double2, integer4, integer5);
    }
}
