package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class BambooFeature extends Feature<ProbabilityFeatureConfiguration> {
    private static final BlockState BAMBOO_TRUNK;
    private static final BlockState BAMBOO_FINAL_LARGE;
    private static final BlockState BAMBOO_TOP_LARGE;
    private static final BlockState BAMBOO_TOP_SMALL;
    
    public BambooFeature(final Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final ProbabilityFeatureConfiguration cdn) {
        int integer7 = 0;
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos(ew);
        final BlockPos.MutableBlockPos a9 = new BlockPos.MutableBlockPos(ew);
        if (bhs.isEmptyBlock(a8)) {
            if (Blocks.BAMBOO.defaultBlockState().canSurvive(bhs, a8)) {
                final int integer8 = random.nextInt(12) + 5;
                if (random.nextFloat() < cdn.probability) {
                    for (int integer9 = random.nextInt(4) + 1, integer10 = ew.getX() - integer9; integer10 <= ew.getX() + integer9; ++integer10) {
                        for (int integer11 = ew.getZ() - integer9; integer11 <= ew.getZ() + integer9; ++integer11) {
                            final int integer12 = integer10 - ew.getX();
                            final int integer13 = integer11 - ew.getZ();
                            if (integer12 * integer12 + integer13 * integer13 <= integer9 * integer9) {
                                a9.set(integer10, bhs.getHeight(Heightmap.Types.WORLD_SURFACE, integer10, integer11) - 1, integer11);
                                if (bhs.getBlockState(a9).getBlock().is(BlockTags.DIRT_LIKE)) {
                                    bhs.setBlock(a9, Blocks.PODZOL.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
                for (int integer9 = 0; integer9 < integer8 && bhs.isEmptyBlock(a8); ++integer9) {
                    bhs.setBlock(a8, BambooFeature.BAMBOO_TRUNK, 2);
                    a8.move(Direction.UP, 1);
                }
                if (a8.getY() - ew.getY() >= 3) {
                    bhs.setBlock(a8, BambooFeature.BAMBOO_FINAL_LARGE, 2);
                    bhs.setBlock(a8.move(Direction.DOWN, 1), BambooFeature.BAMBOO_TOP_LARGE, 2);
                    bhs.setBlock(a8.move(Direction.DOWN, 1), BambooFeature.BAMBOO_TOP_SMALL, 2);
                }
            }
            ++integer7;
        }
        return integer7 > 0;
    }
    
    static {
        BAMBOO_TRUNK = ((((AbstractStateHolder<O, BlockState>)Blocks.BAMBOO.defaultBlockState()).setValue((Property<Comparable>)BambooBlock.AGE, 1)).setValue(BambooBlock.LEAVES, BambooLeaves.NONE)).<Comparable, Integer>setValue((Property<Comparable>)BambooBlock.STAGE, 0);
        BAMBOO_FINAL_LARGE = (((AbstractStateHolder<O, BlockState>)BambooFeature.BAMBOO_TRUNK).setValue(BambooBlock.LEAVES, BambooLeaves.LARGE)).<Comparable, Integer>setValue((Property<Comparable>)BambooBlock.STAGE, 1);
        BAMBOO_TOP_LARGE = ((AbstractStateHolder<O, BlockState>)BambooFeature.BAMBOO_TRUNK).<BambooLeaves, BambooLeaves>setValue(BambooBlock.LEAVES, BambooLeaves.LARGE);
        BAMBOO_TOP_SMALL = ((AbstractStateHolder<O, BlockState>)BambooFeature.BAMBOO_TRUNK).<BambooLeaves, BambooLeaves>setValue(BambooBlock.LEAVES, BambooLeaves.SMALL);
    }
}
