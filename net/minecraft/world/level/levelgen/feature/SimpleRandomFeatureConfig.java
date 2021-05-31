package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;

public class SimpleRandomFeatureConfig implements FeatureConfiguration {
    public final List<ConfiguredFeature<?>> features;
    
    public SimpleRandomFeatureConfig(final List<ConfiguredFeature<?>> list) {
        this.features = list;
    }
    
    public SimpleRandomFeatureConfig(final Feature<?>[] arr, final FeatureConfiguration[] arr) {
        this((List<ConfiguredFeature<?>>)IntStream.range(0, arr.length).mapToObj(integer -> SimpleRandomFeatureConfig.<FeatureConfiguration>getConfiguredFeature((Feature<FeatureConfiguration>)arr[integer], arr[integer])).collect(Collectors.toList()));
    }
    
    private static <FC extends FeatureConfiguration> ConfiguredFeature<FC> getConfiguredFeature(final Feature<FC> cbn, final FeatureConfiguration cbo) {
        return new ConfiguredFeature<FC>(cbn, (FC)cbo);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("features"), dynamicOps.createList(this.features.stream().map(cal -> cal.serialize(dynamicOps).getValue())))));
    }
    
    public static <T> SimpleRandomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<ConfiguredFeature<?>> list2 = (List<ConfiguredFeature<?>>)dynamic.get("features").asList(ConfiguredFeature::deserialize);
        return new SimpleRandomFeatureConfig(list2);
    }
}
