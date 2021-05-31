package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public class EndHighlandsBiome extends Biome {
    public EndHighlandsBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.CONFIG_THEEND).precipitation(Precipitation.NONE).biomeCategory(BiomeCategory.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.END_CITY, FeatureConfiguration.NONE);
        this.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<EndGatewayConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.END_GATEWAY, EndGatewayConfiguration.knownExit(TheEndDimension.END_SPAWN_POINT, true), FeatureDecorator.END_GATEWAY, DecoratorConfiguration.NONE));
        BiomeDefaultFeatures.addEndCity(this);
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.CHORUS_PLANT, FeatureConfiguration.NONE, FeatureDecorator.CHORUS_PLANT, DecoratorConfiguration.NONE));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
    }
    
    @Override
    public int getSkyColor(final float float1) {
        return 0;
    }
}
