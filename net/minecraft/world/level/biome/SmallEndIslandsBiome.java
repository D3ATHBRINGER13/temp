package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public class SmallEndIslandsBiome extends Biome {
    public SmallEndIslandsBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_THEEND).precipitation(Precipitation.NONE).biomeCategory(BiomeCategory.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addFeature(GenerationStep.Decoration.RAW_GENERATION, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.END_ISLAND, FeatureConfiguration.NONE, FeatureDecorator.END_ISLAND, DecoratorConfiguration.NONE));
        BiomeDefaultFeatures.addEndCity(this);
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
    }
    
    @Override
    public int getSkyColor(final float float1) {
        return 0;
    }
}
