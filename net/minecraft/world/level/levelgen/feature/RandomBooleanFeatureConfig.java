package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RandomBooleanFeatureConfig implements FeatureConfiguration {
    public final ConfiguredFeature<?> featureTrue;
    public final ConfiguredFeature<?> featureFalse;
    
    public RandomBooleanFeatureConfig(final ConfiguredFeature<?> cal1, final ConfiguredFeature<?> cal2) {
        this.featureTrue = cal1;
        this.featureFalse = cal2;
    }
    
    public RandomBooleanFeatureConfig(final Feature<?> cbn1, final FeatureConfiguration cbo2, final Feature<?> cbn3, final FeatureConfiguration cbo4) {
        this(RandomBooleanFeatureConfig.getFeature(cbn1, cbo2), RandomBooleanFeatureConfig.getFeature(cbn3, cbo4));
    }
    
    private static <FC extends FeatureConfiguration> ConfiguredFeature<FC> getFeature(final Feature<FC> cbn, final FeatureConfiguration cbo) {
        return new ConfiguredFeature<FC>(cbn, (FC)cbo);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("feature_true"), this.featureTrue.<T>serialize(dynamicOps).getValue(), dynamicOps.createString("feature_false"), this.featureFalse.<T>serialize(dynamicOps).getValue())));
    }
    
    public static <T> RandomBooleanFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final ConfiguredFeature<?> cal2 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature_true").orElseEmptyMap());
        final ConfiguredFeature<?> cal3 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature_false").orElseEmptyMap());
        return new RandomBooleanFeatureConfig(cal2, cal3);
    }
}
