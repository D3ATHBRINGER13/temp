package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.placement.nether.LightGemChanceDecorator;
import net.minecraft.world.level.levelgen.placement.nether.MagmaDecorator;
import net.minecraft.world.level.levelgen.placement.nether.HellFireDecorator;
import net.minecraft.world.level.levelgen.placement.nether.ChanceRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.RandomCountRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.CountRangeDecorator;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.DecoratorChanceRange;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorNoiseDependant;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public abstract class FeatureDecorator<DC extends DecoratorConfiguration> {
    public static final FeatureDecorator<DecoratorFrequency> COUNT_HEIGHTMAP;
    public static final FeatureDecorator<DecoratorFrequency> COUNT_TOP_SOLID;
    public static final FeatureDecorator<DecoratorFrequency> COUNT_HEIGHTMAP_32;
    public static final FeatureDecorator<DecoratorFrequency> COUNT_HEIGHTMAP_DOUBLE;
    public static final FeatureDecorator<DecoratorFrequency> COUNT_HEIGHT_64;
    public static final FeatureDecorator<DecoratorNoiseDependant> NOISE_HEIGHTMAP_32;
    public static final FeatureDecorator<DecoratorNoiseDependant> NOISE_HEIGHTMAP_DOUBLE;
    public static final FeatureDecorator<NoneDecoratorConfiguration> NOPE;
    public static final FeatureDecorator<DecoratorChance> CHANCE_HEIGHTMAP;
    public static final FeatureDecorator<DecoratorChance> CHANCE_HEIGHTMAP_DOUBLE;
    public static final FeatureDecorator<DecoratorChance> CHANCE_PASSTHROUGH;
    public static final FeatureDecorator<DecoratorChance> CHANCE_TOP_SOLID_HEIGHTMAP;
    public static final FeatureDecorator<DecoratorFrequencyWithExtraChance> COUNT_EXTRA_HEIGHTMAP;
    public static final FeatureDecorator<DecoratorCountRange> COUNT_RANGE;
    public static final FeatureDecorator<DecoratorCountRange> COUNT_BIASED_RANGE;
    public static final FeatureDecorator<DecoratorCountRange> COUNT_VERY_BIASED_RANGE;
    public static final FeatureDecorator<DecoratorCountRange> RANDOM_COUNT_RANGE;
    public static final FeatureDecorator<DecoratorChanceRange> CHANCE_RANGE;
    public static final FeatureDecorator<DecoratorFrequencyChance> COUNT_CHANCE_HEIGHTMAP;
    public static final FeatureDecorator<DecoratorFrequencyChance> COUNT_CHANCE_HEIGHTMAP_DOUBLE;
    public static final FeatureDecorator<DepthAverageConfigation> COUNT_DEPTH_AVERAGE;
    public static final FeatureDecorator<NoneDecoratorConfiguration> TOP_SOLID_HEIGHTMAP;
    public static final FeatureDecorator<DecoratorRange> TOP_SOLID_HEIGHTMAP_RANGE;
    public static final FeatureDecorator<DecoratorNoiseCountFactor> TOP_SOLID_HEIGHTMAP_NOISE_BIASED;
    public static final FeatureDecorator<DecoratorCarvingMaskConfig> CARVING_MASK;
    public static final FeatureDecorator<DecoratorFrequency> FOREST_ROCK;
    public static final FeatureDecorator<DecoratorFrequency> HELL_FIRE;
    public static final FeatureDecorator<DecoratorFrequency> MAGMA;
    public static final FeatureDecorator<NoneDecoratorConfiguration> EMERALD_ORE;
    public static final FeatureDecorator<LakeChanceDecoratorConfig> LAVA_LAKE;
    public static final FeatureDecorator<LakeChanceDecoratorConfig> WATER_LAKE;
    public static final FeatureDecorator<MonsterRoomPlacementConfiguration> DUNGEONS;
    public static final FeatureDecorator<NoneDecoratorConfiguration> DARK_OAK_TREE;
    public static final FeatureDecorator<DecoratorChance> ICEBERG;
    public static final FeatureDecorator<DecoratorFrequency> LIGHT_GEM_CHANCE;
    public static final FeatureDecorator<NoneDecoratorConfiguration> END_ISLAND;
    public static final FeatureDecorator<NoneDecoratorConfiguration> CHORUS_PLANT;
    public static final FeatureDecorator<NoneDecoratorConfiguration> END_GATEWAY;
    private final Function<Dynamic<?>, ? extends DC> configurationFactory;
    
    private static <T extends DecoratorConfiguration, G extends FeatureDecorator<T>> G register(final String string, final G che) {
        return Registry.<G>register(Registry.DECORATOR, string, che);
    }
    
    public FeatureDecorator(final Function<Dynamic<?>, ? extends DC> function) {
        this.configurationFactory = function;
    }
    
    public DC createSettings(final Dynamic<?> dynamic) {
        return (DC)this.configurationFactory.apply(dynamic);
    }
    
    protected <FC extends FeatureConfiguration> boolean placeFeature(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final DC cax, final ConfiguredFeature<FC> cal) {
        final AtomicBoolean atomicBoolean8 = new AtomicBoolean(false);
        this.getPositions(bhs, bxi, random, cax, ew).forEach(ew -> {
            final boolean boolean7 = cal.place(bhs, bxi, random, ew);
            atomicBoolean8.set(atomicBoolean8.get() || boolean7);
        });
        return atomicBoolean8.get();
    }
    
    public abstract Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DC cax, final BlockPos ew);
    
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
    
    static {
        COUNT_HEIGHTMAP = FeatureDecorator.<DecoratorConfiguration, CountHeightmapDecorator>register("count_heightmap", new CountHeightmapDecorator(DecoratorFrequency::deserialize));
        COUNT_TOP_SOLID = FeatureDecorator.<DecoratorConfiguration, CountTopSolidDecorator>register("count_top_solid", new CountTopSolidDecorator(DecoratorFrequency::deserialize));
        COUNT_HEIGHTMAP_32 = FeatureDecorator.<DecoratorConfiguration, CountHeightmap32Decorator>register("count_heightmap_32", new CountHeightmap32Decorator(DecoratorFrequency::deserialize));
        COUNT_HEIGHTMAP_DOUBLE = FeatureDecorator.<DecoratorConfiguration, CountHeighmapDoubleDecorator>register("count_heightmap_double", new CountHeighmapDoubleDecorator(DecoratorFrequency::deserialize));
        COUNT_HEIGHT_64 = FeatureDecorator.<DecoratorConfiguration, CountHeight64Decorator>register("count_height_64", new CountHeight64Decorator(DecoratorFrequency::deserialize));
        NOISE_HEIGHTMAP_32 = FeatureDecorator.<DecoratorConfiguration, NoiseHeightmap32Decorator>register("noise_heightmap_32", new NoiseHeightmap32Decorator(DecoratorNoiseDependant::deserialize));
        NOISE_HEIGHTMAP_DOUBLE = FeatureDecorator.<DecoratorConfiguration, NoiseHeightmapDoubleDecorator>register("noise_heightmap_double", new NoiseHeightmapDoubleDecorator(DecoratorNoiseDependant::deserialize));
        NOPE = FeatureDecorator.<DecoratorConfiguration, NopePlacementDecorator>register("nope", new NopePlacementDecorator(NoneDecoratorConfiguration::deserialize));
        CHANCE_HEIGHTMAP = FeatureDecorator.<DecoratorConfiguration, ChanceHeightmapDecorator>register("chance_heightmap", new ChanceHeightmapDecorator(DecoratorChance::deserialize));
        CHANCE_HEIGHTMAP_DOUBLE = FeatureDecorator.<DecoratorConfiguration, ChanceHeightmapDoubleDecorator>register("chance_heightmap_double", new ChanceHeightmapDoubleDecorator(DecoratorChance::deserialize));
        CHANCE_PASSTHROUGH = FeatureDecorator.<DecoratorConfiguration, ChancePassthroughDecorator>register("chance_passthrough", new ChancePassthroughDecorator(DecoratorChance::deserialize));
        CHANCE_TOP_SOLID_HEIGHTMAP = FeatureDecorator.<DecoratorConfiguration, ChanceTopSolidHeightmapDecorator>register("chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(DecoratorChance::deserialize));
        COUNT_EXTRA_HEIGHTMAP = FeatureDecorator.<DecoratorConfiguration, CountWithExtraChanceHeightmapDecorator>register("count_extra_heightmap", new CountWithExtraChanceHeightmapDecorator(DecoratorFrequencyWithExtraChance::deserialize));
        COUNT_RANGE = FeatureDecorator.<DecoratorConfiguration, CountRangeDecorator>register("count_range", new CountRangeDecorator(DecoratorCountRange::deserialize));
        COUNT_BIASED_RANGE = FeatureDecorator.<DecoratorConfiguration, CountBiasedRangeDecorator>register("count_biased_range", new CountBiasedRangeDecorator(DecoratorCountRange::deserialize));
        COUNT_VERY_BIASED_RANGE = FeatureDecorator.<DecoratorConfiguration, CountVeryBiasedRangeDecorator>register("count_very_biased_range", new CountVeryBiasedRangeDecorator(DecoratorCountRange::deserialize));
        RANDOM_COUNT_RANGE = FeatureDecorator.<DecoratorConfiguration, RandomCountRangeDecorator>register("random_count_range", new RandomCountRangeDecorator(DecoratorCountRange::deserialize));
        CHANCE_RANGE = FeatureDecorator.<DecoratorConfiguration, ChanceRangeDecorator>register("chance_range", new ChanceRangeDecorator(DecoratorChanceRange::deserialize));
        COUNT_CHANCE_HEIGHTMAP = FeatureDecorator.<DecoratorConfiguration, CountChanceHeightmapDecorator>register("count_chance_heightmap", new CountChanceHeightmapDecorator(DecoratorFrequencyChance::deserialize));
        COUNT_CHANCE_HEIGHTMAP_DOUBLE = FeatureDecorator.<DecoratorConfiguration, CountChanceHeightmapDoubleDecorator>register("count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(DecoratorFrequencyChance::deserialize));
        COUNT_DEPTH_AVERAGE = FeatureDecorator.<DecoratorConfiguration, CountDepthAverageDecorator>register("count_depth_average", new CountDepthAverageDecorator(DepthAverageConfigation::deserialize));
        TOP_SOLID_HEIGHTMAP = FeatureDecorator.<DecoratorConfiguration, TopSolidHeightMapDecorator>register("top_solid_heightmap", new TopSolidHeightMapDecorator(NoneDecoratorConfiguration::deserialize));
        TOP_SOLID_HEIGHTMAP_RANGE = FeatureDecorator.<DecoratorConfiguration, TopSolidHeightMapRangeDecorator>register("top_solid_heightmap_range", new TopSolidHeightMapRangeDecorator(DecoratorRange::deserialize));
        TOP_SOLID_HEIGHTMAP_NOISE_BIASED = FeatureDecorator.<DecoratorConfiguration, TopSolidHeightMapNoiseBasedDecorator>register("top_solid_heightmap_noise_biased", new TopSolidHeightMapNoiseBasedDecorator(DecoratorNoiseCountFactor::deserialize));
        CARVING_MASK = FeatureDecorator.<DecoratorConfiguration, CarvingMaskDecorator>register("carving_mask", new CarvingMaskDecorator(DecoratorCarvingMaskConfig::deserialize));
        FOREST_ROCK = FeatureDecorator.<DecoratorConfiguration, ForestRockPlacementDecorator>register("forest_rock", new ForestRockPlacementDecorator(DecoratorFrequency::deserialize));
        HELL_FIRE = FeatureDecorator.<DecoratorConfiguration, HellFireDecorator>register("hell_fire", new HellFireDecorator(DecoratorFrequency::deserialize));
        MAGMA = FeatureDecorator.<DecoratorConfiguration, MagmaDecorator>register("magma", new MagmaDecorator(DecoratorFrequency::deserialize));
        EMERALD_ORE = FeatureDecorator.<DecoratorConfiguration, EmeraldPlacementDecorator>register("emerald_ore", new EmeraldPlacementDecorator(NoneDecoratorConfiguration::deserialize));
        LAVA_LAKE = FeatureDecorator.<DecoratorConfiguration, LakeLavaPlacementDecorator>register("lava_lake", new LakeLavaPlacementDecorator(LakeChanceDecoratorConfig::deserialize));
        WATER_LAKE = FeatureDecorator.<DecoratorConfiguration, LakeWaterPlacementDecorator>register("water_lake", new LakeWaterPlacementDecorator(LakeChanceDecoratorConfig::deserialize));
        DUNGEONS = FeatureDecorator.<DecoratorConfiguration, MonsterRoomPlacementDecorator>register("dungeons", new MonsterRoomPlacementDecorator(MonsterRoomPlacementConfiguration::deserialize));
        DARK_OAK_TREE = FeatureDecorator.<DecoratorConfiguration, DarkOakTreePlacementDecorator>register("dark_oak_tree", new DarkOakTreePlacementDecorator(NoneDecoratorConfiguration::deserialize));
        ICEBERG = FeatureDecorator.<DecoratorConfiguration, IcebergPlacementDecorator>register("iceberg", new IcebergPlacementDecorator(DecoratorChance::deserialize));
        LIGHT_GEM_CHANCE = FeatureDecorator.<DecoratorConfiguration, LightGemChanceDecorator>register("light_gem_chance", new LightGemChanceDecorator(DecoratorFrequency::deserialize));
        END_ISLAND = FeatureDecorator.<DecoratorConfiguration, EndIslandPlacementDecorator>register("end_island", new EndIslandPlacementDecorator(NoneDecoratorConfiguration::deserialize));
        CHORUS_PLANT = FeatureDecorator.<DecoratorConfiguration, ChorusPlantPlacementDecorator>register("chorus_plant", new ChorusPlantPlacementDecorator(NoneDecoratorConfiguration::deserialize));
        END_GATEWAY = FeatureDecorator.<DecoratorConfiguration, EndGatewayPlacementDecorator>register("end_gateway", new EndGatewayPlacementDecorator(NoneDecoratorConfiguration::deserialize));
    }
}
