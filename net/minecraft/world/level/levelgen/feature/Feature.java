package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.ImmutableList;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import java.util.Locale;
import com.google.common.collect.HashBiMap;
import java.util.Collections;
import net.minecraft.world.level.biome.Biome;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import java.util.List;
import com.google.common.collect.BiMap;

public abstract class Feature<FC extends FeatureConfiguration> {
    public static final StructureFeature<PillagerOutpostConfiguration> PILLAGER_OUTPOST;
    public static final StructureFeature<MineshaftConfiguration> MINESHAFT;
    public static final StructureFeature<NoneFeatureConfiguration> WOODLAND_MANSION;
    public static final StructureFeature<NoneFeatureConfiguration> JUNGLE_TEMPLE;
    public static final StructureFeature<NoneFeatureConfiguration> DESERT_PYRAMID;
    public static final StructureFeature<NoneFeatureConfiguration> IGLOO;
    public static final StructureFeature<ShipwreckConfiguration> SHIPWRECK;
    public static final SwamplandHutFeature SWAMP_HUT;
    public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD;
    public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT;
    public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN;
    public static final StructureFeature<NoneFeatureConfiguration> NETHER_BRIDGE;
    public static final StructureFeature<NoneFeatureConfiguration> END_CITY;
    public static final StructureFeature<BuriedTreasureConfiguration> BURIED_TREASURE;
    public static final StructureFeature<VillageConfiguration> VILLAGE;
    public static final Feature<NoneFeatureConfiguration> FANCY_TREE;
    public static final Feature<NoneFeatureConfiguration> BIRCH_TREE;
    public static final Feature<NoneFeatureConfiguration> SUPER_BIRCH_TREE;
    public static final Feature<NoneFeatureConfiguration> JUNGLE_GROUND_BUSH;
    public static final Feature<NoneFeatureConfiguration> JUNGLE_TREE;
    public static final Feature<NoneFeatureConfiguration> PINE_TREE;
    public static final Feature<NoneFeatureConfiguration> DARK_OAK_TREE;
    public static final Feature<NoneFeatureConfiguration> SAVANNA_TREE;
    public static final Feature<NoneFeatureConfiguration> SPRUCE_TREE;
    public static final Feature<NoneFeatureConfiguration> SWAMP_TREE;
    public static final Feature<NoneFeatureConfiguration> NORMAL_TREE;
    public static final Feature<NoneFeatureConfiguration> MEGA_JUNGLE_TREE;
    public static final Feature<NoneFeatureConfiguration> MEGA_PINE_TREE;
    public static final Feature<NoneFeatureConfiguration> MEGA_SPRUCE_TREE;
    public static final FlowerFeature DEFAULT_FLOWER;
    public static final FlowerFeature FOREST_FLOWER;
    public static final FlowerFeature PLAIN_FLOWER;
    public static final FlowerFeature SWAMP_FLOWER;
    public static final FlowerFeature GENERAL_FOREST_FLOWER;
    public static final Feature<NoneFeatureConfiguration> JUNGLE_GRASS;
    public static final Feature<NoneFeatureConfiguration> TAIGA_GRASS;
    public static final Feature<GrassConfiguration> GRASS;
    public static final Feature<NoneFeatureConfiguration> VOID_START_PLATFORM;
    public static final Feature<NoneFeatureConfiguration> CACTUS;
    public static final Feature<NoneFeatureConfiguration> DEAD_BUSH;
    public static final Feature<NoneFeatureConfiguration> DESERT_WELL;
    public static final Feature<NoneFeatureConfiguration> FOSSIL;
    public static final Feature<NoneFeatureConfiguration> HELL_FIRE;
    public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM;
    public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM;
    public static final Feature<NoneFeatureConfiguration> ICE_SPIKE;
    public static final Feature<NoneFeatureConfiguration> GLOWSTONE_BLOB;
    public static final Feature<NoneFeatureConfiguration> MELON;
    public static final Feature<NoneFeatureConfiguration> PUMPKIN;
    public static final Feature<NoneFeatureConfiguration> REED;
    public static final Feature<NoneFeatureConfiguration> FREEZE_TOP_LAYER;
    public static final Feature<NoneFeatureConfiguration> VINES;
    public static final Feature<NoneFeatureConfiguration> WATERLILY;
    public static final Feature<NoneFeatureConfiguration> MONSTER_ROOM;
    public static final Feature<NoneFeatureConfiguration> BLUE_ICE;
    public static final Feature<IcebergConfiguration> ICEBERG;
    public static final Feature<BlockBlobConfiguration> FOREST_ROCK;
    public static final Feature<NoneFeatureConfiguration> HAY_PILE;
    public static final Feature<NoneFeatureConfiguration> SNOW_PILE;
    public static final Feature<NoneFeatureConfiguration> ICE_PILE;
    public static final Feature<NoneFeatureConfiguration> MELON_PILE;
    public static final Feature<NoneFeatureConfiguration> PUMPKIN_PILE;
    public static final Feature<BushConfiguration> BUSH;
    public static final Feature<DiskConfiguration> DISK;
    public static final Feature<DoublePlantConfiguration> DOUBLE_PLANT;
    public static final Feature<HellSpringConfiguration> NETHER_SPRING;
    public static final Feature<FeatureRadius> ICE_PATCH;
    public static final Feature<LakeConfiguration> LAKE;
    public static final Feature<OreConfiguration> ORE;
    public static final Feature<RandomRandomFeatureConfig> RANDOM_RANDOM_SELECTOR;
    public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR;
    public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR;
    public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR;
    public static final Feature<ReplaceBlockConfiguration> EMERALD_ORE;
    public static final Feature<SpringConfiguration> SPRING;
    public static final Feature<SpikeConfiguration> END_SPIKE;
    public static final Feature<NoneFeatureConfiguration> END_ISLAND;
    public static final Feature<NoneFeatureConfiguration> CHORUS_PLANT;
    public static final Feature<EndGatewayConfiguration> END_GATEWAY;
    public static final Feature<SeagrassFeatureConfiguration> SEAGRASS;
    public static final Feature<NoneFeatureConfiguration> KELP;
    public static final Feature<NoneFeatureConfiguration> CORAL_TREE;
    public static final Feature<NoneFeatureConfiguration> CORAL_MUSHROOM;
    public static final Feature<NoneFeatureConfiguration> CORAL_CLAW;
    public static final Feature<CountFeatureConfiguration> SEA_PICKLE;
    public static final Feature<SimpleBlockConfiguration> SIMPLE_BLOCK;
    public static final Feature<ProbabilityFeatureConfiguration> BAMBOO;
    public static final Feature<DecoratedFeatureConfiguration> DECORATED;
    public static final Feature<DecoratedFeatureConfiguration> DECORATED_FLOWER;
    public static final Feature<NoneFeatureConfiguration> SWEET_BERRY_BUSH;
    public static final Feature<LayerConfiguration> FILL_LAYER;
    public static final BonusChestFeature BONUS_CHEST;
    public static final BiMap<String, StructureFeature<?>> STRUCTURES_REGISTRY;
    public static final List<StructureFeature<?>> NOISE_AFFECTING_FEATURES;
    private final Function<Dynamic<?>, ? extends FC> configurationFactory;
    protected final boolean doUpdate;
    
    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(final String string, final F cbn) {
        return Registry.<F>register(Registry.FEATURE, string, cbn);
    }
    
    public Feature(final Function<Dynamic<?>, ? extends FC> function) {
        this.configurationFactory = function;
        this.doUpdate = false;
    }
    
    public Feature(final Function<Dynamic<?>, ? extends FC> function, final boolean boolean2) {
        this.configurationFactory = function;
        this.doUpdate = boolean2;
    }
    
    public FC createSettings(final Dynamic<?> dynamic) {
        return (FC)this.configurationFactory.apply(dynamic);
    }
    
    protected void setBlock(final LevelWriter bhz, final BlockPos ew, final BlockState bvt) {
        if (this.doUpdate) {
            bhz.setBlock(ew, bvt, 3);
        }
        else {
            bhz.setBlock(ew, bvt, 2);
        }
    }
    
    public abstract boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final FC cbo);
    
    public List<Biome.SpawnerData> getSpecialEnemies() {
        return (List<Biome.SpawnerData>)Collections.emptyList();
    }
    
    public List<Biome.SpawnerData> getSpecialAnimals() {
        return (List<Biome.SpawnerData>)Collections.emptyList();
    }
    
    static {
        PILLAGER_OUTPOST = Feature.<FeatureConfiguration, PillagerOutpostFeature>register("pillager_outpost", new PillagerOutpostFeature(PillagerOutpostConfiguration::deserialize));
        MINESHAFT = Feature.<FeatureConfiguration, MineshaftFeature>register("mineshaft", new MineshaftFeature(MineshaftConfiguration::deserialize));
        WOODLAND_MANSION = Feature.<FeatureConfiguration, WoodlandMansionFeature>register("woodland_mansion", new WoodlandMansionFeature(NoneFeatureConfiguration::deserialize));
        JUNGLE_TEMPLE = Feature.<FeatureConfiguration, JunglePyramidFeature>register("jungle_temple", new JunglePyramidFeature(NoneFeatureConfiguration::deserialize));
        DESERT_PYRAMID = Feature.<FeatureConfiguration, DesertPyramidFeature>register("desert_pyramid", new DesertPyramidFeature(NoneFeatureConfiguration::deserialize));
        IGLOO = Feature.<FeatureConfiguration, IglooFeature>register("igloo", new IglooFeature(NoneFeatureConfiguration::deserialize));
        SHIPWRECK = Feature.<FeatureConfiguration, ShipwreckFeature>register("shipwreck", new ShipwreckFeature(ShipwreckConfiguration::deserialize));
        SWAMP_HUT = Feature.<FeatureConfiguration, SwamplandHutFeature>register("swamp_hut", new SwamplandHutFeature(NoneFeatureConfiguration::deserialize));
        STRONGHOLD = Feature.<FeatureConfiguration, StrongholdFeature>register("stronghold", new StrongholdFeature(NoneFeatureConfiguration::deserialize));
        OCEAN_MONUMENT = Feature.<FeatureConfiguration, OceanMonumentFeature>register("ocean_monument", new OceanMonumentFeature(NoneFeatureConfiguration::deserialize));
        OCEAN_RUIN = Feature.<FeatureConfiguration, OceanRuinFeature>register("ocean_ruin", new OceanRuinFeature(OceanRuinConfiguration::deserialize));
        NETHER_BRIDGE = Feature.<FeatureConfiguration, NetherFortressFeature>register("nether_bridge", new NetherFortressFeature(NoneFeatureConfiguration::deserialize));
        END_CITY = Feature.<FeatureConfiguration, EndCityFeature>register("end_city", new EndCityFeature(NoneFeatureConfiguration::deserialize));
        BURIED_TREASURE = Feature.<FeatureConfiguration, BuriedTreasureFeature>register("buried_treasure", new BuriedTreasureFeature(BuriedTreasureConfiguration::deserialize));
        VILLAGE = Feature.<FeatureConfiguration, VillageFeature>register("village", new VillageFeature(VillageConfiguration::deserialize));
        FANCY_TREE = Feature.<FeatureConfiguration, BigTreeFeature>register("fancy_tree", new BigTreeFeature(NoneFeatureConfiguration::deserialize, false));
        BIRCH_TREE = Feature.<FeatureConfiguration, BirchFeature>register("birch_tree", new BirchFeature(NoneFeatureConfiguration::deserialize, false, false));
        SUPER_BIRCH_TREE = Feature.<FeatureConfiguration, BirchFeature>register("super_birch_tree", new BirchFeature(NoneFeatureConfiguration::deserialize, false, true));
        JUNGLE_GROUND_BUSH = Feature.<FeatureConfiguration, GroundBushFeature>register("jungle_ground_bush", new GroundBushFeature(NoneFeatureConfiguration::deserialize, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState()));
        JUNGLE_TREE = Feature.<FeatureConfiguration, JungleTreeFeature>register("jungle_tree", new JungleTreeFeature(NoneFeatureConfiguration::deserialize, false, 4, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState(), true));
        PINE_TREE = Feature.<FeatureConfiguration, PineFeature>register("pine_tree", new PineFeature(NoneFeatureConfiguration::deserialize));
        DARK_OAK_TREE = Feature.<FeatureConfiguration, DarkOakFeature>register("dark_oak_tree", new DarkOakFeature(NoneFeatureConfiguration::deserialize, false));
        SAVANNA_TREE = Feature.<FeatureConfiguration, SavannaTreeFeature>register("savanna_tree", new SavannaTreeFeature(NoneFeatureConfiguration::deserialize, false));
        SPRUCE_TREE = Feature.<FeatureConfiguration, SpruceFeature>register("spruce_tree", new SpruceFeature(NoneFeatureConfiguration::deserialize, false));
        SWAMP_TREE = Feature.<FeatureConfiguration, SwampTreeFeature>register("swamp_tree", new SwampTreeFeature(NoneFeatureConfiguration::deserialize));
        NORMAL_TREE = Feature.<FeatureConfiguration, TreeFeature>register("normal_tree", new TreeFeature(NoneFeatureConfiguration::deserialize, false));
        MEGA_JUNGLE_TREE = Feature.<FeatureConfiguration, MegaJungleTreeFeature>register("mega_jungle_tree", new MegaJungleTreeFeature(NoneFeatureConfiguration::deserialize, false, 10, 20, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState()));
        MEGA_PINE_TREE = Feature.<FeatureConfiguration, MegaPineTreeFeature>register("mega_pine_tree", new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, false));
        MEGA_SPRUCE_TREE = Feature.<FeatureConfiguration, MegaPineTreeFeature>register("mega_spruce_tree", new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, true));
        DEFAULT_FLOWER = Feature.<FeatureConfiguration, DefaultFlowerFeature>register("default_flower", new DefaultFlowerFeature(NoneFeatureConfiguration::deserialize));
        FOREST_FLOWER = Feature.<FeatureConfiguration, ForestFlowerFeature>register("forest_flower", new ForestFlowerFeature(NoneFeatureConfiguration::deserialize));
        PLAIN_FLOWER = Feature.<FeatureConfiguration, PlainFlowerFeature>register("plain_flower", new PlainFlowerFeature(NoneFeatureConfiguration::deserialize));
        SWAMP_FLOWER = Feature.<FeatureConfiguration, SwampFlowerFeature>register("swamp_flower", new SwampFlowerFeature(NoneFeatureConfiguration::deserialize));
        GENERAL_FOREST_FLOWER = Feature.<FeatureConfiguration, GeneralForestFlowerFeature>register("general_forest_flower", new GeneralForestFlowerFeature(NoneFeatureConfiguration::deserialize));
        JUNGLE_GRASS = Feature.<FeatureConfiguration, JungleGrassFeature>register("jungle_grass", new JungleGrassFeature(NoneFeatureConfiguration::deserialize));
        TAIGA_GRASS = Feature.<FeatureConfiguration, TaigaGrassFeature>register("taiga_grass", new TaigaGrassFeature(NoneFeatureConfiguration::deserialize));
        GRASS = Feature.<FeatureConfiguration, GrassFeature>register("grass", new GrassFeature(GrassConfiguration::deserialize));
        VOID_START_PLATFORM = Feature.<FeatureConfiguration, VoidStartPlatformFeature>register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration::deserialize));
        CACTUS = Feature.<FeatureConfiguration, CactusFeature>register("cactus", new CactusFeature(NoneFeatureConfiguration::deserialize));
        DEAD_BUSH = Feature.<FeatureConfiguration, DeadBushFeature>register("dead_bush", new DeadBushFeature(NoneFeatureConfiguration::deserialize));
        DESERT_WELL = Feature.<FeatureConfiguration, DesertWellFeature>register("desert_well", new DesertWellFeature(NoneFeatureConfiguration::deserialize));
        FOSSIL = Feature.<FeatureConfiguration, FossilFeature>register("fossil", new FossilFeature(NoneFeatureConfiguration::deserialize));
        HELL_FIRE = Feature.<FeatureConfiguration, HellFireFeature>register("hell_fire", new HellFireFeature(NoneFeatureConfiguration::deserialize));
        HUGE_RED_MUSHROOM = Feature.<FeatureConfiguration, HugeRedMushroomFeature>register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig::deserialize));
        HUGE_BROWN_MUSHROOM = Feature.<FeatureConfiguration, HugeBrownMushroomFeature>register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig::deserialize));
        ICE_SPIKE = Feature.<FeatureConfiguration, IceSpikeFeature>register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration::deserialize));
        GLOWSTONE_BLOB = Feature.<FeatureConfiguration, GlowstoneFeature>register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration::deserialize));
        MELON = Feature.<FeatureConfiguration, MelonFeature>register("melon", new MelonFeature(NoneFeatureConfiguration::deserialize));
        PUMPKIN = Feature.<FeatureConfiguration, CentralSpikedFeature>register("pumpkin", new CentralSpikedFeature(NoneFeatureConfiguration::deserialize, Blocks.PUMPKIN.defaultBlockState()));
        REED = Feature.<FeatureConfiguration, ReedsFeature>register("reed", new ReedsFeature(NoneFeatureConfiguration::deserialize));
        FREEZE_TOP_LAYER = Feature.<FeatureConfiguration, SnowAndFreezeFeature>register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration::deserialize));
        VINES = Feature.<FeatureConfiguration, VinesFeature>register("vines", new VinesFeature(NoneFeatureConfiguration::deserialize));
        WATERLILY = Feature.<FeatureConfiguration, WaterlilyFeature>register("waterlily", new WaterlilyFeature(NoneFeatureConfiguration::deserialize));
        MONSTER_ROOM = Feature.<FeatureConfiguration, MonsterRoomFeature>register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration::deserialize));
        BLUE_ICE = Feature.<FeatureConfiguration, BlueIceFeature>register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration::deserialize));
        ICEBERG = Feature.<FeatureConfiguration, IcebergFeature>register("iceberg", new IcebergFeature(IcebergConfiguration::deserialize));
        FOREST_ROCK = Feature.<FeatureConfiguration, BlockBlobFeature>register("forest_rock", new BlockBlobFeature(BlockBlobConfiguration::deserialize));
        HAY_PILE = Feature.<FeatureConfiguration, HayBlockPileFeature>register("hay_pile", new HayBlockPileFeature(NoneFeatureConfiguration::deserialize));
        SNOW_PILE = Feature.<FeatureConfiguration, SnowBlockPileFeature>register("snow_pile", new SnowBlockPileFeature(NoneFeatureConfiguration::deserialize));
        ICE_PILE = Feature.<FeatureConfiguration, IceBlockPileFeature>register("ice_pile", new IceBlockPileFeature(NoneFeatureConfiguration::deserialize));
        MELON_PILE = Feature.<FeatureConfiguration, MelonBlockPileFeature>register("melon_pile", new MelonBlockPileFeature(NoneFeatureConfiguration::deserialize));
        PUMPKIN_PILE = Feature.<FeatureConfiguration, PumpkinBlockPileFeature>register("pumpkin_pile", new PumpkinBlockPileFeature(NoneFeatureConfiguration::deserialize));
        BUSH = Feature.<FeatureConfiguration, BushFeature>register("bush", new BushFeature(BushConfiguration::deserialize));
        DISK = Feature.<FeatureConfiguration, DiskReplaceFeature>register("disk", new DiskReplaceFeature(DiskConfiguration::deserialize));
        DOUBLE_PLANT = Feature.<FeatureConfiguration, DoublePlantFeature>register("double_plant", new DoublePlantFeature(DoublePlantConfiguration::deserialize));
        NETHER_SPRING = Feature.<FeatureConfiguration, NetherSpringFeature>register("nether_spring", new NetherSpringFeature(HellSpringConfiguration::deserialize));
        ICE_PATCH = Feature.<FeatureConfiguration, IcePatchFeature>register("ice_patch", new IcePatchFeature(FeatureRadius::deserialize));
        LAKE = Feature.<FeatureConfiguration, LakeFeature>register("lake", new LakeFeature(LakeConfiguration::deserialize));
        ORE = Feature.<FeatureConfiguration, OreFeature>register("ore", new OreFeature(OreConfiguration::deserialize));
        RANDOM_RANDOM_SELECTOR = Feature.<FeatureConfiguration, RandomRandomFeature>register("random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize));
        RANDOM_SELECTOR = Feature.<FeatureConfiguration, RandomSelectorFeature>register("random_selector", new RandomSelectorFeature(RandomFeatureConfig::deserialize));
        SIMPLE_RANDOM_SELECTOR = Feature.<FeatureConfiguration, SimpleRandomSelectorFeature>register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfig::deserialize));
        RANDOM_BOOLEAN_SELECTOR = Feature.<FeatureConfiguration, RandomBooleanSelectorFeature>register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfig::deserialize));
        EMERALD_ORE = Feature.<FeatureConfiguration, ReplaceBlockFeature>register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfiguration::deserialize));
        SPRING = Feature.<FeatureConfiguration, SpringFeature>register("spring_feature", new SpringFeature(SpringConfiguration::deserialize));
        END_SPIKE = Feature.<FeatureConfiguration, SpikeFeature>register("end_spike", new SpikeFeature(SpikeConfiguration::deserialize));
        END_ISLAND = Feature.<FeatureConfiguration, EndIslandFeature>register("end_island", new EndIslandFeature(NoneFeatureConfiguration::deserialize));
        CHORUS_PLANT = Feature.<FeatureConfiguration, ChorusPlantFeature>register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration::deserialize));
        END_GATEWAY = Feature.<FeatureConfiguration, EndGatewayFeature>register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration::deserialize));
        SEAGRASS = Feature.<FeatureConfiguration, SeagrassFeature>register("seagrass", new SeagrassFeature(SeagrassFeatureConfiguration::deserialize));
        KELP = Feature.<FeatureConfiguration, KelpFeature>register("kelp", new KelpFeature(NoneFeatureConfiguration::deserialize));
        CORAL_TREE = Feature.<FeatureConfiguration, CoralTreeFeature>register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration::deserialize));
        CORAL_MUSHROOM = Feature.<FeatureConfiguration, CoralMushroomFeature>register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration::deserialize));
        CORAL_CLAW = Feature.<FeatureConfiguration, CoralClawFeature>register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration::deserialize));
        SEA_PICKLE = Feature.<FeatureConfiguration, SeaPickleFeature>register("sea_pickle", new SeaPickleFeature(CountFeatureConfiguration::deserialize));
        SIMPLE_BLOCK = Feature.<FeatureConfiguration, SimpleBlockFeature>register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration::deserialize));
        BAMBOO = Feature.<FeatureConfiguration, BambooFeature>register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration::deserialize));
        DECORATED = Feature.<FeatureConfiguration, DecoratedFeature>register("decorated", new DecoratedFeature(DecoratedFeatureConfiguration::deserialize));
        DECORATED_FLOWER = Feature.<FeatureConfiguration, DecoratedFlowerFeature>register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfiguration::deserialize));
        SWEET_BERRY_BUSH = Feature.<FeatureConfiguration, CentralSpikedFeature>register("sweet_berry_bush", new CentralSpikedFeature(NoneFeatureConfiguration::deserialize, ((AbstractStateHolder<O, BlockState>)Blocks.SWEET_BERRY_BUSH.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)SweetBerryBushBlock.AGE, 3)));
        FILL_LAYER = Feature.<FeatureConfiguration, FillLayerFeature>register("fill_layer", new FillLayerFeature(LayerConfiguration::deserialize));
        BONUS_CHEST = Feature.<FeatureConfiguration, BonusChestFeature>register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration::deserialize));
        STRUCTURES_REGISTRY = Util.<BiMap>make((BiMap)HashBiMap.create(), (java.util.function.Consumer<BiMap>)(hashBiMap -> {
            hashBiMap.put("Pillager_Outpost".toLowerCase(Locale.ROOT), Feature.PILLAGER_OUTPOST);
            hashBiMap.put("Mineshaft".toLowerCase(Locale.ROOT), Feature.MINESHAFT);
            hashBiMap.put("Mansion".toLowerCase(Locale.ROOT), Feature.WOODLAND_MANSION);
            hashBiMap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), Feature.JUNGLE_TEMPLE);
            hashBiMap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), Feature.DESERT_PYRAMID);
            hashBiMap.put("Igloo".toLowerCase(Locale.ROOT), Feature.IGLOO);
            hashBiMap.put("Shipwreck".toLowerCase(Locale.ROOT), Feature.SHIPWRECK);
            hashBiMap.put("Swamp_Hut".toLowerCase(Locale.ROOT), Feature.SWAMP_HUT);
            hashBiMap.put("Stronghold".toLowerCase(Locale.ROOT), Feature.STRONGHOLD);
            hashBiMap.put("Monument".toLowerCase(Locale.ROOT), Feature.OCEAN_MONUMENT);
            hashBiMap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), Feature.OCEAN_RUIN);
            hashBiMap.put("Fortress".toLowerCase(Locale.ROOT), Feature.NETHER_BRIDGE);
            hashBiMap.put("EndCity".toLowerCase(Locale.ROOT), Feature.END_CITY);
            hashBiMap.put("Buried_Treasure".toLowerCase(Locale.ROOT), Feature.BURIED_TREASURE);
            hashBiMap.put("Village".toLowerCase(Locale.ROOT), Feature.VILLAGE);
        }));
        NOISE_AFFECTING_FEATURES = (List)ImmutableList.of(Feature.PILLAGER_OUTPOST, Feature.VILLAGE);
    }
}
