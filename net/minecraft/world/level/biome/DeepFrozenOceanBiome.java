package net.minecraft.world.level.biome;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class DeepFrozenOceanBiome extends Biome {
    protected static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE;
    
    public DeepFrozenOceanBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.FROZEN_OCEAN, SurfaceBuilder.CONFIG_GRASS).precipitation(Precipitation.RAIN).biomeCategory(BiomeCategory.OCEAN).depth(-1.8f).scale(0.1f).temperature(0.5f).downfall(0.5f).waterColor(3750089).waterFogColor(329011).parent(null));
        this.<OceanRuinConfiguration>addStructureStart(Feature.OCEAN_RUIN, new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3f, 0.9f));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.OCEAN_MONUMENT, FeatureConfiguration.NONE);
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<ShipwreckConfiguration>addStructureStart(Feature.SHIPWRECK, new ShipwreckConfiguration(false));
        BiomeDefaultFeatures.addOceanCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDefaultLakes(this);
        BiomeDefaultFeatures.addIcebergs(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        BiomeDefaultFeatures.addBlueIce(this);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        BiomeDefaultFeatures.addWaterTrees(this);
        BiomeDefaultFeatures.addDefaultFlowers(this);
        BiomeDefaultFeatures.addDefaultGrass(this);
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        BiomeDefaultFeatures.addDefaultExtraVegetation(this);
        BiomeDefaultFeatures.addDefaultSprings(this);
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.WATER_CREATURE, new SpawnerData(EntityType.SQUID, 1, 1, 4));
        this.addSpawn(MobCategory.WATER_CREATURE, new SpawnerData(EntityType.SALMON, 15, 1, 5));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
        this.addSpawn(MobCategory.AMBIENT, new SpawnerData(EntityType.BAT, 10, 8, 8));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.DROWNED, 5, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.WITCH, 5, 1, 1));
    }
    
    @Override
    protected float getTemperatureNoCache(final BlockPos ew) {
        float float3 = this.getTemperature();
        final double double4 = DeepFrozenOceanBiome.FROZEN_TEMPERATURE_NOISE.getValue(ew.getX() * 0.05, ew.getZ() * 0.05);
        final double double5 = DeepFrozenOceanBiome.BIOME_INFO_NOISE.getValue(ew.getX() * 0.2, ew.getZ() * 0.2);
        final double double6 = double4 + double5;
        if (double6 < 0.3) {
            final double double7 = DeepFrozenOceanBiome.BIOME_INFO_NOISE.getValue(ew.getX() * 0.09, ew.getZ() * 0.09);
            if (double7 < 0.8) {
                float3 = 0.2f;
            }
        }
        if (ew.getY() > 64) {
            final float float4 = (float)(DeepFrozenOceanBiome.TEMPERATURE_NOISE.getValue(ew.getX() / 8.0f, ew.getZ() / 8.0f) * 4.0);
            return float3 - (float4 + ew.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return float3;
    }
    
    static {
        FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(new Random(3456L), 3);
    }
}
