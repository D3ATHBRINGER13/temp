package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HugeRedMushroomFeature extends Feature<HugeMushroomFeatureConfig> {
    public HugeRedMushroomFeature(final Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final HugeMushroomFeatureConfig ccd) {
        int integer7 = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            integer7 *= 2;
        }
        final int integer8 = ew.getY();
        if (integer8 < 1 || integer8 + integer7 + 1 >= 256) {
            return false;
        }
        final Block bmv9 = bhs.getBlockState(ew.below()).getBlock();
        if (!Block.equalsDirt(bmv9) && bmv9 != Blocks.GRASS_BLOCK && bmv9 != Blocks.MYCELIUM) {
            return false;
        }
        final BlockPos.MutableBlockPos a10 = new BlockPos.MutableBlockPos();
        for (int integer9 = 0; integer9 <= integer7; ++integer9) {
            int integer10 = 0;
            if (integer9 < integer7 && integer9 >= integer7 - 3) {
                integer10 = 2;
            }
            else if (integer9 == integer7) {
                integer10 = 1;
            }
            for (int integer11 = -integer10; integer11 <= integer10; ++integer11) {
                for (int integer12 = -integer10; integer12 <= integer10; ++integer12) {
                    final BlockState bvt15 = bhs.getBlockState(a10.set(ew).move(integer11, integer9, integer12));
                    if (!bvt15.isAir() && !bvt15.is(BlockTags.LEAVES)) {
                        return false;
                    }
                }
            }
        }
        final BlockState bvt16 = ((AbstractStateHolder<O, BlockState>)Blocks.RED_MUSHROOM_BLOCK.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)HugeMushroomBlock.DOWN, false);
        for (int integer10 = integer7 - 3; integer10 <= integer7; ++integer10) {
            final int integer11 = (integer10 < integer7) ? 2 : 1;
            final int integer12 = 0;
            for (int integer13 = -integer11; integer13 <= integer11; ++integer13) {
                for (int integer14 = -integer11; integer14 <= integer11; ++integer14) {
                    final boolean boolean17 = integer13 == -integer11;
                    final boolean boolean18 = integer13 == integer11;
                    final boolean boolean19 = integer14 == -integer11;
                    final boolean boolean20 = integer14 == integer11;
                    final boolean boolean21 = boolean17 || boolean18;
                    final boolean boolean22 = boolean19 || boolean20;
                    if (integer10 >= integer7 || boolean21 != boolean22) {
                        a10.set(ew).move(integer13, integer10, integer14);
                        if (!bhs.getBlockState(a10).isSolidRender(bhs, a10)) {
                            this.setBlock(bhs, a10, ((((((AbstractStateHolder<O, BlockState>)bvt16).setValue((Property<Comparable>)HugeMushroomBlock.UP, integer10 >= integer7 - 1)).setValue((Property<Comparable>)HugeMushroomBlock.WEST, integer13 < 0)).setValue((Property<Comparable>)HugeMushroomBlock.EAST, integer13 > 0)).setValue((Property<Comparable>)HugeMushroomBlock.NORTH, integer14 < 0)).<Comparable, Boolean>setValue((Property<Comparable>)HugeMushroomBlock.SOUTH, integer14 > 0));
                        }
                    }
                }
            }
        }
        final BlockState bvt17 = (((AbstractStateHolder<O, BlockState>)Blocks.MUSHROOM_STEM.defaultBlockState()).setValue((Property<Comparable>)HugeMushroomBlock.UP, false)).<Comparable, Boolean>setValue((Property<Comparable>)HugeMushroomBlock.DOWN, false);
        for (int integer11 = 0; integer11 < integer7; ++integer11) {
            a10.set(ew).move(Direction.UP, integer11);
            if (!bhs.getBlockState(a10).isSolidRender(bhs, a10)) {
                if (ccd.planted) {
                    bhs.setBlock(a10, bvt17, 3);
                }
                else {
                    this.setBlock(bhs, a10, bvt17);
                }
            }
        }
        return true;
    }
}
