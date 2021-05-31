package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Random;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class SavannaTreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState TRUNK;
    private static final BlockState LEAF;
    
    public SavannaTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2) {
        super(function, boolean2);
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = random.nextInt(3) + random.nextInt(3) + 5;
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
        if (!AbstractTreeFeature.isGrassOrDirt(bhw, ew.below()) || ew.getY() >= 256 - integer7 - 1) {
            return false;
        }
        this.setDirtAt(bhw, ew.below());
        final Direction fb9 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int integer9 = integer7 - random.nextInt(4) - 1;
        int integer12 = 3 - random.nextInt(3);
        int integer10 = ew.getX();
        int integer11 = ew.getZ();
        int integer13 = 0;
        for (int integer14 = 0; integer14 < integer7; ++integer14) {
            final int integer15 = ew.getY() + integer14;
            if (integer14 >= integer9 && integer12 > 0) {
                integer10 += fb9.getStepX();
                integer11 += fb9.getStepZ();
                --integer12;
            }
            final BlockPos ew2 = new BlockPos(integer10, integer15, integer11);
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                this.placeLogAt(set, bhw, ew2, cic);
                integer13 = integer15;
            }
        }
        BlockPos ew3 = new BlockPos(integer10, integer13, integer11);
        for (int integer15 = -3; integer15 <= 3; ++integer15) {
            for (int integer16 = -3; integer16 <= 3; ++integer16) {
                if (Math.abs(integer15) != 3 || Math.abs(integer16) != 3) {
                    this.placeLeafAt(set, bhw, ew3.offset(integer15, 0, integer16), cic);
                }
            }
        }
        ew3 = ew3.above();
        for (int integer15 = -1; integer15 <= 1; ++integer15) {
            for (int integer16 = -1; integer16 <= 1; ++integer16) {
                this.placeLeafAt(set, bhw, ew3.offset(integer15, 0, integer16), cic);
            }
        }
        this.placeLeafAt(set, bhw, ew3.east(2), cic);
        this.placeLeafAt(set, bhw, ew3.west(2), cic);
        this.placeLeafAt(set, bhw, ew3.south(2), cic);
        this.placeLeafAt(set, bhw, ew3.north(2), cic);
        integer10 = ew.getX();
        integer11 = ew.getZ();
        final Direction fb10 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        if (fb10 != fb9) {
            final int integer15 = integer9 - random.nextInt(2) - 1;
            int integer16 = 1 + random.nextInt(3);
            integer13 = 0;
            for (int integer17 = integer15; integer17 < integer7 && integer16 > 0; ++integer17, --integer16) {
                if (integer17 >= 1) {
                    final int integer18 = ew.getY() + integer17;
                    integer10 += fb10.getStepX();
                    integer11 += fb10.getStepZ();
                    final BlockPos ew4 = new BlockPos(integer10, integer18, integer11);
                    if (AbstractTreeFeature.isAirOrLeaves(bhw, ew4)) {
                        this.placeLogAt(set, bhw, ew4, cic);
                        integer13 = integer18;
                    }
                }
            }
            if (integer13 > 0) {
                BlockPos ew5 = new BlockPos(integer10, integer13, integer11);
                for (int integer18 = -2; integer18 <= 2; ++integer18) {
                    for (int integer19 = -2; integer19 <= 2; ++integer19) {
                        if (Math.abs(integer18) != 2 || Math.abs(integer19) != 2) {
                            this.placeLeafAt(set, bhw, ew5.offset(integer18, 0, integer19), cic);
                        }
                    }
                }
                ew5 = ew5.above();
                for (int integer18 = -1; integer18 <= 1; ++integer18) {
                    for (int integer19 = -1; integer19 <= 1; ++integer19) {
                        this.placeLeafAt(set, bhw, ew5.offset(integer18, 0, integer19), cic);
                    }
                }
            }
        }
        return true;
    }
    
    private void placeLogAt(final Set<BlockPos> set, final LevelWriter bhz, final BlockPos ew, final BoundingBox cic) {
        this.setBlock(set, bhz, ew, SavannaTreeFeature.TRUNK, cic);
    }
    
    private void placeLeafAt(final Set<BlockPos> set, final LevelSimulatedRW bhw, final BlockPos ew, final BoundingBox cic) {
        if (AbstractTreeFeature.isAirOrLeaves(bhw, ew)) {
            this.setBlock(set, bhw, ew, SavannaTreeFeature.LEAF, cic);
        }
    }
    
    static {
        TRUNK = Blocks.ACACIA_LOG.defaultBlockState();
        LEAF = Blocks.ACACIA_LEAVES.defaultBlockState();
    }
}
