package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Random;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import java.util.Set;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MegaJungleTreeFeature extends MegaTreeFeature<NoneFeatureConfiguration> {
    public MegaJungleTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2, final int integer3, final int integer4, final BlockState bvt5, final BlockState bvt6) {
        super(function, boolean2, integer3, integer4, bvt5, bvt6);
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = this.calcTreeHeigth(random);
        if (!this.prepareTree(bhw, ew, integer7)) {
            return false;
        }
        this.createCrown(bhw, ew.above(integer7), 2, cic, set);
        for (int integer8 = ew.getY() + integer7 - 2 - random.nextInt(4); integer8 > ew.getY() + integer7 / 2; integer8 -= 2 + random.nextInt(4)) {
            final float float9 = random.nextFloat() * 6.2831855f;
            int integer9 = ew.getX() + (int)(0.5f + Mth.cos(float9) * 4.0f);
            int integer10 = ew.getZ() + (int)(0.5f + Mth.sin(float9) * 4.0f);
            for (int integer11 = 0; integer11 < 5; ++integer11) {
                integer9 = ew.getX() + (int)(1.5f + Mth.cos(float9) * integer11);
                integer10 = ew.getZ() + (int)(1.5f + Mth.sin(float9) * integer11);
                this.setBlock(set, bhw, new BlockPos(integer9, integer8 - 3 + integer11 / 2, integer10), this.trunk, cic);
            }
            int integer11 = 1 + random.nextInt(2);
            for (int integer12 = integer8, integer13 = integer12 - integer11; integer13 <= integer12; ++integer13) {
                final int integer14 = integer13 - integer12;
                this.placeSingleTrunkLeaves(bhw, new BlockPos(integer9, integer13, integer10), 1 - integer14, cic, set);
            }
        }
        for (int integer15 = 0; integer15 < integer7; ++integer15) {
            final BlockPos ew2 = ew.above(integer15);
            if (AbstractTreeFeature.isFree(bhw, ew2)) {
                this.setBlock(set, bhw, ew2, this.trunk, cic);
                if (integer15 > 0) {
                    this.placeVine(bhw, random, ew2.west(), VineBlock.EAST);
                    this.placeVine(bhw, random, ew2.north(), VineBlock.SOUTH);
                }
            }
            if (integer15 < integer7 - 1) {
                final BlockPos ew3 = ew2.east();
                if (AbstractTreeFeature.isFree(bhw, ew3)) {
                    this.setBlock(set, bhw, ew3, this.trunk, cic);
                    if (integer15 > 0) {
                        this.placeVine(bhw, random, ew3.east(), VineBlock.WEST);
                        this.placeVine(bhw, random, ew3.north(), VineBlock.SOUTH);
                    }
                }
                final BlockPos ew4 = ew2.south().east();
                if (AbstractTreeFeature.isFree(bhw, ew4)) {
                    this.setBlock(set, bhw, ew4, this.trunk, cic);
                    if (integer15 > 0) {
                        this.placeVine(bhw, random, ew4.east(), VineBlock.WEST);
                        this.placeVine(bhw, random, ew4.south(), VineBlock.NORTH);
                    }
                }
                final BlockPos ew5 = ew2.south();
                if (AbstractTreeFeature.isFree(bhw, ew5)) {
                    this.setBlock(set, bhw, ew5, this.trunk, cic);
                    if (integer15 > 0) {
                        this.placeVine(bhw, random, ew5.west(), VineBlock.EAST);
                        this.placeVine(bhw, random, ew5.south(), VineBlock.NORTH);
                    }
                }
            }
        }
        return true;
    }
    
    private void placeVine(final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BooleanProperty bwl) {
        if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(bhw, ew)) {
            this.setBlock(bhw, ew, ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)bwl, true));
        }
    }
    
    private void createCrown(final LevelSimulatedRW bhw, final BlockPos ew, final int integer, final BoundingBox cic, final Set<BlockPos> set) {
        final int integer2 = 2;
        for (int integer3 = -2; integer3 <= 0; ++integer3) {
            this.placeDoubleTrunkLeaves(bhw, ew.above(integer3), integer + 1 - integer3, cic, set);
        }
    }
}
