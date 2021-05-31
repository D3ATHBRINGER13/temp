package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SurfaceBuilder<C extends SurfaceBuilderConfiguration> {
    public static final BlockState AIR;
    public static final BlockState DIRT;
    public static final BlockState GRASS_BLOCK;
    public static final BlockState PODZOL;
    public static final BlockState GRAVEL;
    public static final BlockState STONE;
    public static final BlockState COARSE_DIRT;
    public static final BlockState SAND;
    public static final BlockState RED_SAND;
    public static final BlockState WHITE_TERRACOTTA;
    public static final BlockState MYCELIUM;
    public static final BlockState NETHERRACK;
    public static final BlockState ENDSTONE;
    public static final SurfaceBuilderBaseConfiguration CONFIG_EMPTY;
    public static final SurfaceBuilderBaseConfiguration CONFIG_PODZOL;
    public static final SurfaceBuilderBaseConfiguration CONFIG_GRAVEL;
    public static final SurfaceBuilderBaseConfiguration CONFIG_GRASS;
    public static final SurfaceBuilderBaseConfiguration CONFIG_DIRT;
    public static final SurfaceBuilderBaseConfiguration CONFIG_STONE;
    public static final SurfaceBuilderBaseConfiguration CONFIG_COARSE_DIRT;
    public static final SurfaceBuilderBaseConfiguration CONFIG_DESERT;
    public static final SurfaceBuilderBaseConfiguration CONFIG_OCEAN_SAND;
    public static final SurfaceBuilderBaseConfiguration CONFIG_FULL_SAND;
    public static final SurfaceBuilderBaseConfiguration CONFIG_BADLANDS;
    public static final SurfaceBuilderBaseConfiguration CONFIG_MYCELIUM;
    public static final SurfaceBuilderBaseConfiguration CONFIG_HELL;
    public static final SurfaceBuilderBaseConfiguration CONFIG_THEEND;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> DEFAULT;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> MOUNTAIN;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> SHATTERED_SAVANNA;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> GRAVELLY_MOUNTAIN;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> GIANT_TREE_TAIGA;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> SWAMP;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> BADLANDS;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> WOODED_BADLANDS;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> ERODED_BADLANDS;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> FROZEN_OCEAN;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> NETHER;
    public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> NOPE;
    private final Function<Dynamic<?>, ? extends C> configurationFactory;
    
    private static <C extends SurfaceBuilderConfiguration, F extends SurfaceBuilder<C>> F register(final String string, final F ckh) {
        return Registry.<F>register(Registry.SURFACE_BUILDER, string, ckh);
    }
    
    public SurfaceBuilder(final Function<Dynamic<?>, ? extends C> function) {
        this.configurationFactory = function;
    }
    
    public abstract void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final C ckj);
    
    public void initNoise(final long long1) {
    }
    
    static {
        AIR = Blocks.AIR.defaultBlockState();
        DIRT = Blocks.DIRT.defaultBlockState();
        GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
        PODZOL = Blocks.PODZOL.defaultBlockState();
        GRAVEL = Blocks.GRAVEL.defaultBlockState();
        STONE = Blocks.STONE.defaultBlockState();
        COARSE_DIRT = Blocks.COARSE_DIRT.defaultBlockState();
        SAND = Blocks.SAND.defaultBlockState();
        RED_SAND = Blocks.RED_SAND.defaultBlockState();
        WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
        MYCELIUM = Blocks.MYCELIUM.defaultBlockState();
        NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
        ENDSTONE = Blocks.END_STONE.defaultBlockState();
        CONFIG_EMPTY = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.AIR, SurfaceBuilder.AIR, SurfaceBuilder.AIR);
        CONFIG_PODZOL = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.PODZOL, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        CONFIG_GRAVEL = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.GRAVEL, SurfaceBuilder.GRAVEL, SurfaceBuilder.GRAVEL);
        CONFIG_GRASS = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.GRASS_BLOCK, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        CONFIG_DIRT = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.DIRT, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        CONFIG_STONE = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.STONE, SurfaceBuilder.STONE, SurfaceBuilder.GRAVEL);
        CONFIG_COARSE_DIRT = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.COARSE_DIRT, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        CONFIG_DESERT = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.SAND, SurfaceBuilder.SAND, SurfaceBuilder.GRAVEL);
        CONFIG_OCEAN_SAND = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.GRASS_BLOCK, SurfaceBuilder.DIRT, SurfaceBuilder.SAND);
        CONFIG_FULL_SAND = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.SAND, SurfaceBuilder.SAND, SurfaceBuilder.SAND);
        CONFIG_BADLANDS = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.RED_SAND, SurfaceBuilder.WHITE_TERRACOTTA, SurfaceBuilder.GRAVEL);
        CONFIG_MYCELIUM = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.MYCELIUM, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        CONFIG_HELL = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.NETHERRACK, SurfaceBuilder.NETHERRACK, SurfaceBuilder.NETHERRACK);
        CONFIG_THEEND = new SurfaceBuilderBaseConfiguration(SurfaceBuilder.ENDSTONE, SurfaceBuilder.ENDSTONE, SurfaceBuilder.ENDSTONE);
        DEFAULT = SurfaceBuilder.<SurfaceBuilderConfiguration, DefaultSurfaceBuilder>register("default", new DefaultSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        MOUNTAIN = SurfaceBuilder.<SurfaceBuilderConfiguration, MountainSurfaceBuilder>register("mountain", new MountainSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        SHATTERED_SAVANNA = SurfaceBuilder.<SurfaceBuilderConfiguration, ShatteredSavanaSurfaceBuilder>register("shattered_savanna", new ShatteredSavanaSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        GRAVELLY_MOUNTAIN = SurfaceBuilder.<SurfaceBuilderConfiguration, GravellyMountainSurfaceBuilder>register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        GIANT_TREE_TAIGA = SurfaceBuilder.<SurfaceBuilderConfiguration, GiantTreeTaigaSurfaceBuilder>register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        SWAMP = SurfaceBuilder.<SurfaceBuilderConfiguration, SwampSurfaceBuilder>register("swamp", new SwampSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        BADLANDS = SurfaceBuilder.<SurfaceBuilderConfiguration, BadlandsSurfaceBuilder>register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        WOODED_BADLANDS = SurfaceBuilder.<SurfaceBuilderConfiguration, WoodedBadlandsSurfaceBuilder>register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        ERODED_BADLANDS = SurfaceBuilder.<SurfaceBuilderConfiguration, ErodedBadlandsSurfaceBuilder>register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        FROZEN_OCEAN = SurfaceBuilder.<SurfaceBuilderConfiguration, FrozenOceanSurfaceBuilder>register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        NETHER = SurfaceBuilder.<SurfaceBuilderConfiguration, NetherSurfaceBuilder>register("nether", new NetherSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
        NOPE = SurfaceBuilder.<SurfaceBuilderConfiguration, NopeSurfaceBuilder>register("nope", new NopeSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
    }
}
