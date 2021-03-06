package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.IcebergConfiguration;
import net.minecraft.world.level.levelgen.feature.SpringConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.levelgen.feature.SeagrassFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.DecoratorCarvingMaskConfig;
import net.minecraft.world.level.levelgen.feature.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorNoiseDependant;
import net.minecraft.world.level.levelgen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequencyChance;
import net.minecraft.world.level.levelgen.feature.BushConfiguration;
import net.minecraft.world.level.levelgen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.GrassConfiguration;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequencyWithExtraChance;
import net.minecraft.world.level.levelgen.feature.RandomFeatureConfig;
import net.minecraft.world.level.levelgen.placement.DecoratorNoiseCountFactor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.DecoratorChance;
import net.minecraft.world.level.levelgen.feature.DoublePlantConfiguration;
import net.minecraft.world.level.levelgen.feature.BlockBlobConfiguration;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.DiskConfiguration;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.DepthAverageConfigation;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.feature.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.MonsterRoomPlacementConfiguration;
import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;
import net.minecraft.world.level.levelgen.feature.LakeConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.VillageConfiguration;
import net.minecraft.world.level.levelgen.feature.BuriedTreasureConfiguration;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.GenerationStep;

public class BiomeDefaultFeatures {
    public static void addDefaultCarvers(final Biome bio) {
        bio.<CarverConfiguration>addCarver(GenerationStep.Carving.AIR, Biome.makeCarver((WorldCarver<C>)WorldCarver.CAVE, (C)new ProbabilityFeatureConfiguration(0.14285715f)));
        bio.<CarverConfiguration>addCarver(GenerationStep.Carving.AIR, Biome.makeCarver((WorldCarver<C>)WorldCarver.CANYON, (C)new ProbabilityFeatureConfiguration(0.02f)));
    }
    
    public static void addOceanCarvers(final Biome bio) {
        bio.<CarverConfiguration>addCarver(GenerationStep.Carving.AIR, Biome.makeCarver((WorldCarver<C>)WorldCarver.CAVE, (C)new ProbabilityFeatureConfiguration(0.06666667f)));
        bio.<CarverConfiguration>addCarver(GenerationStep.Carving.AIR, Biome.makeCarver((WorldCarver<C>)WorldCarver.CANYON, (C)new ProbabilityFeatureConfiguration(0.02f)));
        bio.<CarverConfiguration>addCarver(GenerationStep.Carving.LIQUID, Biome.makeCarver((WorldCarver<C>)WorldCarver.UNDERWATER_CANYON, (C)new ProbabilityFeatureConfiguration(0.02f)));
        bio.<CarverConfiguration>addCarver(GenerationStep.Carving.LIQUID, Biome.makeCarver((WorldCarver<C>)WorldCarver.UNDERWATER_CAVE, (C)new ProbabilityFeatureConfiguration(0.06666667f)));
    }
    
    public static void addStructureFeaturePlacement(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Biome.<MineshaftConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.MINESHAFT, new MineshaftConfiguration(0.004000000189989805, MineshaftFeature.Type.NORMAL), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<PillagerOutpostConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.PILLAGER_OUTPOST, new PillagerOutpostConfiguration(0.004), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.STRONGHOLD, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.SWAMP_HUT, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.DESERT_PYRAMID, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.JUNGLE_TEMPLE, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.IGLOO, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<ShipwreckConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.SHIPWRECK, new ShipwreckConfiguration(false), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.OCEAN_MONUMENT, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.WOODLAND_MANSION, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<OceanRuinConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.OCEAN_RUIN, new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3f, 0.9f), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Biome.<BuriedTreasureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.BURIED_TREASURE, new BuriedTreasureConfiguration(0.01f), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<VillageConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.VILLAGE, new VillageConfiguration("village/plains/town_centers", 6), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
    }
    
    public static void addDefaultLakes(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Biome.<LakeConfiguration, LakeChanceDecoratorConfig>makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.WATER.defaultBlockState()), FeatureDecorator.WATER_LAKE, new LakeChanceDecoratorConfig(4)));
        bio.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Biome.<LakeConfiguration, LakeChanceDecoratorConfig>makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.LAVA.defaultBlockState()), FeatureDecorator.LAVA_LAKE, new LakeChanceDecoratorConfig(80)));
    }
    
    public static void addDesertLakes(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Biome.<LakeConfiguration, LakeChanceDecoratorConfig>makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.LAVA.defaultBlockState()), FeatureDecorator.LAVA_LAKE, new LakeChanceDecoratorConfig(80)));
    }
    
    public static void addDefaultMonsterRoom(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Biome.<NoneFeatureConfiguration, MonsterRoomPlacementConfiguration>makeComposite(Feature.MONSTER_ROOM, FeatureConfiguration.NONE, FeatureDecorator.DUNGEONS, new MonsterRoomPlacementConfiguration(8)));
    }
    
    public static void addDefaultUndergroundVariety(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.DIRT.defaultBlockState(), 33), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(10, 0, 0, 256)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.GRAVEL.defaultBlockState(), 33), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(8, 0, 0, 256)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.GRANITE.defaultBlockState(), 33), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(10, 0, 0, 80)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.DIORITE.defaultBlockState(), 33), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(10, 0, 0, 80)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.ANDESITE.defaultBlockState(), 33), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(10, 0, 0, 80)));
    }
    
    public static void addDefaultOres(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.COAL_ORE.defaultBlockState(), 17), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(20, 0, 0, 128)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.IRON_ORE.defaultBlockState(), 9), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(20, 0, 0, 64)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.GOLD_ORE.defaultBlockState(), 9), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(2, 0, 0, 32)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.REDSTONE_ORE.defaultBlockState(), 8), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(8, 0, 0, 16)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.DIAMOND_ORE.defaultBlockState(), 8), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(1, 0, 0, 16)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DepthAverageConfigation>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.LAPIS_ORE.defaultBlockState(), 7), FeatureDecorator.COUNT_DEPTH_AVERAGE, new DepthAverageConfigation(1, 16, 16)));
    }
    
    public static void addExtraGold(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.GOLD_ORE.defaultBlockState(), 9), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(20, 32, 32, 80)));
    }
    
    public static void addExtraEmeralds(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<ReplaceBlockConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.EMERALD_ORE, new ReplaceBlockConfiguration(Blocks.STONE.defaultBlockState(), Blocks.EMERALD_ORE.defaultBlockState()), FeatureDecorator.EMERALD_ORE, DecoratorConfiguration.NONE));
    }
    
    public static void addInfestedStone(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<OreConfiguration, DecoratorCountRange>makeComposite(Feature.ORE, new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, Blocks.INFESTED_STONE.defaultBlockState(), 9), FeatureDecorator.COUNT_RANGE, new DecoratorCountRange(7, 0, 0, 64)));
    }
    
    public static void addDefaultSoftDisks(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<DiskConfiguration, DecoratorFrequency>makeComposite(Feature.DISK, new DiskConfiguration(Blocks.SAND.defaultBlockState(), 7, 2, (List<BlockState>)Lists.newArrayList((Object[])new BlockState[] { Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState() })), FeatureDecorator.COUNT_TOP_SOLID, new DecoratorFrequency(3)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<DiskConfiguration, DecoratorFrequency>makeComposite(Feature.DISK, new DiskConfiguration(Blocks.CLAY.defaultBlockState(), 4, 1, (List<BlockState>)Lists.newArrayList((Object[])new BlockState[] { Blocks.DIRT.defaultBlockState(), Blocks.CLAY.defaultBlockState() })), FeatureDecorator.COUNT_TOP_SOLID, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<DiskConfiguration, DecoratorFrequency>makeComposite(Feature.DISK, new DiskConfiguration(Blocks.GRAVEL.defaultBlockState(), 6, 2, (List<BlockState>)Lists.newArrayList((Object[])new BlockState[] { Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState() })), FeatureDecorator.COUNT_TOP_SOLID, new DecoratorFrequency(1)));
    }
    
    public static void addSwampClayDisk(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Biome.<DiskConfiguration, DecoratorFrequency>makeComposite(Feature.DISK, new DiskConfiguration(Blocks.CLAY.defaultBlockState(), 4, 1, (List<BlockState>)Lists.newArrayList((Object[])new BlockState[] { Blocks.DIRT.defaultBlockState(), Blocks.CLAY.defaultBlockState() })), FeatureDecorator.COUNT_TOP_SOLID, new DecoratorFrequency(1)));
    }
    
    public static void addMossyStoneBlock(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Biome.<BlockBlobConfiguration, DecoratorFrequency>makeComposite(Feature.FOREST_ROCK, new BlockBlobConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 0), FeatureDecorator.FOREST_ROCK, new DecoratorFrequency(3)));
    }
    
    public static void addFerns(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<DoublePlantConfiguration, DecoratorFrequency>makeComposite(Feature.DOUBLE_PLANT, new DoublePlantConfiguration(Blocks.LARGE_FERN.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(7)));
    }
    
    public static void addBerryBushes(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.SWEET_BERRY_BUSH, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(12)));
    }
    
    public static void addSparseBerryBushes(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.SWEET_BERRY_BUSH, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
    }
    
    public static void addLightBambooVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<ProbabilityFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.BAMBOO, new ProbabilityFeatureConfiguration(0.0f), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(16)));
    }
    
    public static void addBambooVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<ProbabilityFeatureConfiguration, DecoratorNoiseCountFactor>makeComposite(Feature.BAMBOO, new ProbabilityFeatureConfiguration(0.2f), FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED, new DecoratorNoiseCountFactor(160, 80.0, 0.3, Heightmap.Types.WORLD_SURFACE_WG)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH, Feature.MEGA_JUNGLE_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.05f, 0.15f, 0.7f }, Feature.JUNGLE_GRASS, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(30, 0.1f, 1)));
    }
    
    public static void addTaigaTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.PINE_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE }, new float[] { 0.33333334f }, Feature.SPRUCE_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(10, 0.1f, 1)));
    }
    
    public static void addWaterTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.FANCY_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE }, new float[] { 0.1f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(0, 0.1f, 1)));
    }
    
    public static void addBirchTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequencyWithExtraChance>makeComposite(Feature.BIRCH_TREE, FeatureConfiguration.NONE, FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(10, 0.1f, 1)));
    }
    
    public static void addOtherBirchTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.BIRCH_TREE, Feature.FANCY_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.2f, 0.1f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(10, 0.1f, 1)));
    }
    
    public static void addTallBirchTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.SUPER_BIRCH_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE }, new float[] { 0.5f }, Feature.BIRCH_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(10, 0.1f, 1)));
    }
    
    public static void addSavannaTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.SAVANNA_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE }, new float[] { 0.8f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(1, 0.1f, 1)));
    }
    
    public static void addShatteredSavannaTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.SAVANNA_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE }, new float[] { 0.8f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(2, 0.1f, 1)));
    }
    
    public static void addMountainTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.SPRUCE_TREE, Feature.FANCY_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.666f, 0.1f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(0, 0.1f, 1)));
    }
    
    public static void addMountainEdgeTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.SPRUCE_TREE, Feature.FANCY_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.666f, 0.1f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(3, 0.1f, 1)));
    }
    
    public static void addJungleTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH, Feature.MEGA_JUNGLE_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.1f, 0.5f, 0.33333334f }, Feature.JUNGLE_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(50, 0.1f, 1)));
    }
    
    public static void addJungleEdgeTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.1f, 0.5f }, Feature.JUNGLE_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(2, 0.1f, 1)));
    }
    
    public static void addBadlandsTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequencyWithExtraChance>makeComposite(Feature.NORMAL_TREE, FeatureConfiguration.NONE, FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(5, 0.1f, 1)));
    }
    
    public static void addSnowyTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequencyWithExtraChance>makeComposite(Feature.SPRUCE_TREE, FeatureConfiguration.NONE, FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(0, 0.1f, 1)));
    }
    
    public static void addGiantSpruceTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.MEGA_SPRUCE_TREE, Feature.PINE_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.33333334f, 0.33333334f }, Feature.SPRUCE_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(10, 0.1f, 1)));
    }
    
    public static void addGiantTrees(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.MEGA_SPRUCE_TREE, Feature.MEGA_PINE_TREE, Feature.PINE_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE, FeatureConfiguration.NONE, FeatureConfiguration.NONE }, new float[] { 0.025641026f, 0.30769232f, 0.33333334f }, Feature.SPRUCE_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(10, 0.1f, 1)));
    }
    
    public static void addJungleGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.JUNGLE_GRASS, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(25)));
    }
    
    public static void addSavannaGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<DoublePlantConfiguration, DecoratorFrequency>makeComposite(Feature.DOUBLE_PLANT, new DoublePlantConfiguration(Blocks.TALL_GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(7)));
    }
    
    public static void addShatteredSavannaGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorFrequency>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(5)));
    }
    
    public static void addSavannaExtraGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorFrequency>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(20)));
    }
    
    public static void addBadlandGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorFrequency>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.DEAD_BUSH, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(20)));
    }
    
    public static void addForestFlowers(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomRandomFeatureConfig, DecoratorFrequency>makeComposite(Feature.RANDOM_RANDOM_SELECTOR, new RandomRandomFeatureConfig(new Feature[] { Feature.DOUBLE_PLANT, Feature.DOUBLE_PLANT, Feature.DOUBLE_PLANT, Feature.GENERAL_FOREST_FLOWER }, new FeatureConfiguration[] { new DoublePlantConfiguration(Blocks.LILAC.defaultBlockState()), new DoublePlantConfiguration(Blocks.ROSE_BUSH.defaultBlockState()), new DoublePlantConfiguration(Blocks.PEONY.defaultBlockState()), FeatureConfiguration.NONE }, 0), FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(5)));
    }
    
    public static void addForestGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorFrequency>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(2)));
    }
    
    public static void addSwampVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequencyWithExtraChance>makeComposite(Feature.SWAMP_TREE, FeatureConfiguration.NONE, FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(2, 0.1f, 1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.SWAMP_FLOWER, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorFrequency>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(5)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.DEAD_BUSH, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.WATERLILY, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(4)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.BROWN_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP, new DecoratorFrequencyChance(8, 0.25f)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.RED_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE, new DecoratorFrequencyChance(8, 0.125f)));
    }
    
    public static void addMushroomFieldVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomBooleanFeatureConfig, DecoratorFrequency>makeComposite(Feature.RANDOM_BOOLEAN_SELECTOR, new RandomBooleanFeatureConfig(Feature.HUGE_RED_MUSHROOM, new HugeMushroomFeatureConfig(false), Feature.HUGE_BROWN_MUSHROOM, new HugeMushroomFeatureConfig(false)), FeatureDecorator.COUNT_HEIGHTMAP, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.BROWN_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP, new DecoratorFrequencyChance(1, 0.25f)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.RED_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE, new DecoratorFrequencyChance(1, 0.125f)));
    }
    
    public static void addPlainVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, DecoratorFrequencyWithExtraChance>makeComposite(Feature.RANDOM_SELECTOR, new RandomFeatureConfig(new Feature[] { Feature.FANCY_TREE }, new FeatureConfiguration[] { FeatureConfiguration.NONE }, new float[] { 0.33333334f }, Feature.NORMAL_TREE, FeatureConfiguration.NONE), FeatureDecorator.COUNT_EXTRA_HEIGHTMAP, new DecoratorFrequencyWithExtraChance(0, 0.05f, 1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorNoiseDependant>makeComposite(Feature.PLAIN_FLOWER, FeatureConfiguration.NONE, FeatureDecorator.NOISE_HEIGHTMAP_32, new DecoratorNoiseDependant(-0.8, 15, 4)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorNoiseDependant>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.NOISE_HEIGHTMAP_DOUBLE, new DecoratorNoiseDependant(-0.8, 5, 10)));
    }
    
    public static void addDesertVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.DEAD_BUSH, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(2)));
    }
    
    public static void addGiantTaigaVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.TAIGA_GRASS, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(7)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.DEAD_BUSH, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.BROWN_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP, new DecoratorFrequencyChance(3, 0.25f)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.RED_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE, new DecoratorFrequencyChance(3, 0.125f)));
    }
    
    public static void addDefaultFlowers(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.DEFAULT_FLOWER, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(2)));
    }
    
    public static void addWarmFlowers(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.DEFAULT_FLOWER, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_32, new DecoratorFrequency(4)));
    }
    
    public static void addDefaultGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<GrassConfiguration, DecoratorFrequency>makeComposite(Feature.GRASS, new GrassConfiguration(Blocks.GRASS.defaultBlockState()), FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
    }
    
    public static void addTaigaGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.TAIGA_GRASS, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.BROWN_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP, new DecoratorFrequencyChance(1, 0.25f)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorFrequencyChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.RED_MUSHROOM.defaultBlockState()), FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE, new DecoratorFrequencyChance(1, 0.125f)));
    }
    
    public static void addPlainGrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<DoublePlantConfiguration, DecoratorNoiseDependant>makeComposite(Feature.DOUBLE_PLANT, new DoublePlantConfiguration(Blocks.TALL_GRASS.defaultBlockState()), FeatureDecorator.NOISE_HEIGHTMAP_32, new DecoratorNoiseDependant(-0.8, 0, 7)));
    }
    
    public static void addDefaultMushrooms(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.BROWN_MUSHROOM.defaultBlockState()), FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(4)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<BushConfiguration, DecoratorChance>makeComposite(Feature.BUSH, new BushConfiguration(Blocks.RED_MUSHROOM.defaultBlockState()), FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(8)));
    }
    
    public static void addDefaultExtraVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.REED, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(10)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.PUMPKIN, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(32)));
    }
    
    public static void addBadlandExtraVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.REED, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(13)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.PUMPKIN, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(32)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.CACTUS, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(5)));
    }
    
    public static void addJungleExtraVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.MELON, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(1)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.VINES, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHT_64, new DecoratorFrequency(50)));
    }
    
    public static void addDesertExtraVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.REED, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(60)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.PUMPKIN, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(32)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.CACTUS, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(10)));
    }
    
    public static void addSwampExtraVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorFrequency>makeComposite(Feature.REED, FeatureConfiguration.NONE, FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE, new DecoratorFrequency(20)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.PUMPKIN, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE, new DecoratorChance(32)));
    }
    
    public static void addDesertExtraDecoration(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.DESERT_WELL, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_HEIGHTMAP, new DecoratorChance(1000)));
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.FOSSIL, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_PASSTHROUGH, new DecoratorChance(64)));
    }
    
    public static void addSwampExtraDecoration(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorChance>makeComposite(Feature.FOSSIL, FeatureConfiguration.NONE, FeatureDecorator.CHANCE_PASSTHROUGH, new DecoratorChance(64)));
    }
    
    public static void addColdOceanExtraVegetation(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorNoiseCountFactor>makeComposite(Feature.KELP, FeatureConfiguration.NONE, FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED, new DecoratorNoiseCountFactor(120, 80.0, 0.0, Heightmap.Types.OCEAN_FLOOR_WG)));
    }
    
    public static void addDefaultSeagrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SimpleBlockConfiguration, DecoratorCarvingMaskConfig>makeComposite(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(Blocks.SEAGRASS.defaultBlockState(), new BlockState[] { Blocks.STONE.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() }), FeatureDecorator.CARVING_MASK, new DecoratorCarvingMaskConfig(GenerationStep.Carving.LIQUID, 0.1f)));
    }
    
    public static void addWarmSeagrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SeagrassFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.SEAGRASS, new SeagrassFeatureConfiguration(80, 0.3), FeatureDecorator.TOP_SOLID_HEIGHTMAP, DecoratorConfiguration.NONE));
    }
    
    public static void addDeepWarmSeagrass(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SeagrassFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.SEAGRASS, new SeagrassFeatureConfiguration(80, 0.8), FeatureDecorator.TOP_SOLID_HEIGHTMAP, DecoratorConfiguration.NONE));
    }
    
    public static void addLukeWarmKelp(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<NoneFeatureConfiguration, DecoratorNoiseCountFactor>makeComposite(Feature.KELP, FeatureConfiguration.NONE, FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED, new DecoratorNoiseCountFactor(80, 80.0, 0.0, Heightmap.Types.OCEAN_FLOOR_WG)));
    }
    
    public static void addDefaultSprings(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SpringConfiguration, DecoratorCountRange>makeComposite(Feature.SPRING, new SpringConfiguration(Fluids.WATER.defaultFluidState()), FeatureDecorator.COUNT_BIASED_RANGE, new DecoratorCountRange(50, 8, 8, 256)));
        bio.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Biome.<SpringConfiguration, DecoratorCountRange>makeComposite(Feature.SPRING, new SpringConfiguration(Fluids.LAVA.defaultFluidState()), FeatureDecorator.COUNT_VERY_BIASED_RANGE, new DecoratorCountRange(20, 8, 16, 256)));
    }
    
    public static void addIcebergs(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Biome.<IcebergConfiguration, DecoratorChance>makeComposite(Feature.ICEBERG, new IcebergConfiguration(Blocks.PACKED_ICE.defaultBlockState()), FeatureDecorator.ICEBERG, new DecoratorChance(16)));
        bio.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Biome.<IcebergConfiguration, DecoratorChance>makeComposite(Feature.ICEBERG, new IcebergConfiguration(Blocks.BLUE_ICE.defaultBlockState()), FeatureDecorator.ICEBERG, new DecoratorChance(200)));
    }
    
    public static void addBlueIce(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, DecoratorCountRange>makeComposite(Feature.BLUE_ICE, FeatureConfiguration.NONE, FeatureDecorator.RANDOM_COUNT_RANGE, new DecoratorCountRange(20, 30, 32, 64)));
    }
    
    public static void addSurfaceFreezing(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.FREEZE_TOP_LAYER, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
    }
    
    public static void addEndCity(final Biome bio) {
        bio.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Biome.<NoneFeatureConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.END_CITY, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
    }
}
