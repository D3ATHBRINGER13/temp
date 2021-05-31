package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.Blocks;
import com.google.common.collect.Lists;
import java.util.Random;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LogBlock;
import java.util.Objects;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.LevelSimulatedReader;
import java.util.Set;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class BigTreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public BigTreeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final boolean boolean2) {
        super(function, boolean2);
    }
    
    private void crossSection(final LevelSimulatedRW bhw, final BlockPos ew, final float float3, final BoundingBox cic, final Set<BlockPos> set) {
        for (int integer7 = (int)(float3 + 0.618), integer8 = -integer7; integer8 <= integer7; ++integer8) {
            for (int integer9 = -integer7; integer9 <= integer7; ++integer9) {
                if (Math.pow(Math.abs(integer8) + 0.5, 2.0) + Math.pow(Math.abs(integer9) + 0.5, 2.0) <= float3 * float3) {
                    final BlockPos ew2 = ew.offset(integer8, 0, integer9);
                    if (AbstractTreeFeature.isAirOrLeaves(bhw, ew2)) {
                        this.setBlock(set, bhw, ew2, BigTreeFeature.LEAVES, cic);
                    }
                }
            }
        }
    }
    
    private float treeShape(final int integer1, final int integer2) {
        if (integer2 < integer1 * 0.3f) {
            return -1.0f;
        }
        final float float4 = integer1 / 2.0f;
        final float float5 = float4 - integer2;
        float float6 = Mth.sqrt(float4 * float4 - float5 * float5);
        if (float5 == 0.0f) {
            float6 = float4;
        }
        else if (Math.abs(float5) >= float4) {
            return 0.0f;
        }
        return float6 * 0.5f;
    }
    
    private float foliageShape(final int integer) {
        if (integer < 0 || integer >= 5) {
            return -1.0f;
        }
        if (integer == 0 || integer == 4) {
            return 2.0f;
        }
        return 3.0f;
    }
    
    private void foliageCluster(final LevelSimulatedRW bhw, final BlockPos ew, final BoundingBox cic, final Set<BlockPos> set) {
        for (int integer6 = 0; integer6 < 5; ++integer6) {
            this.crossSection(bhw, ew.above(integer6), this.foliageShape(integer6), cic, set);
        }
    }
    
    private int makeLimb(final Set<BlockPos> set, final LevelSimulatedRW bhw, final BlockPos ew3, final BlockPos ew4, final boolean boolean5, final BoundingBox cic) {
        if (!boolean5 && Objects.equals(ew3, ew4)) {
            return -1;
        }
        final BlockPos ew5 = ew4.offset(-ew3.getX(), -ew3.getY(), -ew3.getZ());
        final int integer9 = this.getSteps(ew5);
        final float float10 = ew5.getX() / (float)integer9;
        final float float11 = ew5.getY() / (float)integer9;
        final float float12 = ew5.getZ() / (float)integer9;
        for (int integer10 = 0; integer10 <= integer9; ++integer10) {
            final BlockPos ew6 = ew3.offset(0.5f + integer10 * float10, 0.5f + integer10 * float11, 0.5f + integer10 * float12);
            if (boolean5) {
                this.setBlock(set, bhw, ew6, ((AbstractStateHolder<O, BlockState>)BigTreeFeature.LOG).<Direction.Axis, Direction.Axis>setValue(LogBlock.AXIS, this.getLogAxis(ew3, ew6)), cic);
            }
            else if (!AbstractTreeFeature.isFree(bhw, ew6)) {
                return integer10;
            }
        }
        return -1;
    }
    
    private int getSteps(final BlockPos ew) {
        final int integer3 = Mth.abs(ew.getX());
        final int integer4 = Mth.abs(ew.getY());
        final int integer5 = Mth.abs(ew.getZ());
        if (integer5 > integer3 && integer5 > integer4) {
            return integer5;
        }
        if (integer4 > integer3) {
            return integer4;
        }
        return integer3;
    }
    
    private Direction.Axis getLogAxis(final BlockPos ew1, final BlockPos ew2) {
        Direction.Axis a4 = Direction.Axis.Y;
        final int integer5 = Math.abs(ew2.getX() - ew1.getX());
        final int integer6 = Math.abs(ew2.getZ() - ew1.getZ());
        final int integer7 = Math.max(integer5, integer6);
        if (integer7 > 0) {
            if (integer5 == integer7) {
                a4 = Direction.Axis.X;
            }
            else if (integer6 == integer7) {
                a4 = Direction.Axis.Z;
            }
        }
        return a4;
    }
    
    private void makeFoliage(final LevelSimulatedRW bhw, final int integer, final BlockPos ew, final List<FoliageCoords> list, final BoundingBox cic, final Set<BlockPos> set) {
        for (final FoliageCoords a9 : list) {
            if (this.trimBranches(integer, a9.getBranchBase() - ew.getY())) {
                this.foliageCluster(bhw, a9, cic, set);
            }
        }
    }
    
    private boolean trimBranches(final int integer1, final int integer2) {
        return integer2 >= integer1 * 0.2;
    }
    
    private void makeTrunk(final Set<BlockPos> set, final LevelSimulatedRW bhw, final BlockPos ew, final int integer, final BoundingBox cic) {
        this.makeLimb(set, bhw, ew, ew.above(integer), true, cic);
    }
    
    private void makeBranches(final Set<BlockPos> set, final LevelSimulatedRW bhw, final int integer, final BlockPos ew, final List<FoliageCoords> list, final BoundingBox cic) {
        for (final FoliageCoords a9 : list) {
            final int integer2 = a9.getBranchBase();
            final BlockPos ew2 = new BlockPos(ew.getX(), integer2, ew.getZ());
            if (!ew2.equals(a9) && this.trimBranches(integer, integer2 - ew.getY())) {
                this.makeLimb(set, bhw, ew2, a9, true, cic);
            }
        }
    }
    
    public boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic) {
        final Random random2 = new Random(random.nextLong());
        final int integer8 = this.checkLocation(set, bhw, ew, 5 + random2.nextInt(12), cic);
        if (integer8 == -1) {
            return false;
        }
        this.setDirtAt(bhw, ew.below());
        int integer9 = (int)(integer8 * 0.618);
        if (integer9 >= integer8) {
            integer9 = integer8 - 1;
        }
        final double double10 = 1.0;
        int integer10 = (int)(1.382 + Math.pow(1.0 * integer8 / 13.0, 2.0));
        if (integer10 < 1) {
            integer10 = 1;
        }
        final int integer11 = ew.getY() + integer9;
        int integer12 = integer8 - 5;
        final List<FoliageCoords> list15 = (List<FoliageCoords>)Lists.newArrayList();
        list15.add(new FoliageCoords(ew.above(integer12), integer11));
        while (integer12 >= 0) {
            final float float16 = this.treeShape(integer8, integer12);
            if (float16 >= 0.0f) {
                for (int integer13 = 0; integer13 < integer10; ++integer13) {
                    final double double11 = 1.0;
                    final double double12 = 1.0 * float16 * (random2.nextFloat() + 0.328);
                    final double double13 = random2.nextFloat() * 2.0f * 3.141592653589793;
                    final double double14 = double12 * Math.sin(double13) + 0.5;
                    final double double15 = double12 * Math.cos(double13) + 0.5;
                    final BlockPos ew2 = ew.offset(double14, integer12 - 1, double15);
                    final BlockPos ew3 = ew2.above(5);
                    if (this.makeLimb(set, bhw, ew2, ew3, false, cic) == -1) {
                        final int integer14 = ew.getX() - ew2.getX();
                        final int integer15 = ew.getZ() - ew2.getZ();
                        final double double16 = ew2.getY() - Math.sqrt((double)(integer14 * integer14 + integer15 * integer15)) * 0.381;
                        final int integer16 = (double16 > integer11) ? integer11 : ((int)double16);
                        final BlockPos ew4 = new BlockPos(ew.getX(), integer16, ew.getZ());
                        if (this.makeLimb(set, bhw, ew4, ew2, false, cic) == -1) {
                            list15.add(new FoliageCoords(ew2, ew4.getY()));
                        }
                    }
                }
            }
            --integer12;
        }
        this.makeFoliage(bhw, integer8, ew, list15, cic, set);
        this.makeTrunk(set, bhw, ew, integer9, cic);
        this.makeBranches(set, bhw, integer8, ew, list15, cic);
        return true;
    }
    
    private int checkLocation(final Set<BlockPos> set, final LevelSimulatedRW bhw, final BlockPos ew, final int integer, final BoundingBox cic) {
        if (!AbstractTreeFeature.isGrassOrDirtOrFarmland(bhw, ew.below())) {
            return -1;
        }
        final int integer2 = this.makeLimb(set, bhw, ew, ew.above(integer - 1), false, cic);
        if (integer2 == -1) {
            return integer;
        }
        if (integer2 < 6) {
            return -1;
        }
        return integer2;
    }
    
    static {
        LOG = Blocks.OAK_LOG.defaultBlockState();
        LEAVES = Blocks.OAK_LEAVES.defaultBlockState();
    }
    
    static class FoliageCoords extends BlockPos {
        private final int branchBase;
        
        public FoliageCoords(final BlockPos ew, final int integer) {
            super(ew.getX(), ew.getY(), ew.getZ());
            this.branchBase = integer;
        }
        
        public int getBranchBase() {
            return this.branchBase;
        }
    }
}
