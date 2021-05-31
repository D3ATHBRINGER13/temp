package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.LazyAreaContext;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.LazyArea;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.area.Area;
import java.util.function.LongFunction;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public class Layers {
    protected static final int WARM_OCEAN;
    protected static final int LUKEWARM_OCEAN;
    protected static final int OCEAN;
    protected static final int COLD_OCEAN;
    protected static final int FROZEN_OCEAN;
    protected static final int DEEP_WARM_OCEAN;
    protected static final int DEEP_LUKEWARM_OCEAN;
    protected static final int DEEP_OCEAN;
    protected static final int DEEP_COLD_OCEAN;
    protected static final int DEEP_FROZEN_OCEAN;
    
    private static <T extends Area, C extends BigContext<T>> AreaFactory<T> zoom(final long long1, final AreaTransformer1 cna, final AreaFactory<T> clu, final int integer, final LongFunction<C> longFunction) {
        AreaFactory<T> clu2 = clu;
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            clu2 = cna.<T>run((BigContext<T>)longFunction.apply(long1 + integer2), clu2);
        }
        return clu2;
    }
    
    public static <T extends Area, C extends BigContext<T>> ImmutableList<AreaFactory<T>> getDefaultLayers(final LevelType bhy, final OverworldGeneratorSettings bze, final LongFunction<C> longFunction) {
        AreaFactory<T> clu4 = IslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1L));
        clu4 = ZoomLayer.FUZZY.<T>run((BigContext<T>)longFunction.apply(2000L), clu4);
        clu4 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1L), clu4);
        clu4 = ZoomLayer.NORMAL.<T>run((BigContext<T>)longFunction.apply(2001L), clu4);
        clu4 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(2L), clu4);
        clu4 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(50L), clu4);
        clu4 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(70L), clu4);
        clu4 = RemoveTooMuchOceanLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(2L), clu4);
        AreaFactory<T> clu5 = OceanLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(2L));
        clu5 = Layers.<T, C>zoom(2001L, ZoomLayer.NORMAL, clu5, 6, longFunction);
        clu4 = AddSnowLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(2L), clu4);
        clu4 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(3L), clu4);
        clu4 = AddEdgeLayer.CoolWarm.INSTANCE.<T>run((BigContext<T>)longFunction.apply(2L), clu4);
        clu4 = AddEdgeLayer.HeatIce.INSTANCE.<T>run((BigContext<T>)longFunction.apply(2L), clu4);
        clu4 = AddEdgeLayer.IntroduceSpecial.INSTANCE.<T>run((BigContext<T>)longFunction.apply(3L), clu4);
        clu4 = ZoomLayer.NORMAL.<T>run((BigContext<T>)longFunction.apply(2002L), clu4);
        clu4 = ZoomLayer.NORMAL.<T>run((BigContext<T>)longFunction.apply(2003L), clu4);
        clu4 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(4L), clu4);
        clu4 = AddMushroomIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(5L), clu4);
        clu4 = AddDeepOceanLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(4L), clu4);
        clu4 = Layers.<T, C>zoom(1000L, ZoomLayer.NORMAL, clu4, 0, longFunction);
        int integer7;
        int integer6 = integer7 = 4;
        if (bze != null) {
            integer6 = bze.getBiomeSize();
            integer7 = bze.getRiverSize();
        }
        if (bhy == LevelType.LARGE_BIOMES) {
            integer6 = 6;
        }
        AreaFactory<T> clu6 = clu4;
        clu6 = Layers.<T, C>zoom(1000L, ZoomLayer.NORMAL, clu6, 0, longFunction);
        clu6 = RiverInitLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(100L), clu6);
        AreaFactory<T> clu7 = clu4;
        clu7 = new BiomeInitLayer(bhy, bze).<T>run((BigContext<T>)longFunction.apply(200L), clu7);
        clu7 = RareBiomeLargeLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1001L), clu7);
        clu7 = Layers.<T, C>zoom(1000L, ZoomLayer.NORMAL, clu7, 2, longFunction);
        clu7 = BiomeEdgeLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1000L), clu7);
        AreaFactory<T> clu8 = clu6;
        clu8 = Layers.<T, C>zoom(1000L, ZoomLayer.NORMAL, clu8, 2, longFunction);
        clu7 = RegionHillsLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1000L), clu7, clu8);
        clu6 = Layers.<T, C>zoom(1000L, ZoomLayer.NORMAL, clu6, 2, longFunction);
        clu6 = Layers.<T, C>zoom(1000L, ZoomLayer.NORMAL, clu6, integer7, longFunction);
        clu6 = RiverLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1L), clu6);
        clu6 = SmoothLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1000L), clu6);
        clu7 = RareBiomeSpotLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1001L), clu7);
        for (int integer8 = 0; integer8 < integer6; ++integer8) {
            clu7 = ZoomLayer.NORMAL.<T>run((BigContext<T>)longFunction.apply((long)(1000 + integer8)), clu7);
            if (integer8 == 0) {
                clu7 = AddIslandLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(3L), clu7);
            }
            if (integer8 == 1 || integer6 == 1) {
                clu7 = ShoreLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1000L), clu7);
            }
        }
        clu7 = SmoothLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(1000L), clu7);
        clu7 = RiverMixerLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(100L), clu7, clu6);
        final AreaFactory<T> clu9;
        clu7 = (clu9 = OceanMixerLayer.INSTANCE.<T>run((BigContext<T>)longFunction.apply(100L), clu7, clu5));
        final AreaFactory<T> clu10 = VoronoiZoom.INSTANCE.<T>run((BigContext<T>)longFunction.apply(10L), clu7);
        return (ImmutableList<AreaFactory<T>>)ImmutableList.of(clu7, clu10, clu9);
    }
    
    public static Layer[] getDefaultLayers(final long long1, final LevelType bhy, final OverworldGeneratorSettings bze) {
        final int integer5 = 25;
        final ImmutableList<AreaFactory<LazyArea>> immutableList6 = Layers.<LazyArea, BigContext>getDefaultLayers(bhy, bze, (java.util.function.LongFunction<BigContext>)(long2 -> new LazyAreaContext(25, long1, long2)));
        final Layer cmj7 = new Layer((AreaFactory<LazyArea>)immutableList6.get(0));
        final Layer cmj8 = new Layer((AreaFactory<LazyArea>)immutableList6.get(1));
        final Layer cmj9 = new Layer((AreaFactory<LazyArea>)immutableList6.get(2));
        return new Layer[] { cmj7, cmj8, cmj9 };
    }
    
    public static boolean isSame(final int integer1, final int integer2) {
        if (integer1 == integer2) {
            return true;
        }
        final Biome bio3 = Registry.BIOME.byId(integer1);
        final Biome bio4 = Registry.BIOME.byId(integer2);
        if (bio3 == null || bio4 == null) {
            return false;
        }
        if (bio3 == Biomes.WOODED_BADLANDS_PLATEAU || bio3 == Biomes.BADLANDS_PLATEAU) {
            return bio4 == Biomes.WOODED_BADLANDS_PLATEAU || bio4 == Biomes.BADLANDS_PLATEAU;
        }
        return (bio3.getBiomeCategory() != Biome.BiomeCategory.NONE && bio4.getBiomeCategory() != Biome.BiomeCategory.NONE && bio3.getBiomeCategory() == bio4.getBiomeCategory()) || bio3 == bio4;
    }
    
    protected static boolean isOcean(final int integer) {
        return integer == Layers.WARM_OCEAN || integer == Layers.LUKEWARM_OCEAN || integer == Layers.OCEAN || integer == Layers.COLD_OCEAN || integer == Layers.FROZEN_OCEAN || integer == Layers.DEEP_WARM_OCEAN || integer == Layers.DEEP_LUKEWARM_OCEAN || integer == Layers.DEEP_OCEAN || integer == Layers.DEEP_COLD_OCEAN || integer == Layers.DEEP_FROZEN_OCEAN;
    }
    
    protected static boolean isShallowOcean(final int integer) {
        return integer == Layers.WARM_OCEAN || integer == Layers.LUKEWARM_OCEAN || integer == Layers.OCEAN || integer == Layers.COLD_OCEAN || integer == Layers.FROZEN_OCEAN;
    }
    
    static {
        WARM_OCEAN = Registry.BIOME.getId(Biomes.WARM_OCEAN);
        LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.LUKEWARM_OCEAN);
        OCEAN = Registry.BIOME.getId(Biomes.OCEAN);
        COLD_OCEAN = Registry.BIOME.getId(Biomes.COLD_OCEAN);
        FROZEN_OCEAN = Registry.BIOME.getId(Biomes.FROZEN_OCEAN);
        DEEP_WARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_WARM_OCEAN);
        DEEP_LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_LUKEWARM_OCEAN);
        DEEP_OCEAN = Registry.BIOME.getId(Biomes.DEEP_OCEAN);
        DEEP_COLD_OCEAN = Registry.BIOME.getId(Biomes.DEEP_COLD_OCEAN);
        DEEP_FROZEN_OCEAN = Registry.BIOME.getId(Biomes.DEEP_FROZEN_OCEAN);
    }
}
