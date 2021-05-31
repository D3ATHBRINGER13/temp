package net.minecraft.world.level.biome;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorChanceRange;
import net.minecraft.world.level.levelgen.feature.BushConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.feature.HellSpringConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.SpringConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

public final class NetherBiome extends Biome {
    protected NetherBiome() {
        super(new BiomeBuilder().<SurfaceBuilderBaseConfiguration>surfaceBuilder(SurfaceBuilder.NETHER, SurfaceBuilder.CONFIG_HELL).precipitation(Precipitation.NONE).biomeCategory(BiomeCategory.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.<NoneFeatureConfiguration>addStructureStart(Feature.NETHER_BRIDGE, FeatureConfiguration.NONE);
        this.<CarverConfiguration>addCarver(GenerationStep.Carving.AIR, Biome.makeCarver((WorldCarver<C>)WorldCarver.HELL_CAVE, (C)new ProbabilityFeatureConfiguration(0.2f)));
        this.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SpringConfiguration, DecoratorCountRange>makeComposite(Feature.SPRING, new SpringConfiguration(Fluids.LAVA.defaultFluidState()), FeatureDecorator.COUNT_VERY_BIASED_RANGE, new DecoratorCountRange(20, 8, 16, 256)));
        BiomeDefaultFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.NETHER_BRIDGE, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<HellSpringConfiguration, DecoratorCountRange>makeComposite(Feature.NETHER_SPRING, new HellSpringConfiguration(false), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(8, 4, 8, 128)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.HELL_FIRE, FeatureConfiguration.NONE, FeatureDecorator.HELL_FIRE, new DecoratorFrequency(10)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.GLOWSTONE_BLOB, FeatureConfiguration.NONE, FeatureDecorator.LIGHT_GEM_CHANCE, new DecoratorFrequency(10)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorCountRange>makeComposite(Feature.GLOWSTONE_BLOB, FeatureConfiguration.NONE, FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(10, 0, 0, 128)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<BushConfiguration, DecoratorChanceRange>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.BROWN_MUSHROOM.defaultBlockState()), FeatureDecorator.CHANCE_RANGE, new DecoratorChanceRange(0.5f, 0, 0, 128)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<BushConfiguration, DecoratorChanceRange>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.RED_MUSHROOM.defaultBlockState()), FeatureDecorator.CHANCE_RANGE, new DecoratorChanceRange(0.5f, 0, 0, 128)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(16, 10, 20, 128)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<OreConfiguration, DecoratorFrequency>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NETHERRACK, Blocks.MAGMA_BLOCK.defaultBlockState(), 33), FeatureDecorator.MAGMA, new DecoratorFrequency(4)));
        this.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<HellSpringConfiguration, DecoratorCountRange>makeComposite(Feature.NETHER_SPRING, new HellSpringConfiguration(true), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(16, 10, 20, 128)));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.GHAST, 50, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.MAGMA_CUBE, 2, 4, 4));
        this.addSpawn(MobCategory.MONSTER, new SpawnerData(EntityType.ENDERMAN, 1, 4, 4));
    }
}
