package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Random;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.core.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class SwampTreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState TRUNK;
    private static final BlockState LEAF;
    
    public SwampTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function, false);
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, BlockPos ew, final BoundingBox cic) {
        final int integer7 = random.nextInt(4) + 5;
        ew = bhw.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, ew);
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
                integer9 = 3;
            }
            final BlockPos.MutableBlockPos a11 = new BlockPos.MutableBlockPos();
            for (int integer10 = ew.getX() - integer9; integer10 <= ew.getX() + integer9 && boolean8; ++integer10) {
                for (int integer11 = ew.getZ() - integer9; integer11 <= ew.getZ() + integer9 && boolean8; ++integer11) {
                    if (integer8 >= 0 && integer8 < 256) {
                        a11.set(integer10, integer8, integer11);
                        if (!AbstractTreeFeature.isAirOrLeaves(bhw, a11)) {
                            if (AbstractTreeFeature.isBlockWater(bhw, a11)) {
                                if (integer8 > ew.getY()) {
                                    boolean8 = false;
                                }
                            }
                            else {
                                boolean8 = false;
                            }
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
        for (int integer8 = ew.getY() - 3 + integer7; integer8 <= ew.getY() + integer7; ++integer8) {
            final int integer9 = integer8 - (ew.getY() + integer7);
            for (int integer12 = 2 - integer9 / 2, integer10 = ew.getX() - integer12; integer10 <= ew.getX() + integer12; ++integer10) {
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
                    if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2) || AbstractTreeFeature.isReplaceablePlant(bhw, ew2)) {
                        this.setBlock(set, bhw, ew2, SwampTreeFeature.LEAF, cic);
                    }
                }
            }
        }
        for (int integer8 = 0; integer8 < integer7; ++integer8) {
            final BlockPos ew3 = ew.above(integer8);
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew3) || AbstractTreeFeature.isBlockWater(bhw, ew3)) {
                this.setBlock(set, bhw, ew3, SwampTreeFeature.TRUNK, cic);
            }
        }
        for (int integer8 = ew.getY() - 3 + integer7; integer8 <= ew.getY() + integer7; ++integer8) {
            final int integer9 = integer8 - (ew.getY() + integer7);
            final int integer12 = 2 - integer9 / 2;
            final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
            for (int integer11 = ew.getX() - integer12; integer11 <= ew.getX() + integer12; ++integer11) {
                for (int integer13 = ew.getZ() - integer12; integer13 <= ew.getZ() + integer12; ++integer13) {
                    a12.set(integer11, integer8, integer13);
                    if (AbstractTreeFeature.isLeaves(bhw, a12)) {
                        final BlockPos ew4 = a12.west();
                        final BlockPos ew2 = a12.east();
                        final BlockPos ew5 = a12.north();
                        final BlockPos ew6 = a12.south();
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew4)) {
                            this.addVine(bhw, ew4, VineBlock.EAST);
                        }
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew2)) {
                            this.addVine(bhw, ew2, VineBlock.WEST);
                        }
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew5)) {
                            this.addVine(bhw, ew5, VineBlock.SOUTH);
                        }
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew6)) {
                            this.addVine(bhw, ew6, VineBlock.NORTH);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private void addVine(final LevelSimulatedRW bhw, BlockPos ew, final BooleanProperty bwl) {
        final BlockState bvt5 = ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)bwl, true);
        this.setBlock(bhw, ew, bvt5);
        int integer6;
        for (integer6 = 4, ew = ew.below(); AbstractTreeFeature.isAir(bhw, ew) && integer6 > 0; ew = ew.below(), --integer6) {
            this.setBlock(bhw, ew, bvt5);
        }
    }
    
    static {
        TRUNK = Blocks.OAK_LOG.defaultBlockState();
        LEAF = Blocks.OAK_LEAVES.defaultBlockState();
    }
}
