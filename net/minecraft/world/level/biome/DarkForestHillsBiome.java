package net.minecraft.world.level.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.RandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class DarkForestHillsBiome extends Biome {
    public DarkForestHillsBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_GRASS).precipitation(Precipitation.RAIN).biomeCategory(BiomeCategory.FOREST).depth(0.2f).scale(0.4f).temperature(0.7f).downfall(0.8f).waterColor(4159204).waterFogColor(329011).parent("dark_forest"));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.WOODLAND_MANSION, FeatureConfiguration.NONE);
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.STRONGHOLD, FeatureConfiguration.NONE);
        BiomeDefaultFeatures.addDefaultCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDefaultLakes(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, NoneDecoratorConfiguration>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.HUGE_RED_MUSHROOM, Feature.HUGE_BROWN_MUSHROOM, Feature.DARK_OAK_TREE, Feature.FANCY_TREE }, new FeatureConfiguration[] { new HugeMushroomFeatureConfig(false), new HugeMushroomFeatureConfig(false), FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.025f, 0.05f, 0.6666667f, 0.1f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.DARK_OAK_TREE, DecoratorConfiguration.NONE));
        BiomeDefaultFeatures.addForestFlowers(this);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        BiomeDefaultFeatures.addDefaultFlowers(this);
        BiomeDefaultFeatures.addForestGrass(this);
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        BiomeDefaultFeatures.addDefaultExtraVegetation(this);
        BiomeDefaultFeatures.addDefaultSprings(this);
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.SHEEP, 12, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.PIG, 10, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.CHICKEN, 10, 4, 4));
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.COW, 8, 4, 4));
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
    
    @Override
    public int getGrassColor(final BlockPos ew) {
        final int integer3 = super.getGrassColor(ew);
        return (integer3 & 0xFEFEFE) + 2634762 >> 1;
    }
}
