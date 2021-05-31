package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;

public class RandomFeatureConfig implements FeatureConfiguration {
    public final List<WeightedConfiguredFeature<?>> features;
    public final ConfiguredFeature<?> defaultFeature;
    
    public RandomFeatureConfig(final List<WeightedConfiguredFeature<?>> list, final ConfiguredFeature<?> cal) {
        this.features = list;
        this.defaultFeature = cal;
    }
    
    public RandomFeatureConfig(final Feature<?>[] arr, final FeatureConfiguration[] arr, final float[] arr, final Feature<?> cbn, final FeatureConfiguration cbo) {
        this((List<WeightedConfiguredFeature<?>>)IntStream.range(0, arr.length).mapToObj(integer -> RandomFeatureConfig.<FeatureConfiguration>getWeightedConfiguredFeature((Feature<FeatureConfiguration>)arr[integer], arr[integer], arr[integer])).collect(Collectors.toList()), RandomFeatureConfig.getDefaultFeature(cbn, cbo));
    }
    
    private static <FC extends FeatureConfiguration> WeightedConfiguredFeature<FC> getWeightedConfiguredFeature(final Feature<FC> cbn, final FeatureConfiguration cbo, final float float3) {
        return new WeightedConfiguredFeature<FC>(cbn, (FC)cbo, float3);
    }
    
    private static <FC extends FeatureConfiguration> ConfiguredFeature<FC> getDefaultFeature(final Feature<FC> cbn, final FeatureConfiguration cbo) {
        return new ConfiguredFeature<FC>(cbn, (FC)cbo);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)dynamicOps.createList(this.features.stream().map(cfi -> cfi.serialize(dynamicOps).getValue()));
        final T object4 = (T)this.defaultFeature.<T>serialize(dynamicOps).getValue();
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("features"), object3, dynamicOps.createString("default"), object4)));
    }
    
    public static <T> RandomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<WeightedConfiguredFeature<?>> list2 = (List<WeightedConfiguredFeature<?>>)dynamic.get("features").asList(WeightedConfiguredFeature::deserialize);
        final ConfiguredFeature<?> cal3 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("default").orElseEmptyMap());
        return new RandomFeatureConfig(list2, cal3);
    }
}
