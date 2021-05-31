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

public class SpruceFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState TRUNK;
    private static final BlockState LEAF;
    
    public SpruceFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2) {
        super(function, boolean2);
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = random.nextInt(4) + 6;
        final int integer8 = 1 + random.nextInt(2);
        final int integer9 = integer7 - integer8;
        final int integer10 = 2 + random.nextInt(2);
        boolean boolean11 = true;
        if (ew.getY() < 1 || ew.getY() + integer7 + 1 > 256) {
            return false;
        }
        for (int integer11 = ew.getY(); integer11 <= ew.getY() + 1 + integer7 && boolean11; ++integer11) {
            int integer12;
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
                        a14.set(integer13, integer11, integer14);
                        if (!AbstractTreeFeature.isAirOrLeaves(bhw, a14)) {
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
        if (!AbstractTreeFeature.isGrassOrDirtOrFarmland(bhw, ew.below()) || ew.getY() >= 256 - integer7 - 1) {
            return false;
        }
        this.setDirtAt(bhw, ew.below());
        int integer11 = random.nextInt(2);
        int integer12 = 1;
        int integer15 = 0;
        for (int integer13 = 0; integer13 <= integer9; ++integer13) {
            final int integer14 = ew.getY() + integer7 - integer13;
            for (int integer16 = ew.getX() - integer11; integer16 <= ew.getX() + integer11; ++integer16) {
                final int integer17 = integer16 - ew.getX();
                for (int integer18 = ew.getZ() - integer11; integer18 <= ew.getZ() + integer11; ++integer18) {
                    final int integer19 = integer18 - ew.getZ();
                    if (Math.abs(integer17) != integer11 || Math.abs(integer19) != integer11 || integer11 <= 0) {
                        final BlockPos ew2 = new BlockPos(integer16, integer14, integer18);
                        if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2) || AbstractTreeFeature.isReplaceablePlant(bhw, ew2)) {
                            this.setBlock(set, bhw, ew2, SpruceFeature.LEAF, cic);
                        }
                    }
                }
            }
            if (integer11 >= integer12) {
                integer11 = integer15;
                integer15 = 1;
                if (++integer12 > integer10) {
                    integer12 = integer10;
                }
            }
            else {
                ++integer11;
            }
        }
        int integer13;
        for (integer13 = random.nextInt(3), int integer14 = 0; integer14 < integer7 - integer13; ++integer14) {
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.above(integer14))) {
                this.setBlock(set, bhw, ew.above(integer14), SpruceFeature.TRUNK, cic);
            }
        }
        return true;
    }
    
    static {
        TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
        LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();
    }
}
