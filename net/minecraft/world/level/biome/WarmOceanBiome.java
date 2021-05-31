package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.placement.DecoratorChance;
import net.minecraft.world.level.levelgen.feature.CountFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.DecoratorNoiseCountFactor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public class WarmOceanBiome extends Biome {
    public WarmOceanBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_FULL_SAND).precipitation(Precipitation.RAIN).biomeCategory(BiomeCategory.OCEAN).depth(-1.0f).scale(0.1f).temperature(0.5f).downfall(0.5f).waterColor(4445678).waterFogColor(270131).parent(null));
        this.<OceanRuinConfiguration>addStructureStart(Feature.OCEAN_RUIN, new OceanRuinConfiguration(OceanRuinFeature.Type.WARM, 0.3f, 0.9f));
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<ShipwreckConfiguration>addStructureStart(Feature.SHIPWRECK, new ShipwreckConfiguration(false));
        BiomeDefaultFeatures.addOceanCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDefaultLakes(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        BiomeDefaultFeatures.addWaterTrees(this);
        BiomeDefaultFeatures.addDefaultFlowers(this);
        BiomeDefaultFeatures.addDefaultGrass(this);
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        BiomeDefaultFeatures.addDefaultExtraVegetation(this);
        BiomeDefaultFeatures.addDefaultSprings(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SimpleRandomFeatureConfig, DecoratorNoiseCountFactor>makeComposite(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfig(new Feature[] { Feature.CORAL_TREE, Feature.CORAL_CLAW, Feature.CORAL_MUSHROOM }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE, FeatureConfiguration.NONE }), FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED, new DecoratorNoiseCountFactor(20, 400.0, 0.0, Heightmap.Types.OCEAN_FLOOR_WG)));
        BiomeDefaultFeatures.addWarmSeagrass(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<CountFeatureConfiguration, DecoratorChance>makeComposite(Feature.SEA_PICKLE, new CountFeatureConfiguration(20), FeatureDecorator.CHANCE_TOP_SOLID_HEIGHTMAP, new DecoratorChance(16)));
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.WATER_CREATURE, new SpawnerData(EntityType.SQUID, 10, 4, 4));
        this.addSpawn(MobCategory.WATER_CREATURE, new SpawnerData(EntityType.PUFFERFISH, 15, 1, 3));
        this.addSpawn(MobCategory.WATER_CREATURE, new SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
        this.addSpawn(MobCategory.WATER_CREATURE, new SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
        this.addSpawn(MobCategory.AMBIENT, new SpawnerData(EntityType.BAT, 10, 8, 8));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.WITCH, 5, 1, 1));
    }
}
