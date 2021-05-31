package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequencyWithExtraChance;
import net.minecraft.world.level.levelgen.feature.RandomFeatureConfig;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.DoublePlantConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class ForestFlowerBiome extends Biome {
    public ForestFlowerBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_GRASS).precipitation(Precipitation.RAIN).biomeCategory(BiomeCategory.FOREST).depth(0.1f).scale(0.4f).temperature(0.7f).downfall(0.8f).waterColor(4159204).waterFogColor(329011).parent("forest"));
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.STRONGHOLD, FeatureConfiguration.NONE);
        BiomeDefaultFeatures.addDefaultCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDefaultLakes(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomRandomFeatureConfig, DecoratorFrequency>makeComposite(Feature.RANDOM_RANDOM_SELECTOR, new RandomRandomFeatureConfig(new Feature[] { Feature.DOUBLE_PLANT, Feature.DOUBLE_PLANT, Feature.DOUBLE_PLANT, Feature.GENERAL_FOREST_FLOWER }, new FeatureConfiguration[] { new DoublePlantConfiguration(Blocks.LILAC.defaultBlockState()), new DoublePlantConfiguration(Blocks.ROSE_BUSH.defaultBlockState()), new DoublePlantConfiguration(Blocks.PEONY.defaultBlockState()), FeatureConfiguration.NONE }, 2), FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(5)));
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.BIRCH_TREE, Feature.FANCY_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.2f, 0.1f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(6, 0.1f, 1)));
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.FOREST_FLOWER, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(100)));
        BiomeDefaultFeatures.addDefaultGrass(this);
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        BiomeDefaultFeatures.addDefaultExtraVegetation(this);
        BiomeDefaultFeatures.addDefaultSprings(this);
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.SHEEP, 12, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.PIG, 10, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.CHICKEN, 10, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.COW, 8, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.RABBIT, 4, 2, 3));
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
