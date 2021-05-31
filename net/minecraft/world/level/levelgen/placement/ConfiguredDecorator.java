package net.minecraft.world.level.levelgen.placement;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class ConfiguredDecorator<DC extends DecoratorConfiguration> {
    public final FeatureDecorator<DC> decorator;
    public final DC config;
    
    public ConfiguredDecorator(final FeatureDecorator<DC> che, final Dynamic<?> dynamic) {
        this((FeatureDecorator<DecoratorConfiguration>)che, che.createSettings(dynamic));
    }
    
    public ConfiguredDecorator(final FeatureDecorator<DC> che, final DC cax) {
        this.decorator = che;
        this.config = cax;
    }
    
    public <FC extends FeatureConfiguration> boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final ConfiguredFeature<FC> cal) {
        return this.decorator.<FC>placeFeature(bhs, bxi, random, ew, this.config, cal);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.DECORATOR.getKey(this.decorator).toString()), dynamicOps.createString("config"), this.config.<T>serialize(dynamicOps).getValue())));
    }
    
    public static <T> ConfiguredDecorator<?> deserialize(final Dynamic<T> dynamic) {
        final FeatureDecorator<? extends DecoratorConfiguration> che2 = Registry.DECORATOR.get(new ResourceLocation(dynamic.get("name").asString("")));
        return new ConfiguredDecorator<>(che2, dynamic.get("config").orElseEmptyMap());
    }
}
