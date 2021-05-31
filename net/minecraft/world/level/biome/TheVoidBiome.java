package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class TheVoidBiome extends Biome {
    public TheVoidBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.CONFIG_STONE).precipitation(Precipitation.NONE).biomeCategory(BiomeCategory.NONE).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.VOID_START_PLATFORM, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
    }
}
