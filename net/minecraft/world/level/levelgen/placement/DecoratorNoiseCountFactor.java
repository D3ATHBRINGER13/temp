package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorNoiseCountFactor implements DecoratorConfiguration {
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;
    public final Heightmap.Types heightmap;
    
    public DecoratorNoiseCountFactor(final int integer, final double double2, final double double3, final Heightmap.Types a) {
        this.noiseToCountRatio = integer;
        this.noiseFactor = double2;
        this.noiseOffset = double3;
        this.heightmap = a;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("noise_to_count_ratio"), dynamicOps.createInt(this.noiseToCountRatio), dynamicOps.createString("noise_factor"), dynamicOps.createDouble(this.noiseFactor), dynamicOps.createString("noise_offset"), dynamicOps.createDouble(this.noiseOffset), dynamicOps.createString("heightmap"), dynamicOps.createString(this.heightmap.getSerializationKey()))));
    }
    
    public static DecoratorNoiseCountFactor deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("noise_to_count_ratio").asInt(10);
        final double double3 = dynamic.get("noise_factor").asDouble(80.0);
        final double double4 = dynamic.get("noise_offset").asDouble(0.0);
        final Heightmap.Types a7 = Heightmap.Types.getFromKey(dynamic.get("heightmap").asString("OCEAN_FLOOR_WG"));
        return new DecoratorNoiseCountFactor(integer2, double3, double4, a7);
    }
}
