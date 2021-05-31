package net.minecraft.core;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import net.minecraft.stats.Stats;
import org.apache.commons.lang3.Validate;
import net.minecraft.SharedConstants;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Random;
import java.util.Set;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.stats.StatType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.sounds.SoundEvent;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements IdMap<T> {
    protected static final Logger LOGGER;
    private static final Map<ResourceLocation, Supplier<?>> LOADERS;
    public static final WritableRegistry<WritableRegistry<?>> REGISTRY;
    public static final Registry<SoundEvent> SOUND_EVENT;
    public static final DefaultedRegistry<Fluid> FLUID;
    public static final Registry<MobEffect> MOB_EFFECT;
    public static final DefaultedRegistry<Block> BLOCK;
    public static final Registry<Enchantment> ENCHANTMENT;
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE;
    public static final DefaultedRegistry<Item> ITEM;
    public static final DefaultedRegistry<Potion> POTION;
    public static final Registry<WorldCarver<?>> CARVER;
    public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER;
    public static final Registry<Feature<?>> FEATURE;
    public static final Registry<FeatureDecorator<?>> DECORATOR;
    public static final Registry<Biome> BIOME;
    public static final Registry<ParticleType<? extends ParticleOptions>> PARTICLE_TYPE;
    public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE;
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE;
    public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE;
    public static final Registry<DimensionType> DIMENSION_TYPE;
    public static final DefaultedRegistry<Motive> MOTIVE;
    public static final Registry<ResourceLocation> CUSTOM_STAT;
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS;
    public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE;
    public static final Registry<StructurePieceType> STRUCTURE_PIECE;
    public static final Registry<RuleTestType> RULE_TEST;
    public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR;
    public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT;
    public static final Registry<MenuType<?>> MENU;
    public static final Registry<RecipeType<?>> RECIPE_TYPE;
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER;
    public static final Registry<StatType<?>> STAT_TYPE;
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE;
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION;
    public static final DefaultedRegistry<PoiType> POINT_OF_INTEREST_TYPE;
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE;
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE;
    public static final Registry<Schedule> SCHEDULE;
    public static final Registry<Activity> ACTIVITY;
    
    private static <T> Registry<T> registerSimple(final String string, final Supplier<T> supplier) {
        return Registry.<T, MappedRegistry<T>>internalRegister(string, new MappedRegistry<T>(), supplier);
    }
    
    private static <T> DefaultedRegistry<T> registerDefaulted(final String string1, final String string2, final Supplier<T> supplier) {
        return Registry.<T, DefaultedRegistry<T>>internalRegister(string1, new DefaultedRegistry<T>(string2), supplier);
    }
    
    private static <T, R extends WritableRegistry<T>> R internalRegister(final String string, final R ft, final Supplier<T> supplier) {
        final ResourceLocation qv4 = new ResourceLocation(string);
        Registry.LOADERS.put(qv4, supplier);
        return Registry.REGISTRY.<R>register(qv4, ft);
    }
    
    @Nullable
    public abstract ResourceLocation getKey(final T object);
    
    public abstract int getId(@Nullable final T object);
    
    @Nullable
    public abstract T get(@Nullable final ResourceLocation qv);
    
    public abstract Optional<T> getOptional(@Nullable final ResourceLocation qv);
    
    public abstract Set<ResourceLocation> keySet();
    
    @Nullable
    public abstract T getRandom(final Random random);
    
    public Stream<T> stream() {
        return (Stream<T>)StreamSupport.stream(this.spliterator(), false);
    }
    
    public abstract boolean containsKey(final ResourceLocation qv);
    
    public static <T> T register(final Registry<? super T> fn, final String string, final T object) {
        return Registry.<T>register(fn, new ResourceLocation(string), object);
    }
    
    public static <T> T register(final Registry<? super T> fn, final ResourceLocation qv, final T object) {
        return ((WritableRegistry)fn).<T>register(qv, object);
    }
    
    public static <T> T registerMapping(final Registry<? super T> fn, final int integer, final String string, final T object) {
        return ((WritableRegistry)fn).<T>registerMapping(integer, new ResourceLocation(string), object);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        LOADERS = (Map)Maps.newLinkedHashMap();
        REGISTRY = new MappedRegistry<WritableRegistry<?>>();
        SOUND_EVENT = Registry.<SoundEvent>registerSimple("sound_event", (java.util.function.Supplier<SoundEvent>)(() -> SoundEvents.ITEM_PICKUP));
        FLUID = Registry.<Fluid>registerDefaulted("fluid", "empty", (java.util.function.Supplier<Fluid>)(() -> Fluids.EMPTY));
        MOB_EFFECT = Registry.<MobEffect>registerSimple("mob_effect", (java.util.function.Supplier<MobEffect>)(() -> MobEffects.LUCK));
        BLOCK = Registry.<Block>registerDefaulted("block", "air", (java.util.function.Supplier<Block>)(() -> Blocks.AIR));
        ENCHANTMENT = Registry.<Enchantment>registerSimple("enchantment", (java.util.function.Supplier<Enchantment>)(() -> Enchantments.BLOCK_FORTUNE));
        ENTITY_TYPE = Registry.<EntityType<?>>registerDefaulted("entity_type", "pig", (java.util.function.Supplier<EntityType<?>>)(() -> EntityType.PIG));
        ITEM = Registry.<Item>registerDefaulted("item", "air", (java.util.function.Supplier<Item>)(() -> Items.AIR));
        POTION = Registry.<Potion>registerDefaulted("potion", "empty", (java.util.function.Supplier<Potion>)(() -> Potions.EMPTY));
        CARVER = Registry.<WorldCarver<?>>registerSimple("carver", (java.util.function.Supplier<WorldCarver<?>>)(() -> WorldCarver.CAVE));
        SURFACE_BUILDER = Registry.<SurfaceBuilder<?>>registerSimple("surface_builder", (java.util.function.Supplier<SurfaceBuilder<?>>)(() -> SurfaceBuilder.DEFAULT));
        FEATURE = Registry.<Feature<?>>registerSimple("feature", (java.util.function.Supplier<Feature<?>>)(() -> Feature.ORE));
        DECORATOR = Registry.<FeatureDecorator<?>>registerSimple("decorator", (java.util.function.Supplier<FeatureDecorator<?>>)(() -> FeatureDecorator.NOPE));
        BIOME = Registry.<Biome>registerSimple("biome", (java.util.function.Supplier<Biome>)(() -> Biomes.DEFAULT));
        PARTICLE_TYPE = Registry.<ParticleType<? extends ParticleOptions>>registerSimple("particle_type", (java.util.function.Supplier<ParticleType<? extends ParticleOptions>>)(() -> ParticleTypes.BLOCK));
        BIOME_SOURCE_TYPE = Registry.<BiomeSourceType<?, ?>>registerSimple("biome_source_type", (java.util.function.Supplier<BiomeSourceType<?, ?>>)(() -> BiomeSourceType.VANILLA_LAYERED));
        BLOCK_ENTITY_TYPE = Registry.<BlockEntityType<?>>registerSimple("block_entity_type", (java.util.function.Supplier<BlockEntityType<?>>)(() -> BlockEntityType.FURNACE));
        CHUNK_GENERATOR_TYPE = Registry.<ChunkGeneratorType<?, ?>>registerSimple("chunk_generator_type", (java.util.function.Supplier<ChunkGeneratorType<?, ?>>)(() -> ChunkGeneratorType.FLAT));
        DIMENSION_TYPE = Registry.<DimensionType>registerSimple("dimension_type", (java.util.function.Supplier<DimensionType>)(() -> DimensionType.OVERWORLD));
        MOTIVE = Registry.<Motive>registerDefaulted("motive", "kebab", (java.util.function.Supplier<Motive>)(() -> Motive.KEBAB));
        CUSTOM_STAT = Registry.<ResourceLocation>registerSimple("custom_stat", (java.util.function.Supplier<ResourceLocation>)(() -> Stats.JUMP));
        CHUNK_STATUS = Registry.<ChunkStatus>registerDefaulted("chunk_status", "empty", (java.util.function.Supplier<ChunkStatus>)(() -> ChunkStatus.EMPTY));
        STRUCTURE_FEATURE = Registry.<StructureFeature<?>>registerSimple("structure_feature", (java.util.function.Supplier<StructureFeature<?>>)(() -> StructureFeatureIO.MINESHAFT));
        STRUCTURE_PIECE = Registry.<StructurePieceType>registerSimple("structure_piece", (java.util.function.Supplier<StructurePieceType>)(() -> StructurePieceType.MINE_SHAFT_ROOM));
        RULE_TEST = Registry.<RuleTestType>registerSimple("rule_test", (java.util.function.Supplier<RuleTestType>)(() -> RuleTestType.ALWAYS_TRUE_TEST));
        STRUCTURE_PROCESSOR = Registry.<StructureProcessorType>registerSimple("structure_processor", (java.util.function.Supplier<StructureProcessorType>)(() -> StructureProcessorType.BLOCK_IGNORE));
        STRUCTURE_POOL_ELEMENT = Registry.<StructurePoolElementType>registerSimple("structure_pool_element", (java.util.function.Supplier<StructurePoolElementType>)(() -> StructurePoolElementType.EMPTY));
        MENU = Registry.<MenuType<?>>registerSimple("menu", (java.util.function.Supplier<MenuType<?>>)(() -> MenuType.ANVIL));
        RECIPE_TYPE = Registry.<RecipeType<?>>registerSimple("recipe_type", (java.util.function.Supplier<RecipeType<?>>)(() -> RecipeType.CRAFTING));
        RECIPE_SERIALIZER = Registry.<RecipeSerializer<?>>registerSimple("recipe_serializer", (java.util.function.Supplier<RecipeSerializer<?>>)(() -> RecipeSerializer.SHAPELESS_RECIPE));
        STAT_TYPE = Registry.<StatType<?>>registerSimple("stat_type", (java.util.function.Supplier<StatType<?>>)(() -> Stats.ITEM_USED));
        VILLAGER_TYPE = Registry.<VillagerType>registerDefaulted("villager_type", "plains", (java.util.function.Supplier<VillagerType>)(() -> VillagerType.PLAINS));
        VILLAGER_PROFESSION = Registry.<VillagerProfession>registerDefaulted("villager_profession", "none", (java.util.function.Supplier<VillagerProfession>)(() -> VillagerProfession.NONE));
        POINT_OF_INTEREST_TYPE = Registry.<PoiType>registerDefaulted("point_of_interest_type", "unemployed", (java.util.function.Supplier<PoiType>)(() -> PoiType.UNEMPLOYED));
        MEMORY_MODULE_TYPE = Registry.<MemoryModuleType<?>>registerDefaulted("memory_module_type", "dummy", (java.util.function.Supplier<MemoryModuleType<?>>)(() -> MemoryModuleType.DUMMY));
        SENSOR_TYPE = Registry.<SensorType<?>>registerDefaulted("sensor_type", "dummy", (java.util.function.Supplier<SensorType<?>>)(() -> SensorType.DUMMY));
        SCHEDULE = Registry.<Schedule>registerSimple("schedule", (java.util.function.Supplier<Schedule>)(() -> Schedule.EMPTY));
        ACTIVITY = Registry.<Activity>registerSimple("activity", (java.util.function.Supplier<Activity>)(() -> Activity.IDLE));
        Registry.LOADERS.entrySet().forEach(entry -> {
            if (((Supplier)entry.getValue()).get() == null) {
                Registry.LOGGER.error("Unable to bootstrap registry '{}'", entry.getKey());
            }
        });
        Registry.REGISTRY.forEach(ft -> {
            if (ft.isEmpty()) {
                Registry.LOGGER.error("Registry '{}' was empty after loading", Registry.REGISTRY.getKey(ft));
                if (SharedConstants.IS_RUNNING_IN_IDE) {
                    throw new IllegalStateException(new StringBuilder().append("Registry: '").append(Registry.REGISTRY.getKey(ft)).append("' is empty, not allowed, fix me!").toString());
                }
            }
            if (ft instanceof DefaultedRegistry) {
                final ResourceLocation qv2 = (ft).getDefaultKey();
                Validate.notNull(ft.get(qv2), new StringBuilder().append("Missing default of DefaultedMappedRegistry: ").append(qv2).toString(), new Object[0]);
            }
        });
    }
}
