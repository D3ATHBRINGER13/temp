package net.minecraft.world.level.levelgen.carver;

import net.minecraft.world.level.material.Fluid;
import java.util.Set;
import net.minecraft.world.level.block.state.BlockState;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import java.util.BitSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import net.minecraft.world.level.material.Fluids;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HellCaveWorldCarver extends CaveWorldCarver {
    public HellCaveWorldCarver(final Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> function) {
        super(function, 128);
        this.replaceableBlocks = (Set<Block>)ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, (Object[])new Block[] { Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK });
        this.liquids = (Set<Fluid>)ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
    }
    
    @Override
    protected int getCaveBound() {
        return 10;
    }
    
    @Override
    protected float getThickness(final Random random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }
    
    @Override
    protected double getYScale() {
        return 5.0;
    }
    
    @Override
    protected int getCaveY(final Random random) {
        return random.nextInt(this.genHeight);
    }
    
    @Override
    protected boolean carveBlock(final ChunkAccess bxh, final BitSet bitSet, final Random random, final BlockPos.MutableBlockPos a4, final BlockPos.MutableBlockPos a5, final BlockPos.MutableBlockPos a6, final int integer7, final int integer8, final int integer9, final int integer10, final int integer11, final int integer12, final int integer13, final int integer14, final AtomicBoolean atomicBoolean) {
        final int integer15 = integer12 | integer14 << 4 | integer13 << 8;
        if (bitSet.get(integer15)) {
            return false;
        }
        bitSet.set(integer15);
        a4.set(integer10, integer13, integer11);
        if (this.canReplaceBlock(bxh.getBlockState(a4))) {
            BlockState bvt18;
            if (integer13 <= 31) {
                bvt18 = HellCaveWorldCarver.LAVA.createLegacyBlock();
            }
            else {
                bvt18 = HellCaveWorldCarver.CAVE_AIR;
            }
            bxh.setBlockState(a4, bvt18, false);
            return true;
        }
        return false;
    }
}
