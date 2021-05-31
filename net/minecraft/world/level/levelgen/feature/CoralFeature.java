package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Iterator;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class CoralFeature extends Feature<NoneFeatureConfiguration> {
    public CoralFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final BlockState bvt7 = BlockTags.CORAL_BLOCKS.getRandomElement(random).defaultBlockState();
        return this.placeFeature(bhs, random, ew, bvt7);
    }
    
    protected abstract boolean placeFeature(final LevelAccessor bhs, final Random random, final BlockPos ew, final BlockState bvt);
    
    protected boolean placeCoralBlock(final LevelAccessor bhs, final Random random, final BlockPos ew, final BlockState bvt) {
        final BlockPos ew2 = ew.above();
        final BlockState bvt2 = bhs.getBlockState(ew);
        if ((bvt2.getBlock() != Blocks.WATER && !bvt2.is(BlockTags.CORALS)) || bhs.getBlockState(ew2).getBlock() != Blocks.WATER) {
            return false;
        }
        bhs.setBlock(ew, bvt, 3);
        if (random.nextFloat() < 0.25f) {
            bhs.setBlock(ew2, BlockTags.CORALS.getRandomElement(random).defaultBlockState(), 2);
        }
        else if (random.nextFloat() < 0.05f) {
            bhs.setBlock(ew2, ((AbstractStateHolder<O, BlockState>)Blocks.SEA_PICKLE.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
        }
        for (final Direction fb9 : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.2f) {
                final BlockPos ew3 = ew.relative(fb9);
                if (bhs.getBlockState(ew3).getBlock() != Blocks.WATER) {
                    continue;
                }
                final BlockState bvt3 = ((AbstractStateHolder<O, BlockState>)BlockTags.WALL_CORALS.getRandomElement(random).defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)BaseCoralWallFanBlock.FACING, fb9);
                bhs.setBlock(ew3, bvt3, 2);
            }
        }
        return true;
    }
}
