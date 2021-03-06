package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.placement.DecoratorChance;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
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

public final class SunflowerPlainsBiome extends Biome {
    protected SunflowerPlainsBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_GRASS).precipitation(Precipitation.RAIN).biomeCategory(BiomeCategory.PLAINS).depth(0.125f).scale(0.05f).temperature(0.8f).downfall(0.4f).waterColor(4159204).waterFogColor(329011).parent("plains"));
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.STRONGHOLD, FeatureConfiguration.NONE);
        BiomeDefaultFeatures.addDefaultCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDefaultLakes(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        BiomeDefaultFeatures.addPlainGrass(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<DoublePlantConfiguration, DecoratorFrequency>makeComposite(Feature.DOUBLE_PLANT, new DoublePlantConfiguration(Blocks.SUNFLOWER.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(10)));
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        BiomeDefaultFeatures.addPlainVegetation(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.REED, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(10)));
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.PUMPKIN, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(32)));
        BiomeDefaultFeatures.addDefaultSprings(this);
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.SHEEP, 12, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.PIG, 10, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.CHICKEN, 10, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.COW, 8, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.HORSE, 5, 2, 6));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.DONKEY, 1, 1, 3));
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
