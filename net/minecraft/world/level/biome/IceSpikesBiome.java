package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.FeatureRadius;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class IceSpikesBiome extends Biome {
    public IceSpikesBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, new SurfaceBuilderBaseConfiguration(Blocks.SNOW_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.GRAVEL.defaultBlockState())).precipitation(Precipitation.SNOW).biomeCategory(BiomeCategory.ICY).depth(0.425f).scale(0.45000002f).temperature(0.0f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent("snowy_tundra"));
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.STRONGHOLD, FeatureConfiguration.NONE);
        BiomeDefaultFeatures.addDefaultCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDefaultLakes(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        this.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.ICE_SPIKE, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP, new DecoratorFrequency(3)));
        this.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<FeatureRadius, DecoratorFrequency>makeComposite(Feature.ICE_PATCH, new FeatureRadius(2), FeatureDecorator.COUNT_HEIGHTMAP, new DecoratorFrequency(2)));
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        BiomeDefaultFeatures.addSnowyTrees(this);
        BiomeDefaultFeatures.addDefaultFlowers(this);
        BiomeDefaultFeatures.addDefaultGrass(this);
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        BiomeDefaultFeatures.addDefaultExtraVegetation(this);
        BiomeDefaultFeatures.addDefaultSprings(this);
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.RABBIT, 10, 2, 3));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
        this.addSpawn(MobCategory.AMBIENT, new SpawnerData(EntityType.BAT, 10, 8, 8));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.WITCH, 5, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SKELETON, 20, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.STRAY, 80, 4, 4));
    }
    
    @Override
    public float getCreatureProbability() {
        return 0.07f;
    }
}
