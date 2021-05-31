package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class OceanRuinConfiguration implements FeatureConfiguration {
    public final OceanRuinFeature.Type biomeTemp;
    public final float largeProbability;
    public final float clusterProbability;
    
    public OceanRuinConfiguration(final OceanRuinFeature.Type b, final float float2, final float float3) {
        this.biomeTemp = b;
        this.largeProbability = float2;
        this.clusterProbability = float3;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("biome_temp"), dynamicOps.createString(this.biomeTemp.getName()), dynamicOps.createString("large_probability"), dynamicOps.createFloat(this.largeProbability), dynamicOps.createString("cluster_probability"), dynamicOps.createFloat(this.clusterProbability))));
    }
    
    public static <T> OceanRuinConfiguration deserialize(final Dynamic<T> dynamic) {
        final OceanRuinFeature.Type b2 = OceanRuinFeature.Type.byName(dynamic.get("biome_temp").asString(""));
        final float float3 = dynamic.get("large_probability").asFloat(0.0f);
        final float float4 = dynamic.get("cluster_probability").asFloat(0.0f);
        return new OceanRuinConfiguration(b2, float3, float4);
    }
}
