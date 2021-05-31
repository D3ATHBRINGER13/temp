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

public class BirchFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState LOG;
    private static final BlockState LEAF;
    private final boolean superBirch;
    
    public BirchFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2, final boolean boolean3) {
        super(function, boolean2);
        this.superBirch = boolean3;
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        int integer7 = random.nextInt(3) + 5;
        if (this.superBirch) {
            integer7 += random.nextInt(7);
        }
        boolean boolean8 = true;
        if (ew.getY() < 1 || ew.getY() + integer7 + 1 > 256) {
            return false;
        }
        for (int integer8 = ew.getY(); integer8 <= ew.getY() + 1 + integer7; ++integer8) {
            int integer9 = 1;
            if (integer8 == ew.getY()) {
                integer9 = 0;
            }
            if (integer8 >= ew.getY() + 1 + integer7 - 2) {
                integer9 = 2;
            }
            final BlockPos.MutableBlockPos a11 = new BlockPos.MutableBlockPos();
            for (int integer10 = ew.getX() - integer9; integer10 <= ew.getX() + integer9 && boolean8; ++integer10) {
                for (int integer11 = ew.getZ() - integer9; integer11 <= ew.getZ() + integer9 && boolean8; ++integer11) {
                    if (integer8 >= 0 && integer8 < 256) {
                        if (!AbstractTreeFeature.isFree(bhw, a11.set(integer10, integer8, integer11))) {
                            boolean8 = false;
                        }
                    }
                    else {
                        boolean8 = false;
                    }
                }
            }
        }
        if (!boolean8) {
            return false;
        }
        if (!AbstractTreeFeature.isGrassOrDirtOrFarmland(bhw, ew.below()) || ew.getY() >= 256 - integer7 - 1) {
            return false;
        }
        this.setDirtAt(bhw, ew.below());
        for (int integer8 = ew.getY() - 3 + integer7; integer8 <= ew.getY() + integer7; ++integer8) {
            final int integer9 = integer8 - (ew.getY() + integer7);
            for (int integer12 = 1 - integer9 / 2, integer10 = ew.getX() - integer12; integer10 <= ew.getX() + integer12; ++integer10) {
                final int integer11 = integer10 - ew.getX();
                for (int integer13 = ew.getZ() - integer12; integer13 <= ew.getZ() + integer12; ++integer13) {
                    final int integer14 = integer13 - ew.getZ();
                    if (Math.abs(integer11) == integer12 && Math.abs(integer14) == integer12) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (integer9 == 0) {
                            continue;
                        }
                    }
                    final BlockPos ew2 = new BlockPos(integer10, integer8, integer13);
                    if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                        this.setBlock(set, bhw, ew2, BirchFeature.LEAF, cic);
                    }
                }
            }
        }
        for (int integer8 = 0; integer8 < integer7; ++integer8) {
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.above(integer8))) {
                this.setBlock(set, bhw, ew.above(integer8), BirchFeature.LOG, cic);
            }
        }
        return true;
    }
    
    static {
        LOG = Blocks.BIRCH_LOG.defaultBlockState();
        LEAF = Blocks.BIRCH_LEAVES.defaultBlockState();
    }
}
