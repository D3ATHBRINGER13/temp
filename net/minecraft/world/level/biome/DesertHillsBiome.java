package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class DesertHillsBiome extends Biome {
    public DesertHillsBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_DESERT).precipitation(Precipitation.NONE).biomeCategory(BiomeCategory.DESERT).depth(0.45f).scale(0.3f).temperature(2.0f).downfall(0.0f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.DESERT_PYRAMID, FeatureConfiguration.NONE);
        this.<MineshaftConfiguration>addStructureStart(Feature.MINESHAFT, new MineshaftConfiguration(0.004, MineshaftFeature.Type.NORMAL));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.STRONGHOLD, FeatureConfiguration.NONE);
        BiomeDefaultFeatures.addDefaultCarvers(this);
        BiomeDefaultFeatures.addStructureFeaturePlacement(this);
        BiomeDefaultFeatures.addDesertLakes(this);
        BiomeDefaultFeatures.addDefaultMonsterRoom(this);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(this);
        BiomeDefaultFeatures.addDefaultOres(this);
        BiomeDefaultFeatures.addDefaultSoftDisks(this);
        BiomeDefaultFeatures.addDefaultFlowers(this);
        BiomeDefaultFeatures.addDefaultGrass(this);
        BiomeDefaultFeatures.addDesertVegetation(this);
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        BiomeDefaultFeatures.addDesertExtraVegetation(this);
        BiomeDefaultFeatures.addDefaultSprings(this);
        BiomeDefaultFeatures.addDesertExtraDecoration(this);
        BiomeDefaultFeatures.addSurfaceFreezing(this);
        this.addSpawn(MobCategory.CREATURE, new SpawnerData(EntityType.RABBIT, 4, 2, 3));
        this.addSpawn(MobCategory.AMBIENT, new SpawnerData(EntityType.BAT, 10, 8, 8));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.WITCH, 5, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE, 19, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE_VILLAGER, 1, 1, 1));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.HUSK, 80, 4, 4));
    }
}
