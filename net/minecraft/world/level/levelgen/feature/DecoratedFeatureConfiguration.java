package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.Registry;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

public class DecoratedFeatureConfiguration implements FeatureConfiguration {
    public final ConfiguredFeature<?> feature;
    public final ConfiguredDecorator<?> decorator;
    
    public DecoratedFeatureConfiguration(final ConfiguredFeature<?> cal, final ConfiguredDecorator<?> cgg) {
        this.feature = cal;
        this.decorator = cgg;
    }
    
    public <F extends FeatureConfiguration, D extends DecoratorConfiguration> DecoratedFeatureConfiguration(final Feature<F> cbn, final F cbo, final FeatureDecorator<D> che, final D cax) {
        this(new ConfiguredFeature<>(cbn, cbo), new ConfiguredDecorator<>(che, cax));
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("feature"), this.feature.<T>serialize(dynamicOps).getValue(), dynamicOps.createString("decorator"), this.decorator.<T>serialize(dynamicOps).getValue())));
    }
    
    public String toString() {
        return String.format("< %s [%s | %s] >", new Object[] { this.getClass().getSimpleName(), Registry.FEATURE.getKey(this.feature.feature), Registry.DECORATOR.getKey(this.decorator.decorator) });
    }
    
    public static <T> DecoratedFeatureConfiguration deserialize(final Dynamic<T> dynamic) {
        final ConfiguredFeature<?> cal2 = ConfiguredFeature.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("feature").orElseEmptyMap());
        final ConfiguredDecorator<?> cgg3 = ConfiguredDecorator.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("decorator").orElseEmptyMap());
        return new DecoratedFeatureConfiguration(cal2, cgg3);
    }
}
