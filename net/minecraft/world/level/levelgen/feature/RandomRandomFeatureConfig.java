package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;

public class RandomRandomFeatureConfig implements FeatureConfiguration {
    public final List<ConfiguredFeature<?>> features;
    public final int count;
    
    public RandomRandomFeatureConfig(final List<ConfiguredFeature<?>> list, final int integer) {
        this.features = list;
        this.count = integer;
    }
    
    public RandomRandomFeatureConfig(final Feature<?>[] arr, final FeatureConfiguration[] arr, final int integer) {
        this((List<ConfiguredFeature<?>>)IntStream.range(0, arr.length).mapToObj(integer -> RandomRandomFeatureConfig.<FeatureConfiguration>getConfiguredFeature((Feature<FeatureConfiguration>)arr[integer], arr[integer])).collect(Collectors.toList()), integer);
    }
    
    private static <FC extends FeatureConfiguration> ConfiguredFeature<?> getConfiguredFeature(final Feature<FC> cbn, final FeatureConfiguration cbo) {
        return new ConfiguredFeature<>(cbn, cbo);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("features"), dynamicOps.createList(this.features.stream().map(cal -> cal.serialize(dynamicOps).getValue())), dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
    }
    
    public static <T> RandomRandomFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<ConfiguredFeature<?>> list2 = (List<ConfiguredFeature<?>>)dynamic.get("features").asList(ConfiguredFeature::deserialize);
        final int integer3 = dynamic.get("count").asInt(0);
        return new RandomRandomFeatureConfig(list2, integer3);
    }
}
