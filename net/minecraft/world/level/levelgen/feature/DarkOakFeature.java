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

public class DarkOakFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public DarkOakFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2) {
        super(function, boolean2);
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = random.nextInt(3) + random.nextInt(2) + 6;
        final int integer8 = ew.getX();
        final int integer9 = ew.getY();
        final int integer10 = ew.getZ();
        if (integer9 < 1 || integer9 + integer7 + 1 >= 256) {
            return false;
        }
        final BlockPos ew2 = ew.below();
        if (!AbstractTreeFeature.isGrassOrDirt(bhw, ew2)) {
            return false;
        }
        if (!this.canPlaceTreeOfHeight(bhw, ew, integer7)) {
            return false;
        }
        this.setDirtAt(bhw, ew2);
        this.setDirtAt(bhw, ew2.east());
        this.setDirtAt(bhw, ew2.south());
        this.setDirtAt(bhw, ew2.south().east());
        final Direction fb12 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        final int integer11 = integer7 - random.nextInt(4);
        int integer12 = 2 - random.nextInt(3);
        int integer13 = integer8;
        int integer14 = integer10;
        final int integer15 = integer9 + integer7 - 1;
        for (int integer16 = 0; integer16 < integer7; ++integer16) {
            if (integer16 >= integer11 && integer12 > 0) {
                integer13 += fb12.getStepX();
                integer14 += fb12.getStepZ();
                --integer12;
            }
            final int integer17 = integer9 + integer16;
            final BlockPos ew3 = new BlockPos(integer13, integer17, integer14);
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew3)) {
                this.placeLogAt(set, bhw, ew3, cic);
                this.placeLogAt(set, bhw, ew3.east(), cic);
                this.placeLogAt(set, bhw, ew3.south(), cic);
                this.placeLogAt(set, bhw, ew3.east().south(), cic);
            }
        }
        for (int integer16 = -2; integer16 <= 0; ++integer16) {
            for (int integer17 = -2; integer17 <= 0; ++integer17) {
                int integer18 = -1;
                this.placeLeafAt(bhw, integer13 + integer16, integer15 + integer18, integer14 + integer17, cic, set);
                this.placeLeafAt(bhw, 1 + integer13 - integer16, integer15 + integer18, integer14 + integer17, cic, set);
                this.placeLeafAt(bhw, integer13 + integer16, integer15 + integer18, 1 + integer14 - integer17, cic, set);
                this.placeLeafAt(bhw, 1 + integer13 - integer16, integer15 + integer18, 1 + integer14 - integer17, cic, set);
                if (integer16 > -2 || integer17 > -1) {
                    if (integer16 != -1 || integer17 != -2) {
                        integer18 = 1;
                        this.placeLeafAt(bhw, integer13 + integer16, integer15 + integer18, integer14 + integer17, cic, set);
                        this.placeLeafAt(bhw, 1 + integer13 - integer16, integer15 + integer18, integer14 + integer17, cic, set);
                        this.placeLeafAt(bhw, integer13 + integer16, integer15 + integer18, 1 + integer14 - integer17, cic, set);
                        this.placeLeafAt(bhw, 1 + integer13 - integer16, integer15 + integer18, 1 + integer14 - integer17, cic, set);
                    }
                }
            }
        }
        if (random.nextBoolean()) {
            this.placeLeafAt(bhw, integer13, integer15 + 2, integer14, cic, set);
            this.placeLeafAt(bhw, integer13 + 1, integer15 + 2, integer14, cic, set);
            this.placeLeafAt(bhw, integer13 + 1, integer15 + 2, integer14 + 1, cic, set);
            this.placeLeafAt(bhw, integer13, integer15 + 2, integer14 + 1, cic, set);
        }
        for (int integer16 = -3; integer16 <= 4; ++integer16) {
            for (int integer17 = -3; integer17 <= 4; ++integer17) {
                if ((integer16 != -3 || integer17 != -3) && (integer16 != -3 || integer17 != 4) && (integer16 != 4 || integer17 != -3)) {
                    if (integer16 != 4 || integer17 != 4) {
                        if (Math.abs(integer16) < 3 || Math.abs(integer17) < 3) {
                            this.placeLeafAt(bhw, integer13 + integer16, integer15, integer14 + integer17, cic, set);
                        }
                    }
                }
            }
        }
        for (int integer16 = -1; integer16 <= 2; ++integer16) {
            for (int integer17 = -1; integer17 <= 2; ++integer17) {
                if (integer16 < 0 || integer16 > 1 || integer17 < 0 || integer17 > 1) {
                    if (random.nextInt(3) <= 0) {
                        for (int integer18 = random.nextInt(3) + 2, integer19 = 0; integer19 < integer18; ++integer19) {
                            this.placeLogAt(set, bhw, new BlockPos(integer8 + integer16, integer15 - integer19 - 1, integer10 + integer17), cic);
                        }
                        for (int integer19 = -1; integer19 <= 1; ++integer19) {
                            for (int integer20 = -1; integer20 <= 1; ++integer20) {
                                this.placeLeafAt(bhw, integer13 + integer16 + integer19, integer15, integer14 + integer17 + integer20, cic, set);
                            }
                        }
                        for (int integer19 = -2; integer19 <= 2; ++integer19) {
                            for (int integer20 = -2; integer20 <= 2; ++integer20) {
                                if (Math.abs(integer19) != 2 || Math.abs(integer20) != 2) {
                                    this.placeLeafAt(bhw, integer13 + integer16 + integer19, integer15 - 1, integer14 + integer17 + integer20, cic, set);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean canPlaceTreeOfHeight(final LevelSimulatedReader bhx, final BlockPos ew, final int integer) {
        final int integer2 = ew.getX();
        final int integer3 = ew.getY();
        final int integer4 = ew.getZ();
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos();
        for (int integer5 = 0; integer5 <= integer + 1; ++integer5) {
            int integer6 = 1;
            if (integer5 == 0) {
                integer6 = 0;
            }
            if (integer5 >= integer - 1) {
                integer6 = 2;
            }
            for (int integer7 = -integer6; integer7 <= integer6; ++integer7) {
                for (int integer8 = -integer6; integer8 <= integer6; ++integer8) {
                    if (!AbstractTreeFeature.isFree(bhx, a8.set(integer2 + integer7, integer3 + integer5, integer4 + integer8))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private void placeLogAt(final Set<BlockPos> set, final LevelSimulatedRW bhw, final BlockPos ew, final BoundingBox cic) {
        if (AbstractTreeFeature.isFree(bhw, ew)) {
            this.setBlock(set, bhw, ew, DarkOakFeature.LOG, cic);
        }
    }
    
    private void placeLeafAt(final LevelSimulatedRW bhw, final int integer2, final int integer3, final int integer4, final BoundingBox cic, final Set<BlockPos> set) {
        final BlockPos ew8 = new BlockPos(integer2, integer3, integer4);
        if (AbstractTreeFeature.isAir(bhw, ew8)) {
            this.setBlock(set, bhw, ew8, DarkOakFeature.LEAVES, cic);
        }
    }
    
    static {
        LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
        LEAVES = Blocks.DARK_OAK_LEAVES.defaultBlockState();
    }
}
