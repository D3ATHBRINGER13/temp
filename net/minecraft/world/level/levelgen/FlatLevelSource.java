package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import java.util.Locale;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import com.google.common.collect.Lists;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class FlatLevelSource extends ChunkGenerator<FlatLevelGeneratorSettings> {
    private final Biome biomeWrapper;
    private final PhantomSpawner phantomSpawner;
    private final CatSpawner catSpawner;
    
    public FlatLevelSource(final LevelAccessor bhs, final BiomeSource biq, final FlatLevelGeneratorSettings cfx) {
        super(bhs, biq, cfx);
        this.phantomSpawner = new PhantomSpawner();
        this.catSpawner = new CatSpawner();
        this.biomeWrapper = this.getBiomeFromSettings();
    }
    
    private Biome getBiomeFromSettings() {
        final Biome bio2 = ((FlatLevelGeneratorSettings)this.settings).getBiome();
        final FlatLevelBiomeWrapper a3 = new FlatLevelBiomeWrapper(bio2.getSurfaceBuilder(), bio2.getPrecipitation(), bio2.getBiomeCategory(), bio2.getDepth(), bio2.getScale(), bio2.getTemperature(), bio2.getDownfall(), bio2.getWaterColor(), bio2.getWaterFogColor(), bio2.getParent());
        final Map<String, Map<String, String>> map4 = ((FlatLevelGeneratorSettings)this.settings).getStructuresOptions();
        for (final String string6 : map4.keySet()) {
            final ConfiguredFeature<?>[] arr7 = FlatLevelGeneratorSettings.STRUCTURE_FEATURES.get(string6);
            if (arr7 == null) {
                continue;
            }
            for (final ConfiguredFeature<?> cal11 : arr7) {
                a3.addFeature((GenerationStep.Decoration)FlatLevelGeneratorSettings.STRUCTURE_FEATURES_STEP.get(cal11), cal11);
                final ConfiguredFeature<?> cal12 = ((DecoratedFeatureConfiguration)cal11.config).feature;
                if (cal12.feature instanceof StructureFeature) {
                    final StructureFeature<FeatureConfiguration> ceu13 = (StructureFeature<FeatureConfiguration>)(StructureFeature)cal12.feature;
                    final FeatureConfiguration cbo14 = bio2.<FeatureConfiguration>getStructureConfiguration(ceu13);
                    a3.<FeatureConfiguration>addStructureStart(ceu13, (cbo14 != null) ? cbo14 : ((FeatureConfiguration)FlatLevelGeneratorSettings.STRUCTURE_FEATURES_DEFAULT.get(cal11)));
                }
            }
        }
        final boolean boolean5 = (!((FlatLevelGeneratorSettings)this.settings).isVoidGen() || bio2 == Biomes.THE_VOID) && map4.containsKey("decoration");
        if (boolean5) {
            final List<GenerationStep.Decoration> list6 = (List<GenerationStep.Decoration>)Lists.newArrayList();
            list6.add(GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
            list6.add(GenerationStep.Decoration.SURFACE_STRUCTURES);
            for (final GenerationStep.Decoration b10 : GenerationStep.Decoration.values()) {
                if (!list6.contains(b10)) {
                    for (final ConfiguredFeature<?> cal12 : bio2.getFeaturesForStep(b10)) {
                        a3.addFeature(b10, cal12);
                    }
                }
            }
        }
        final BlockState[] arr8 = ((FlatLevelGeneratorSettings)this.settings).getLayers();
        for (int integer7 = 0; integer7 < arr8.length; ++integer7) {
            final BlockState bvt8 = arr8[integer7];
            if (bvt8 != null && !Heightmap.Types.MOTION_BLOCKING.isOpaque().test(bvt8)) {
                ((FlatLevelGeneratorSettings)this.settings).deleteLayer(integer7);
                a3.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Biome.<LayerConfiguration, NoneDecoratorConfiguration>makeComposite(Feature.FILL_LAYER, new LayerConfiguration(integer7, bvt8), FeatureDecorator.NOPE, DecoratorConfiguration.NONE));
            }
        }
        return a3;
    }
    
    @Override
    public void buildSurfaceAndBedrock(final ChunkAccess bxh) {
    }
    
    @Override
    public int getSpawnHeight() {
        final ChunkAccess bxh2 = this.level.getChunk(0, 0);
        return bxh2.getHeight(Heightmap.Types.MOTION_BLOCKING, 8, 8);
    }
    
    @Override
    protected Biome getCarvingBiome(final ChunkAccess bxh) {
        return this.biomeWrapper;
    }
    
    @Override
    protected Biome getDecorationBiome(final WorldGenRegion vq, final BlockPos ew) {
        return this.biomeWrapper;
    }
    
    @Override
    public void fillFromNoise(final LevelAccessor bhs, final ChunkAccess bxh) {
        final BlockState[] arr4 = ((FlatLevelGeneratorSettings)this.settings).getLayers();
        final BlockPos.MutableBlockPos a5 = new BlockPos.MutableBlockPos();
        final Heightmap bza6 = bxh.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        final Heightmap bza7 = bxh.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        for (int integer8 = 0; integer8 < arr4.length; ++integer8) {
            final BlockState bvt9 = arr4[integer8];
            if (bvt9 != null) {
                for (int integer9 = 0; integer9 < 16; ++integer9) {
                    for (int integer10 = 0; integer10 < 16; ++integer10) {
                        bxh.setBlockState(a5.set(integer9, integer8, integer10), bvt9, false);
                        bza6.update(integer9, integer8, integer10, bvt9);
                        bza7.update(integer9, integer8, integer10, bvt9);
                    }
                }
            }
        }
    }
    
    @Override
    public int getBaseHeight(final int integer1, final int integer2, final Heightmap.Types a) {
        final BlockState[] arr5 = ((FlatLevelGeneratorSettings)this.settings).getLayers();
        for (int integer3 = arr5.length - 1; integer3 >= 0; --integer3) {
            final BlockState bvt7 = arr5[integer3];
            if (bvt7 != null) {
                if (a.isOpaque().test(bvt7)) {
                    return integer3 + 1;
                }
            }
        }
        return 0;
    }
    
    @Override
    public void tickCustomSpawners(final ServerLevel vk, final boolean boolean2, final boolean boolean3) {
        this.phantomSpawner.tick(vk, boolean2, boolean3);
        this.catSpawner.tick(vk, boolean2, boolean3);
    }
    
    @Override
    public boolean isBiomeValidStartForStructure(final Biome bio, final StructureFeature<? extends FeatureConfiguration> ceu) {
        return this.biomeWrapper.isValidStart(ceu);
    }
    
    @Nullable
    @Override
    public <C extends FeatureConfiguration> C getStructureConfiguration(final Biome bio, final StructureFeature<C> ceu) {
        return this.biomeWrapper.<C>getStructureConfiguration(ceu);
    }
    
    @Nullable
    @Override
    public BlockPos findNearestMapFeature(final Level bhr, final String string, final BlockPos ew, final int integer, final boolean boolean5) {
        if (!((FlatLevelGeneratorSettings)this.settings).getStructuresOptions().keySet().contains(string.toLowerCase(Locale.ROOT))) {
            return null;
        }
        return super.findNearestMapFeature(bhr, string, ew, integer, boolean5);
    }
    
    class FlatLevelBiomeWrapper extends Biome {
        protected FlatLevelBiomeWrapper(final ConfiguredSurfaceBuilder<?> cjx, final Precipitation d, final BiomeCategory b, final float float5, final float float6, final float float7, final float float8, final int integer9, final int integer10, @Nullable final String string) {
            super(new BiomeBuilder().surfaceBuilder(cjx).precipitation(d).biomeCategory(b).depth(float5).scale(float6).temperature(float7).downfall(float8).waterColor(integer9).waterFogColor(integer10).parent(string));
        }
    }
}
