package net.minecraft.world.level.levelgen.feature;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;

public class ConfiguredFeature<FC extends FeatureConfiguration> {
    public final Feature<FC> feature;
    public final FC config;
    
    public ConfiguredFeature(final Feature<FC> cbn, final FC cbo) {
        this.feature = cbn;
        this.config = cbo;
    }
    
    public ConfiguredFeature(final Feature<FC> cbn, final Dynamic<?> dynamic) {
        this((Feature<FeatureConfiguration>)cbn, cbn.createSettings(dynamic));
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.FEATURE.getKey(this.feature).toString()), dynamicOps.createString("config"), this.config.<T>serialize(dynamicOps).getValue())));
    }
    
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew) {
        return this.feature.place(bhs, bxi, random, ew, this.config);
    }
    
    public static <T> ConfiguredFeature<?> deserialize(final Dynamic<T> dynamic) {
        final Feature<? extends FeatureConfiguration> cbn2 = Registry.FEATURE.get(new ResourceLocation(dynamic.get("name").asString("")));
        return new ConfiguredFeature<>(cbn2, dynamic.get("config").orElseEmptyMap());
    }
}
