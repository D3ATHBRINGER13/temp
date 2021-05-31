package net.minecraft.world.level.levelgen.carver;

import java.util.Set;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import java.util.Random;
import java.util.BitSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class UnderwaterCaveWorldCarver extends CaveWorldCarver {
    public UnderwaterCaveWorldCarver(final Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> function) {
        super(function, 256);
        this.replaceableBlocks = (Set<Block>)ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, (Object[])new Block[] { Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE });
    }
    
    @Override
    protected boolean hasWater(final ChunkAccess bxh, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9) {
        return false;
    }
    
    @Override
    protected boolean carveBlock(final ChunkAccess bxh, final BitSet bitSet, final Random random, final BlockPos.MutableBlockPos a4, final BlockPos.MutableBlockPos a5, final BlockPos.MutableBlockPos a6, final int integer7, final int integer8, final int integer9, final int integer10, final int integer11, final int integer12, final int integer13, final int integer14, final AtomicBoolean atomicBoolean) {
        return carveBlock(this, bxh, bitSet, random, a4, integer7, integer8, integer9, integer10, integer11, integer12, integer13, integer14);
    }
    
    protected static boolean carveBlock(final WorldCarver<?> bzt, final ChunkAccess bxh, final BitSet bitSet, final Random random, final BlockPos.MutableBlockPos a, final int integer6, final int integer7, final int integer8, final int integer9, final int integer10, final int integer11, final int integer12, final int integer13) {
        if (integer12 >= integer6) {
            return false;
        }
        final int integer14 = integer11 | integer13 << 4 | integer12 << 8;
        if (bitSet.get(integer14)) {
            return false;
        }
        bitSet.set(integer14);
        a.set(integer9, integer12, integer10);
        final BlockState bvt15 = bxh.getBlockState(a);
        if (!bzt.canReplaceBlock(bvt15)) {
            return false;
        }
        if (integer12 == 10) {
            final float float16 = random.nextFloat();
            if (float16 < 0.25) {
                bxh.setBlockState(a, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                bxh.getBlockTicks().scheduleTick(a, Blocks.MAGMA_BLOCK, 0);
            }
            else {
                bxh.setBlockState(a, Blocks.OBSIDIAN.defaultBlockState(), false);
            }
            return true;
        }
        if (integer12 < 10) {
            bxh.setBlockState(a, Blocks.LAVA.defaultBlockState(), false);
            return false;
        }
        boolean boolean16 = false;
        for (final Direction fb18 : Direction.Plane.HORIZONTAL) {
            final int integer15 = integer9 + fb18.getStepX();
            final int integer16 = integer10 + fb18.getStepZ();
            if (integer15 >> 4 != integer7 || integer16 >> 4 != integer8 || bxh.getBlockState(a.set(integer15, integer12, integer16)).isAir()) {
                bxh.setBlockState(a, UnderwaterCaveWorldCarver.WATER.createLegacyBlock(), false);
                bxh.getLiquidTicks().scheduleTick(a, UnderwaterCaveWorldCarver.WATER.getType(), 0);
                boolean16 = true;
                break;
            }
        }
        a.set(integer9, integer12, integer10);
        if (!boolean16) {
            bxh.setBlockState(a, UnderwaterCaveWorldCarver.WATER.createLegacyBlock(), false);
            return true;
        }
        return true;
    }
}