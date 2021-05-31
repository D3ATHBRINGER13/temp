package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Random;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class PineFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState TRUNK;
    private static final BlockState LEAF;
    
    public PineFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function, false);
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = random.nextInt(5) + 7;
        final int integer8 = integer7 - random.nextInt(2) - 3;
        final int integer9 = integer7 - integer8;
        final int integer10 = 1 + random.nextInt(integer9 + 1);
        if (ew.getY() < 1 || ew.getY() + integer7 + 1 > 256) {
            return false;
        }
        boolean boolean11 = true;
        for (int integer11 = ew.getY(); integer11 <= ew.getY() + 1 + integer7 && boolean11; ++integer11) {
            int integer12 = 1;
            if (integer11 - ew.getY() < integer8) {
                integer12 = 0;
            }
            else {
                integer12 = integer10;
            }
            final BlockPos.MutableBlockPos a14 = new BlockPos.MutableBlockPos();
            for (int integer13 = ew.getX() - integer12; integer13 <= ew.getX() + integer12 && boolean11; ++integer13) {
                for (int integer14 = ew.getZ() - integer12; integer14 <= ew.getZ() + integer12 && boolean11; ++integer14) {
                    if (integer11 >= 0 && integer11 < 256) {
                        if (!AbstractTreeFeature.isFree(bhw, a14.set(integer13, integer11, integer14))) {
                            boolean11 = false;
                        }
                    }
                    else {
                        boolean11 = false;
                    }
                }
            }
        }
        if (!boolean11) {
            return false;
        }
        if (!AbstractTreeFeature.isGrassOrDirt(bhw, ew.below()) || ew.getY() >= 256 - integer7 - 1) {
            return false;
        }
        this.setDirtAt(bhw, ew.below());
        int integer11 = 0;
        for (int integer12 = ew.getY() + integer7; integer12 >= ew.getY() + integer8; --integer12) {
            for (int integer15 = ew.getX() - integer11; integer15 <= ew.getX() + integer11; ++integer15) {
                final int integer13 = integer15 - ew.getX();
                for (int integer14 = ew.getZ() - integer11; integer14 <= ew.getZ() + integer11; ++integer14) {
                    final int integer16 = integer14 - ew.getZ();
                    if (Math.abs(integer13) != integer11 || Math.abs(integer16) != integer11 || integer11 <= 0) {
                        final BlockPos ew2 = new BlockPos(integer15, integer12, integer14);
                        if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                            this.setBlock(set, bhw, ew2, PineFeature.LEAF, cic);
                        }
                    }
                }
            }
            if (integer11 >= 1 && integer12 == ew.getY() + integer8 + 1) {
                --integer11;
            }
            else if (integer11 < integer10) {
                ++integer11;
            }
        }
        for (int integer12 = 0; integer12 < integer7 - 1; ++integer12) {
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.above(integer12))) {
                this.setBlock(set, bhw, ew.above(integer12), PineFeature.TRUNK, cic);
            }
        }
        return true;
    }
    
    static {
        TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
        LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();
    }
}
