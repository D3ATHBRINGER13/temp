package net.minecraft.world.level.levelgen.carver;

import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import java.util.Random;
import java.util.BitSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluids;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import java.util.Set;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public abstract class WorldCarver<C extends CarverConfiguration> {
    public static final WorldCarver<ProbabilityFeatureConfiguration> CAVE;
    public static final WorldCarver<ProbabilityFeatureConfiguration> HELL_CAVE;
    public static final WorldCarver<ProbabilityFeatureConfiguration> CANYON;
    public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CANYON;
    public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CAVE;
    protected static final BlockState AIR;
    protected static final BlockState CAVE_AIR;
    protected static final FluidState WATER;
    protected static final FluidState LAVA;
    protected Set<Block> replaceableBlocks;
    protected Set<Fluid> liquids;
    private final Function<Dynamic<?>, ? extends C> configurationFactory;
    protected final int genHeight;
    
    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(final String string, final F bzt) {
        return Registry.<F>register(Registry.CARVER, string, bzt);
    }
    
    public WorldCarver(final Function<Dynamic<?>, ? extends C> function, final int integer) {
        this.replaceableBlocks = (Set<Block>)ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, (Object[])new Block[] { Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE });
        this.liquids = (Set<Fluid>)ImmutableSet.of(Fluids.WATER);
        this.configurationFactory = function;
        this.genHeight = integer;
    }
    
    public int getRange() {
        return 4;
    }
    
    protected boolean carveSphere(final ChunkAccess bxh, final long long2, final int integer3, final int integer4, final int integer5, final double double6, final double double7, final double double8, final double double9, final double double10, final BitSet bitSet) {
        final Random random19 = new Random(long2 + integer4 + integer5);
        final double double11 = integer4 * 16 + 8;
        final double double12 = integer5 * 16 + 8;
        if (double6 < double11 - 16.0 - double9 * 2.0 || double8 < double12 - 16.0 - double9 * 2.0 || double6 > double11 + 16.0 + double9 * 2.0 || double8 > double12 + 16.0 + double9 * 2.0) {
            return false;
        }
        final int integer6 = Math.max(Mth.floor(double6 - double9) - integer4 * 16 - 1, 0);
        final int integer7 = Math.min(Mth.floor(double6 + double9) - integer4 * 16 + 1, 16);
        final int integer8 = Math.max(Mth.floor(double7 - double10) - 1, 1);
        final int integer9 = Math.min(Mth.floor(double7 + double10) + 1, this.genHeight - 8);
        final int integer10 = Math.max(Mth.floor(double8 - double9) - integer5 * 16 - 1, 0);
        final int integer11 = Math.min(Mth.floor(double8 + double9) - integer5 * 16 + 1, 16);
        if (this.hasWater(bxh, integer4, integer5, integer6, integer7, integer8, integer9, integer10, integer11)) {
            return false;
        }
        boolean boolean30 = false;
        final BlockPos.MutableBlockPos a31 = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos a32 = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos a33 = new BlockPos.MutableBlockPos();
        for (int integer12 = integer6; integer12 < integer7; ++integer12) {
            final int integer13 = integer12 + integer4 * 16;
            final double double13 = (integer13 + 0.5 - double6) / double9;
            for (int integer14 = integer10; integer14 < integer11; ++integer14) {
                final int integer15 = integer14 + integer5 * 16;
                final double double14 = (integer15 + 0.5 - double8) / double9;
                if (double13 * double13 + double14 * double14 < 1.0) {
                    final AtomicBoolean atomicBoolean42 = new AtomicBoolean(false);
                    for (int integer16 = integer9; integer16 > integer8; --integer16) {
                        final double double15 = (integer16 - 0.5 - double7) / double10;
                        if (!this.skip(double13, double15, double14, integer16)) {
                            boolean30 |= this.carveBlock(bxh, bitSet, random19, a31, a32, a33, integer3, integer4, integer5, integer13, integer15, integer12, integer16, integer14, atomicBoolean42);
                        }
                    }
                }
            }
        }
        return boolean30;
    }
    
    protected boolean carveBlock(final ChunkAccess bxh, final BitSet bitSet, final Random random, final BlockPos.MutableBlockPos a4, final BlockPos.MutableBlockPos a5, final BlockPos.MutableBlockPos a6, final int integer7, final int integer8, final int integer9, final int integer10, final int integer11, final int integer12, final int integer13, final int integer14, final AtomicBoolean atomicBoolean) {
        final int integer15 = integer12 | integer14 << 4 | integer13 << 8;
        if (bitSet.get(integer15)) {
            return false;
        }
        bitSet.set(integer15);
        a4.set(integer10, integer13, integer11);
        final BlockState bvt18 = bxh.getBlockState(a4);
        final BlockState bvt19 = bxh.getBlockState(a5.set(a4).move(Direction.UP));
        if (bvt18.getBlock() == Blocks.GRASS_BLOCK || bvt18.getBlock() == Blocks.MYCELIUM) {
            atomicBoolean.set(true);
        }
        if (!this.canReplaceBlock(bvt18, bvt19)) {
            return false;
        }
        if (integer13 < 11) {
            bxh.setBlockState(a4, WorldCarver.LAVA.createLegacyBlock(), false);
        }
        else {
            bxh.setBlockState(a4, WorldCarver.CAVE_AIR, false);
            if (atomicBoolean.get()) {
                a6.set(a4).move(Direction.DOWN);
                if (bxh.getBlockState(a6).getBlock() == Blocks.DIRT) {
                    bxh.setBlockState(a6, bxh.getBiome(a4).getSurfaceBuilderConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }
    
    public abstract boolean carve(final ChunkAccess bxh, final Random random, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final BitSet bitSet, final C bzm);
    
    public abstract boolean isStartChunk(final Random random, final int integer2, final int integer3, final C bzm);
    
    protected boolean canReplaceBlock(final BlockState bvt) {
        return this.replaceableBlocks.contains(bvt.getBlock());
    }
    
    protected boolean canReplaceBlock(final BlockState bvt1, final BlockState bvt2) {
        final Block bmv4 = bvt1.getBlock();
        return this.canReplaceBlock(bvt1) || ((bmv4 == Blocks.SAND || bmv4 == Blocks.GRAVEL) && !bvt2.getFluidState().is(FluidTags.WATER));
    }
    
    protected boolean hasWater(final ChunkAccess bxh, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9) {
        final BlockPos.MutableBlockPos a11 = new BlockPos.MutableBlockPos();
        for (int integer10 = integer4; integer10 < integer5; ++integer10) {
            for (int integer11 = integer8; integer11 < integer9; ++integer11) {
                for (int integer12 = integer6 - 1; integer12 <= integer7 + 1; ++integer12) {
                    if (this.liquids.contains(bxh.getFluidState(a11.set(integer10 + integer2 * 16, integer12, integer11 + integer3 * 16)).getType())) {
                        return true;
                    }
                    if (integer12 != integer7 + 1 && !this.isEdge(integer4, integer5, integer8, integer9, integer10, integer11)) {
                        integer12 = integer7;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isEdge(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        return integer5 == integer1 || integer5 == integer2 - 1 || integer6 == integer3 || integer6 == integer4 - 1;
    }
    
    protected boolean canReach(final int integer1, final int integer2, final double double3, final double double4, final int integer5, final int integer6, final float float7) {
        final double double5 = integer1 * 16 + 8;
        final double double6 = integer2 * 16 + 8;
        final double double7 = double3 - double5;
        final double double8 = double4 - double6;
        final double double9 = integer6 - integer5;
        final double double10 = float7 + 2.0f + 16.0f;
        return double7 * double7 + double8 * double8 - double9 * double9 <= double10 * double10;
    }
    
    protected abstract boolean skip(final double double1, final double double2, final double double3, final int integer);
    
    static {
        CAVE = WorldCarver.<CarverConfiguration, CaveWorldCarver>register("cave", new CaveWorldCarver(ProbabilityFeatureConfiguration::deserialize, 256));
        HELL_CAVE = WorldCarver.<CarverConfiguration, HellCaveWorldCarver>register("hell_cave", new HellCaveWorldCarver(ProbabilityFeatureConfiguration::deserialize));
        CANYON = WorldCarver.<CarverConfiguration, CanyonWorldCarver>register("canyon", new CanyonWorldCarver(ProbabilityFeatureConfiguration::deserialize));
        UNDERWATER_CANYON = WorldCarver.<CarverConfiguration, UnderwaterCanyonWorldCarver>register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityFeatureConfiguration::deserialize));
        UNDERWATER_CAVE = WorldCarver.<CarverConfiguration, UnderwaterCaveWorldCarver>register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityFeatureConfiguration::deserialize));
        AIR = Blocks.AIR.defaultBlockState();
        CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
        WATER = Fluids.WATER.defaultFluidState();
        LAVA = Fluids.LAVA.defaultFluidState();
    }
}
