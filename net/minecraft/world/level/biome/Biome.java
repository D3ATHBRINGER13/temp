package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.WeighedRandom;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import java.util.function.Supplier;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import java.util.Iterator;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import java.util.Random;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.core.Registry;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import java.util.List;
import net.minecraft.world.level.levelgen.GenerationStep;
import java.util.Map;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.core.IdMapper;
import java.util.Set;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
    public static final Logger LOGGER;
    public static final Set<Biome> EXPLORABLE_BIOMES;
    public static final IdMapper<Biome> MUTATED_BIOMES;
    protected static final PerlinSimplexNoise TEMPERATURE_NOISE;
    public static final PerlinSimplexNoise BIOME_INFO_NOISE;
    @Nullable
    protected String descriptionId;
    protected final float depth;
    protected final float scale;
    protected final float temperature;
    protected final float downfall;
    protected final int waterColor;
    protected final int waterFogColor;
    @Nullable
    protected final String parent;
    protected final ConfiguredSurfaceBuilder<?> surfaceBuilder;
    protected final BiomeCategory biomeCategory;
    protected final Precipitation precipitation;
    protected final Map<GenerationStep.Carving, List<ConfiguredWorldCarver<?>>> carvers;
    protected final Map<GenerationStep.Decoration, List<ConfiguredFeature<?>>> features;
    protected final List<ConfiguredFeature<?>> flowerFeatures;
    protected final Map<StructureFeature<?>, FeatureConfiguration> validFeatureStarts;
    private final Map<MobCategory, List<SpawnerData>> spawners;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache;
    
    @Nullable
    public static Biome getMutatedVariant(final Biome bio) {
        return Biome.MUTATED_BIOMES.byId(Registry.BIOME.getId(bio));
    }
    
    public static <C extends CarverConfiguration> ConfiguredWorldCarver<C> makeCarver(final WorldCarver<C> bzt, final C bzm) {
        return new ConfiguredWorldCarver<C>(bzt, bzm);
    }
    
    public static <F extends FeatureConfiguration, D extends DecoratorConfiguration> ConfiguredFeature<?> makeComposite(final Feature<F> cbn, final F cbo, final FeatureDecorator<D> che, final D cax) {
        final Feature<DecoratedFeatureConfiguration> cbn2 = (cbn instanceof FlowerFeature) ? Feature.DECORATED_FLOWER : Feature.DECORATED;
        return new ConfiguredFeature<>(cbn2, new DecoratedFeatureConfiguration((Feature<F>)cbn, (F)cbo, (FeatureDecorator<D>)che, (D)cax));
    }
    
    protected Biome(final BiomeBuilder a) {
        this.carvers = (Map<GenerationStep.Carving, List<ConfiguredWorldCarver<?>>>)Maps.newHashMap();
        this.features = (Map<GenerationStep.Decoration, List<ConfiguredFeature<?>>>)Maps.newHashMap();
        this.flowerFeatures = (List<ConfiguredFeature<?>>)Lists.newArrayList();
        this.validFeatureStarts = (Map<StructureFeature<?>, FeatureConfiguration>)Maps.newHashMap();
        this.spawners = (Map<MobCategory, List<SpawnerData>>)Maps.newHashMap();
        this.temperatureCache = (ThreadLocal<Long2FloatLinkedOpenHashMap>)ThreadLocal.withInitial(() -> Util.<Long2FloatLinkedOpenHashMap>make((java.util.function.Supplier<Long2FloatLinkedOpenHashMap>)(() -> {
            final Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap2 = new Long2FloatLinkedOpenHashMap(1024, 0.25f) {
                protected void rehash(final int integer) {
                }
            };
            long2FloatLinkedOpenHashMap2.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap2;
        })));
        if (a.surfaceBuilder == null || a.precipitation == null || a.biomeCategory == null || a.depth == null || a.scale == null || a.temperature == null || a.downfall == null || a.waterColor == null || a.waterFogColor == null) {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + a);
        }
        this.surfaceBuilder = a.surfaceBuilder;
        this.precipitation = a.precipitation;
        this.biomeCategory = a.biomeCategory;
        this.depth = a.depth;
        this.scale = a.scale;
        this.temperature = a.temperature;
        this.downfall = a.downfall;
        this.waterColor = a.waterColor;
        this.waterFogColor = a.waterFogColor;
        this.parent = a.parent;
        for (final GenerationStep.Decoration b6 : GenerationStep.Decoration.values()) {
            this.features.put(b6, Lists.newArrayList());
        }
        for (final MobCategory aiz6 : MobCategory.values()) {
            this.spawners.put(aiz6, Lists.newArrayList());
        }
    }
    
    public boolean isMutated() {
        return this.parent != null;
    }
    
    public int getSkyColor(float float1) {
        float1 /= 3.0f;
        float1 = Mth.clamp(float1, -1.0f, 1.0f);
        return Mth.hsvToRgb(0.62222224f - float1 * 0.05f, 0.5f + float1 * 0.1f, 1.0f);
    }
    
    protected void addSpawn(final MobCategory aiz, final SpawnerData e) {
        ((List)this.spawners.get(aiz)).add(e);
    }
    
    public List<SpawnerData> getMobs(final MobCategory aiz) {
        return (List<SpawnerData>)this.spawners.get(aiz);
    }
    
    public Precipitation getPrecipitation() {
        return this.precipitation;
    }
    
    public boolean isHumid() {
        return this.getDownfall() > 0.85f;
    }
    
    public float getCreatureProbability() {
        return 0.1f;
    }
    
    protected float getTemperatureNoCache(final BlockPos ew) {
        if (ew.getY() > 64) {
            final float float3 = (float)(Biome.TEMPERATURE_NOISE.getValue(ew.getX() / 8.0f, ew.getZ() / 8.0f) * 4.0);
            return this.getTemperature() - (float3 + ew.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return this.getTemperature();
    }
    
    public final float getTemperature(final BlockPos ew) {
        final long long3 = ew.asLong();
        final Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap5 = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
        final float float6 = long2FloatLinkedOpenHashMap5.get(long3);
        if (!Float.isNaN(float6)) {
            return float6;
        }
        final float float7 = this.getTemperatureNoCache(ew);
        if (long2FloatLinkedOpenHashMap5.size() == 1024) {
            long2FloatLinkedOpenHashMap5.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap5.put(long3, float7);
        return float7;
    }
    
    public boolean shouldFreeze(final LevelReader bhu, final BlockPos ew) {
        return this.shouldFreeze(bhu, ew, true);
    }
    
    public boolean shouldFreeze(final LevelReader bhu, final BlockPos ew, final boolean boolean3) {
        if (this.getTemperature(ew) >= 0.15f) {
            return false;
        }
        if (ew.getY() >= 0 && ew.getY() < 256 && bhu.getBrightness(LightLayer.BLOCK, ew) < 10) {
            final BlockState bvt5 = bhu.getBlockState(ew);
            final FluidState clk6 = bhu.getFluidState(ew);
            if (clk6.getType() == Fluids.WATER && bvt5.getBlock() instanceof LiquidBlock) {
                if (!boolean3) {
                    return true;
                }
                final boolean boolean4 = bhu.isWaterAt(ew.west()) && bhu.isWaterAt(ew.east()) && bhu.isWaterAt(ew.north()) && bhu.isWaterAt(ew.south());
                if (!boolean4) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean shouldSnow(final LevelReader bhu, final BlockPos ew) {
        if (this.getTemperature(ew) >= 0.15f) {
            return false;
        }
        if (ew.getY() >= 0 && ew.getY() < 256 && bhu.getBrightness(LightLayer.BLOCK, ew) < 10) {
            final BlockState bvt4 = bhu.getBlockState(ew);
            if (bvt4.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(bhu, ew)) {
                return true;
            }
        }
        return false;
    }
    
    public void addFeature(final GenerationStep.Decoration b, final ConfiguredFeature<?> cal) {
        if (cal.feature == Feature.DECORATED_FLOWER) {
            this.flowerFeatures.add(cal);
        }
        ((List)this.features.get(b)).add(cal);
    }
    
    public <C extends CarverConfiguration> void addCarver(final GenerationStep.Carving a, final ConfiguredWorldCarver<C> bzo) {
        ((List)this.carvers.computeIfAbsent(a, a -> Lists.newArrayList())).add(bzo);
    }
    
    public List<ConfiguredWorldCarver<?>> getCarvers(final GenerationStep.Carving a) {
        return (List<ConfiguredWorldCarver<?>>)this.carvers.computeIfAbsent(a, a -> Lists.newArrayList());
    }
    
    public <C extends FeatureConfiguration> void addStructureStart(final StructureFeature<C> ceu, final C cbo) {
        this.validFeatureStarts.put(ceu, cbo);
    }
    
    public <C extends FeatureConfiguration> boolean isValidStart(final StructureFeature<C> ceu) {
        return this.validFeatureStarts.containsKey(ceu);
    }
    
    @Nullable
    public <C extends FeatureConfiguration> C getStructureConfiguration(final StructureFeature<C> ceu) {
        return (C)this.validFeatureStarts.get(ceu);
    }
    
    public List<ConfiguredFeature<?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }
    
    public List<ConfiguredFeature<?>> getFeaturesForStep(final GenerationStep.Decoration b) {
        return (List<ConfiguredFeature<?>>)this.features.get(b);
    }
    
    public void generate(final GenerationStep.Decoration b, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final LevelAccessor bhs, final long long4, final WorldgenRandom bzk, final BlockPos ew) {
        int integer9 = 0;
        for (final ConfiguredFeature<?> cal11 : (List)this.features.get(b)) {
            bzk.setFeatureSeed(long4, integer9, b.ordinal());
            try {
                cal11.place(bhs, bxi, bzk, ew);
            }
            catch (Exception exception12) {
                final CrashReport d13 = CrashReport.forThrowable((Throwable)exception12, "Feature placement");
                d13.addCategory("Feature").setDetail("Id", Registry.FEATURE.getKey(cal11.feature)).setDetail("Description", (CrashReportDetail<String>)cal11.feature::toString);
                throw new ReportedException(d13);
            }
            ++integer9;
        }
    }
    
    public int getGrassColor(final BlockPos ew) {
        final double double3 = Mth.clamp(this.getTemperature(ew), 0.0f, 1.0f);
        final double double4 = Mth.clamp(this.getDownfall(), 0.0f, 1.0f);
        return GrassColor.get(double3, double4);
    }
    
    public int getFoliageColor(final BlockPos ew) {
        final double double3 = Mth.clamp(this.getTemperature(ew), 0.0f, 1.0f);
        final double double4 = Mth.clamp(this.getDownfall(), 0.0f, 1.0f);
        return FoliageColor.get(double3, double4);
    }
    
    public void buildSurfaceAt(final Random random, final ChunkAccess bxh, final int integer3, final int integer4, final int integer5, final double double6, final BlockState bvt7, final BlockState bvt8, final int integer9, final long long10) {
        this.surfaceBuilder.initNoise(long10);
        this.surfaceBuilder.apply(random, bxh, this, integer3, integer4, integer5, double6, bvt7, bvt8, integer9, long10);
    }
    
    public BiomeTempCategory getTemperatureCategory() {
        if (this.biomeCategory == BiomeCategory.OCEAN) {
            return BiomeTempCategory.OCEAN;
        }
        if (this.getTemperature() < 0.2) {
            return BiomeTempCategory.COLD;
        }
        if (this.getTemperature() < 1.0) {
            return BiomeTempCategory.MEDIUM;
        }
        return BiomeTempCategory.WARM;
    }
    
    public final float getDepth() {
        return this.depth;
    }
    
    public final float getDownfall() {
        return this.downfall;
    }
    
    public Component getName() {
        return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
    }
    
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("biome", Registry.BIOME.getKey(this));
        }
        return this.descriptionId;
    }
    
    public final float getScale() {
        return this.scale;
    }
    
    public final float getTemperature() {
        return this.temperature;
    }
    
    public final int getWaterColor() {
        return this.waterColor;
    }
    
    public final int getWaterFogColor() {
        return this.waterFogColor;
    }
    
    public final BiomeCategory getBiomeCategory() {
        return this.biomeCategory;
    }
    
    public ConfiguredSurfaceBuilder<?> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }
    
    public SurfaceBuilderConfiguration getSurfaceBuilderConfig() {
        return (SurfaceBuilderConfiguration)this.surfaceBuilder.getSurfaceBuilderConfiguration();
    }
    
    @Nullable
    public String getParent() {
        return this.parent;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EXPLORABLE_BIOMES = (Set)Sets.newHashSet();
        MUTATED_BIOMES = new IdMapper<Biome>();
        TEMPERATURE_NOISE = new PerlinSimplexNoise(new Random(1234L), 1);
        BIOME_INFO_NOISE = new PerlinSimplexNoise(new Random(2345L), 1);
    }
    
    public enum BiomeTempCategory {
        OCEAN("ocean"), 
        COLD("cold"), 
        MEDIUM("medium"), 
        WARM("warm");
        
        private static final Map<String, BiomeTempCategory> BY_NAME;
        private final String name;
        
        private BiomeTempCategory(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(BiomeTempCategory::getName, c -> c));
        }
    }
    
    public enum BiomeCategory {
        NONE("none"), 
        TAIGA("taiga"), 
        EXTREME_HILLS("extreme_hills"), 
        JUNGLE("jungle"), 
        MESA("mesa"), 
        PLAINS("plains"), 
        SAVANNA("savanna"), 
        ICY("icy"), 
        THEEND("the_end"), 
        BEACH("beach"), 
        FOREST("forest"), 
        OCEAN("ocean"), 
        DESERT("desert"), 
        RIVER("river"), 
        SWAMP("swamp"), 
        MUSHROOM("mushroom"), 
        NETHER("nether");
        
        private static final Map<String, BiomeCategory> BY_NAME;
        private final String name;
        
        private BiomeCategory(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(BiomeCategory::getName, b -> b));
        }
    }
    
    public enum Precipitation {
        NONE("none"), 
        RAIN("rain"), 
        SNOW("snow");
        
        private static final Map<String, Precipitation> BY_NAME;
        private final String name;
        
        private Precipitation(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Precipitation::getName, d -> d));
        }
    }
    
    public static class SpawnerData extends WeighedRandom.WeighedRandomItem {
        public final EntityType<?> type;
        public final int minCount;
        public final int maxCount;
        
        public SpawnerData(final EntityType<?> ais, final int integer2, final int integer3, final int integer4) {
            super(integer2);
            this.type = ais;
            this.minCount = integer3;
            this.maxCount = integer4;
        }
        
        public String toString() {
            return new StringBuilder().append(EntityType.getKey(this.type)).append("*(").append(this.minCount).append("-").append(this.maxCount).append("):").append(this.weight).toString();
        }
    }
    
    public static class BiomeBuilder {
        @Nullable
        private ConfiguredSurfaceBuilder<?> surfaceBuilder;
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private BiomeCategory biomeCategory;
        @Nullable
        private Float depth;
        @Nullable
        private Float scale;
        @Nullable
        private Float temperature;
        @Nullable
        private Float downfall;
        @Nullable
        private Integer waterColor;
        @Nullable
        private Integer waterFogColor;
        @Nullable
        private String parent;
        
        public <SC extends SurfaceBuilderConfiguration> BiomeBuilder surfaceBuilder(final SurfaceBuilder<SC> ckh, final SC ckj) {
            this.surfaceBuilder = new ConfiguredSurfaceBuilder<>(ckh, ckj);
            return this;
        }
        
        public BiomeBuilder surfaceBuilder(final ConfiguredSurfaceBuilder<?> cjx) {
            this.surfaceBuilder = cjx;
            return this;
        }
        
        public BiomeBuilder precipitation(final Precipitation d) {
            this.precipitation = d;
            return this;
        }
        
        public BiomeBuilder biomeCategory(final BiomeCategory b) {
            this.biomeCategory = b;
            return this;
        }
        
        public BiomeBuilder depth(final float float1) {
            this.depth = float1;
            return this;
        }
        
        public BiomeBuilder scale(final float float1) {
            this.scale = float1;
            return this;
        }
        
        public BiomeBuilder temperature(final float float1) {
            this.temperature = float1;
            return this;
        }
        
        public BiomeBuilder downfall(final float float1) {
            this.downfall = float1;
            return this;
        }
        
        public BiomeBuilder waterColor(final int integer) {
            this.waterColor = integer;
            return this;
        }
        
        public BiomeBuilder waterFogColor(final int integer) {
            this.waterFogColor = integer;
            return this;
        }
        
        public BiomeBuilder parent(@Nullable final String string) {
            this.parent = string;
            return this;
        }
        
        public String toString() {
            return new StringBuilder().append("BiomeBuilder{\nsurfaceBuilder=").append(this.surfaceBuilder).append(",\nprecipitation=").append(this.precipitation).append(",\nbiomeCategory=").append(this.biomeCategory).append(",\ndepth=").append(this.depth).append(",\nscale=").append(this.scale).append(",\ntemperature=").append(this.temperature).append(",\ndownfall=").append(this.downfall).append(",\nwaterColor=").append(this.waterColor).append(",\nwaterFogColor=").append(this.waterFogColor).append(",\nparent='").append(this.parent).append('\'').append("\n").append('}').toString();
        }
    }
}
