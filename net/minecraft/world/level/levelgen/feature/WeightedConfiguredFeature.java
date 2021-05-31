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

public class WeightedConfiguredFeature<FC extends FeatureConfiguration> {
    public final Feature<FC> feature;
    public final FC config;
    public final Float chance;
    
    public WeightedConfiguredFeature(final Feature<FC> cbn, final FC cbo, final Float float3) {
        this.feature = cbn;
        this.config = cbo;
        this.chance = float3;
    }
    
    public WeightedConfiguredFeature(final Feature<FC> cbn, final Dynamic<?> dynamic, final float float3) {
        this((Feature<FeatureConfiguration>)cbn, cbn.createSettings(dynamic), float3);
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.FEATURE.getKey(this.feature).toString()), dynamicOps.createString("config"), this.config.<T>serialize(dynamicOps).getValue(), dynamicOps.createString("chance"), dynamicOps.createFloat((float)this.chance))));
    }
    
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew) {
        return this.feature.place(bhs, bxi, random, ew, this.config);
    }
    
    public static <T> WeightedConfiguredFeature<?> deserialize(final Dynamic<T> dynamic) {
        final Feature<? extends FeatureConfiguration> cbn2 = Registry.FEATURE.get(new ResourceLocation(dynamic.get("name").asString("")));
        return new WeightedConfiguredFeature<>(cbn2, dynamic.get("config").orElseEmptyMap(), dynamic.get("chance").asFloat(0.0f));
    }
}
