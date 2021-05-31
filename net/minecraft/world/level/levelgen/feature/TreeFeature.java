package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.Blocks;
import java.util.Iterator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.VineBlock;
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

public class TreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState DEFAULT_TRUNK;
    private static final BlockState DEFAULT_LEAF;
    protected final int baseHeight;
    private final boolean addJungleFeatures;
    private final BlockState trunk;
    private final BlockState leaf;
    
    public TreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2) {
        this(function, boolean2, 4, TreeFeature.DEFAULT_TRUNK, TreeFeature.DEFAULT_LEAF, false);
    }
    
    public TreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2, final int integer, final BlockState bvt4, final BlockState bvt5, final boolean boolean6) {
        super(function, boolean2);
        this.baseHeight = integer;
        this.trunk = bvt4;
        this.leaf = bvt5;
        this.addJungleFeatures = boolean6;
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final int integer7 = this.getTreeHeight(random);
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
        int integer8 = 3;
        int integer9 = 0;
        for (int integer12 = ew.getY() - 3 + integer7; integer12 <= ew.getY() + integer7; ++integer12) {
            final int integer10 = integer12 - (ew.getY() + integer7);
            for (int integer11 = 1 - integer10 / 2, integer13 = ew.getX() - integer11; integer13 <= ew.getX() + integer11; ++integer13) {
                final int integer14 = integer13 - ew.getX();
                for (int integer15 = ew.getZ() - integer11; integer15 <= ew.getZ() + integer11; ++integer15) {
                    final int integer16 = integer15 - ew.getZ();
                    if (Math.abs(integer14) == integer11 && Math.abs(integer16) == integer11) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (integer10 == 0) {
                            continue;
                        }
                    }
                    final BlockPos ew2 = new BlockPos(integer13, integer12, integer15);
                    if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2) || AbstractTreeFeature.isReplaceablePlant(bhw, ew2)) {
                        this.setBlock(set, bhw, ew2, this.leaf, cic);
                    }
                }
            }
        }
        for (int integer12 = 0; integer12 < integer7; ++integer12) {
            if (AbstractTreeFeature.isAirOrLeaves(bhw, ew.above(integer12)) || AbstractTreeFeature.isReplaceablePlant(bhw, ew.above(integer12))) {
                this.setBlock(set, bhw, ew.above(integer12), this.trunk, cic);
                if (this.addJungleFeatures && integer12 > 0) {
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(bhw, ew.offset(-1, integer12, 0))) {
                        this.addVine(bhw, ew.offset(-1, integer12, 0), VineBlock.EAST);
                    }
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(bhw, ew.offset(1, integer12, 0))) {
                        this.addVine(bhw, ew.offset(1, integer12, 0), VineBlock.WEST);
                    }
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(bhw, ew.offset(0, integer12, -1))) {
                        this.addVine(bhw, ew.offset(0, integer12, -1), VineBlock.SOUTH);
                    }
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(bhw, ew.offset(0, integer12, 1))) {
                        this.addVine(bhw, ew.offset(0, integer12, 1), VineBlock.NORTH);
                    }
                }
            }
        }
        if (this.addJungleFeatures) {
            for (int integer12 = ew.getY() - 3 + integer7; integer12 <= ew.getY() + integer7; ++integer12) {
                final int integer10 = integer12 - (ew.getY() + integer7);
                final int integer11 = 2 - integer10 / 2;
                final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
                for (int integer14 = ew.getX() - integer11; integer14 <= ew.getX() + integer11; ++integer14) {
                    for (int integer15 = ew.getZ() - integer11; integer15 <= ew.getZ() + integer11; ++integer15) {
                        a12.set(integer14, integer12, integer15);
                        if (AbstractTreeFeature.isLeaves(bhw, a12)) {
                            final BlockPos ew3 = a12.west();
                            final BlockPos ew2 = a12.east();
                            final BlockPos ew4 = a12.north();
                            final BlockPos ew5 = a12.south();
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew3)) {
                                this.addHangingVine(bhw, ew3, VineBlock.EAST);
                            }
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew2)) {
                                this.addHangingVine(bhw, ew2, VineBlock.WEST);
                            }
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew4)) {
                                this.addHangingVine(bhw, ew4, VineBlock.SOUTH);
                            }
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(bhw, ew5)) {
                                this.addHangingVine(bhw, ew5, VineBlock.NORTH);
                            }
                        }
                    }
                }
            }
            if (random.nextInt(5) == 0 && integer7 > 5) {
                for (int integer12 = 0; integer12 < 2; ++integer12) {
                    for (final Direction fb13 : Direction.Plane.HORIZONTAL) {
                        if (random.nextInt(4 - integer12) == 0) {
                            final Direction fb14 = fb13.getOpposite();
                            this.placeCocoa(bhw, random.nextInt(3), ew.offset(fb14.getStepX(), integer7 - 5 + integer12, fb14.getStepZ()), fb13);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    protected int getTreeHeight(final Random random) {
        return this.baseHeight + random.nextInt(3);
    }
    
    private void placeCocoa(final LevelWriter bhz, final int integer, final BlockPos ew, final Direction fb) {
        this.setBlock(bhz, ew, (((AbstractStateHolder<O, BlockState>)Blocks.COCOA.defaultBlockState()).setValue((Property<Comparable>)CocoaBlock.AGE, integer)).<Comparable, Direction>setValue((Property<Comparable>)CocoaBlock.FACING, fb));
    }
    
    private void addVine(final LevelWriter bhz, final BlockPos ew, final BooleanProperty bwl) {
        this.setBlock(bhz, ew, ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)bwl, true));
    }
    
    private void addHangingVine(final LevelSimulatedRW bhw, BlockPos ew, final BooleanProperty bwl) {
        this.addVine(bhw, ew, bwl);
        int integer5;
        for (integer5 = 4, ew = ew.below(); AbstractTreeFeature.isAir(bhw, ew) && integer5 > 0; ew = ew.below(), --integer5) {
            this.addVine(bhw, ew, bwl);
        }
    }
    
    static {
        DEFAULT_TRUNK = Blocks.OAK_LOG.defaultBlockState();
        DEFAULT_LEAF = Blocks.OAK_LEAVES.defaultBlockState();
    }
}
