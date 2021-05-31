package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import java.util.function.Supplier;
import net.minecraft.Util;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import com.google.common.math.DoubleMath;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import java.util.Arrays;
import com.google.common.math.IntMath;
import net.minecraft.world.phys.AABB;

public final class Shapes {
    private static final VoxelShape BLOCK;
    public static final VoxelShape INFINITY;
    private static final VoxelShape EMPTY;
    
    public static VoxelShape empty() {
        return Shapes.EMPTY;
    }
    
    public static VoxelShape block() {
        return Shapes.BLOCK;
    }
    
    public static VoxelShape box(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        return create(new AABB(double1, double2, double3, double4, double5, double6));
    }
    
    public static VoxelShape create(final AABB csc) {
        final int integer2 = findBits(csc.minX, csc.maxX);
        final int integer3 = findBits(csc.minY, csc.maxY);
        final int integer4 = findBits(csc.minZ, csc.maxZ);
        if (integer2 < 0 || integer3 < 0 || integer4 < 0) {
            return new ArrayVoxelShape(Shapes.BLOCK.shape, new double[] { csc.minX, csc.maxX }, new double[] { csc.minY, csc.maxY }, new double[] { csc.minZ, csc.maxZ });
        }
        if (integer2 == 0 && integer3 == 0 && integer4 == 0) {
            return csc.contains(0.5, 0.5, 0.5) ? block() : empty();
        }
        final int integer5 = 1 << integer2;
        final int integer6 = 1 << integer3;
        final int integer7 = 1 << integer4;
        final int integer8 = (int)Math.round(csc.minX * integer5);
        final int integer9 = (int)Math.round(csc.maxX * integer5);
        final int integer10 = (int)Math.round(csc.minY * integer6);
        final int integer11 = (int)Math.round(csc.maxY * integer6);
        final int integer12 = (int)Math.round(csc.minZ * integer7);
        final int integer13 = (int)Math.round(csc.maxZ * integer7);
        final BitSetDiscreteVoxelShape csl14 = new BitSetDiscreteVoxelShape(integer5, integer6, integer7, integer8, integer10, integer12, integer9, integer11, integer13);
        for (long long15 = integer8; long15 < integer9; ++long15) {
            for (long long16 = integer10; long16 < integer11; ++long16) {
                for (long long17 = integer12; long17 < integer13; ++long17) {
                    csl14.setFull((int)long15, (int)long16, (int)long17, false, true);
                }
            }
        }
        return new CubeVoxelShape(csl14);
    }
    
    private static int findBits(final double double1, final double double2) {
        if (double1 < -1.0E-7 || double2 > 1.0000001) {
            return -1;
        }
        for (int integer5 = 0; integer5 <= 3; ++integer5) {
            final double double3 = double1 * (1 << integer5);
            final double double4 = double2 * (1 << integer5);
            final boolean boolean10 = Math.abs(double3 - Math.floor(double3)) < 1.0E-7;
            final boolean boolean11 = Math.abs(double4 - Math.floor(double4)) < 1.0E-7;
            if (boolean10 && boolean11) {
                return integer5;
            }
        }
        return -1;
    }
    
    protected static long lcm(final int integer1, final int integer2) {
        return integer1 * (long)(integer2 / IntMath.gcd(integer1, integer2));
    }
    
    public static VoxelShape or(final VoxelShape ctc1, final VoxelShape ctc2) {
        return join(ctc1, ctc2, BooleanOp.OR);
    }
    
    public static VoxelShape or(final VoxelShape ctc, final VoxelShape... arr) {
        return (VoxelShape)Arrays.stream((Object[])arr).reduce(ctc, Shapes::or);
    }
    
    public static VoxelShape join(final VoxelShape ctc1, final VoxelShape ctc2, final BooleanOp csm) {
        return joinUnoptimized(ctc1, ctc2, csm).optimize();
    }
    
    public static VoxelShape joinUnoptimized(final VoxelShape ctc1, final VoxelShape ctc2, final BooleanOp csm) {
        if (csm.apply(false, false)) {
            throw new IllegalArgumentException();
        }
        if (ctc1 == ctc2) {
            return csm.apply(true, true) ? ctc1 : empty();
        }
        final boolean boolean4 = csm.apply(true, false);
        final boolean boolean5 = csm.apply(false, true);
        if (ctc1.isEmpty()) {
            return boolean5 ? ctc2 : empty();
        }
        if (ctc2.isEmpty()) {
            return boolean4 ? ctc1 : empty();
        }
        final IndexMerger csu6 = createIndexMerger(1, ctc1.getCoords(Direction.Axis.X), ctc2.getCoords(Direction.Axis.X), boolean4, boolean5);
        final IndexMerger csu7 = createIndexMerger(csu6.getList().size() - 1, ctc1.getCoords(Direction.Axis.Y), ctc2.getCoords(Direction.Axis.Y), boolean4, boolean5);
        final IndexMerger csu8 = createIndexMerger((csu6.getList().size() - 1) * (csu7.getList().size() - 1), ctc1.getCoords(Direction.Axis.Z), ctc2.getCoords(Direction.Axis.Z), boolean4, boolean5);
        final BitSetDiscreteVoxelShape csl9 = BitSetDiscreteVoxelShape.join(ctc1.shape, ctc2.shape, csu6, csu7, csu8, csm);
        if (csu6 instanceof DiscreteCubeMerger && csu7 instanceof DiscreteCubeMerger && csu8 instanceof DiscreteCubeMerger) {
            return new CubeVoxelShape(csl9);
        }
        return new ArrayVoxelShape(csl9, csu6.getList(), csu7.getList(), csu8.getList());
    }
    
    public static boolean joinIsNotEmpty(final VoxelShape ctc1, final VoxelShape ctc2, final BooleanOp csm) {
        if (csm.apply(false, false)) {
            throw new IllegalArgumentException();
        }
        if (ctc1 == ctc2) {
            return csm.apply(true, true);
        }
        if (ctc1.isEmpty()) {
            return csm.apply(false, !ctc2.isEmpty());
        }
        if (ctc2.isEmpty()) {
            return csm.apply(!ctc1.isEmpty(), false);
        }
        final boolean boolean4 = csm.apply(true, false);
        final boolean boolean5 = csm.apply(false, true);
        for (final Direction.Axis a9 : AxisCycle.AXIS_VALUES) {
            if (ctc1.max(a9) < ctc2.min(a9) - 1.0E-7) {
                return boolean4 || boolean5;
            }
            if (ctc2.max(a9) < ctc1.min(a9) - 1.0E-7) {
                return boolean4 || boolean5;
            }
        }
        final IndexMerger csu6 = createIndexMerger(1, ctc1.getCoords(Direction.Axis.X), ctc2.getCoords(Direction.Axis.X), boolean4, boolean5);
        final IndexMerger csu7 = createIndexMerger(csu6.getList().size() - 1, ctc1.getCoords(Direction.Axis.Y), ctc2.getCoords(Direction.Axis.Y), boolean4, boolean5);
        final IndexMerger csu8 = createIndexMerger((csu6.getList().size() - 1) * (csu7.getList().size() - 1), ctc1.getCoords(Direction.Axis.Z), ctc2.getCoords(Direction.Axis.Z), boolean4, boolean5);
        return joinIsNotEmpty(csu6, csu7, csu8, ctc1.shape, ctc2.shape, csm);
    }
    
    private static boolean joinIsNotEmpty(final IndexMerger csu1, final IndexMerger csu2, final IndexMerger csu3, final DiscreteVoxelShape csr4, final DiscreteVoxelShape csr5, final BooleanOp csm) {
        return !csu1.forMergedIndexes((integer6, integer7, integer8) -> csu2.forMergedIndexes((integer7, integer8, integer9) -> csu3.forMergedIndexes((integer8, integer9, integer10) -> !csm.apply(csr4.isFullWide(integer6, integer7, integer8), csr5.isFullWide(integer7, integer8, integer9)))));
    }
    
    public static double collide(final Direction.Axis a, final AABB csc, final Stream<VoxelShape> stream, double double4) {
        final Iterator<VoxelShape> iterator6 = (Iterator<VoxelShape>)stream.iterator();
        while (iterator6.hasNext()) {
            if (Math.abs(double4) < 1.0E-7) {
                return 0.0;
            }
            double4 = ((VoxelShape)iterator6.next()).collide(a, csc, double4);
        }
        return double4;
    }
    
    public static double collide(final Direction.Axis a, final AABB csc, final LevelReader bhu, final double double4, final CollisionContext csn, final Stream<VoxelShape> stream) {
        return collide(csc, bhu, double4, csn, AxisCycle.between(a, Direction.Axis.Z), stream);
    }
    
    private static double collide(final AABB csc, final LevelReader bhu, double double3, final CollisionContext csn, final AxisCycle ev, final Stream<VoxelShape> stream) {
        if (csc.getXsize() < 1.0E-6 || csc.getYsize() < 1.0E-6 || csc.getZsize() < 1.0E-6) {
            return double3;
        }
        if (Math.abs(double3) < 1.0E-7) {
            return 0.0;
        }
        final AxisCycle ev2 = ev.inverse();
        final Direction.Axis a9 = ev2.cycle(Direction.Axis.X);
        final Direction.Axis a10 = ev2.cycle(Direction.Axis.Y);
        final Direction.Axis a11 = ev2.cycle(Direction.Axis.Z);
        final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
        final int integer13 = Mth.floor(csc.min(a9) - 1.0E-7) - 1;
        final int integer14 = Mth.floor(csc.max(a9) + 1.0E-7) + 1;
        final int integer15 = Mth.floor(csc.min(a10) - 1.0E-7) - 1;
        final int integer16 = Mth.floor(csc.max(a10) + 1.0E-7) + 1;
        final double double4 = csc.min(a11) - 1.0E-7;
        final double double5 = csc.max(a11) + 1.0E-7;
        final boolean boolean21 = double3 > 0.0;
        final int integer17 = boolean21 ? (Mth.floor(csc.max(a11) - 1.0E-7) - 1) : (Mth.floor(csc.min(a11) + 1.0E-7) + 1);
        int integer18 = lastC(double3, double4, double5);
        final int integer19 = boolean21 ? 1 : -1;
        int integer20 = integer17;
        while (true) {
            if (boolean21) {
                if (integer20 > integer18) {
                    break;
                }
            }
            else if (integer20 < integer18) {
                break;
            }
            for (int integer21 = integer13; integer21 <= integer14; ++integer21) {
                for (int integer22 = integer15; integer22 <= integer16; ++integer22) {
                    int integer23 = 0;
                    if (integer21 == integer13 || integer21 == integer14) {
                        ++integer23;
                    }
                    if (integer22 == integer15 || integer22 == integer16) {
                        ++integer23;
                    }
                    if (integer20 == integer17 || integer20 == integer18) {
                        ++integer23;
                    }
                    if (integer23 < 3) {
                        a12.set(ev2, integer21, integer22, integer20);
                        final BlockState bvt29 = bhu.getBlockState(a12);
                        if (integer23 != 1 || bvt29.hasLargeCollisionShape()) {
                            if (integer23 != 2 || bvt29.getBlock() == Blocks.MOVING_PISTON) {
                                double3 = bvt29.getCollisionShape(bhu, a12, csn).collide(a11, csc.move(-a12.getX(), -a12.getY(), -a12.getZ()), double3);
                                if (Math.abs(double3) < 1.0E-7) {
                                    return 0.0;
                                }
                                integer18 = lastC(double3, double4, double5);
                            }
                        }
                    }
                }
            }
            integer20 += integer19;
        }
        final double[] arr25 = { double3 };
        stream.forEach(ctc -> arr25[0] = ctc.collide(a11, csc, arr25[0]));
        return arr25[0];
    }
    
    private static int lastC(final double double1, final double double2, final double double3) {
        return (double1 > 0.0) ? (Mth.floor(double3 + double1) + 1) : (Mth.floor(double2 + double1) - 1);
    }
    
    public static boolean blockOccudes(final VoxelShape ctc1, final VoxelShape ctc2, final Direction fb) {
        if (ctc1 == block() && ctc2 == block()) {
            return true;
        }
        if (ctc2.isEmpty()) {
            return false;
        }
        final Direction.Axis a4 = fb.getAxis();
        final Direction.AxisDirection b5 = fb.getAxisDirection();
        final VoxelShape ctc3 = (b5 == Direction.AxisDirection.POSITIVE) ? ctc1 : ctc2;
        final VoxelShape ctc4 = (b5 == Direction.AxisDirection.POSITIVE) ? ctc2 : ctc1;
        final BooleanOp csm8 = (b5 == Direction.AxisDirection.POSITIVE) ? BooleanOp.ONLY_FIRST : BooleanOp.ONLY_SECOND;
        return DoubleMath.fuzzyEquals(ctc3.max(a4), 1.0, 1.0E-7) && DoubleMath.fuzzyEquals(ctc4.min(a4), 0.0, 1.0E-7) && !joinIsNotEmpty(new SliceShape(ctc3, a4, ctc3.shape.getSize(a4) - 1), new SliceShape(ctc4, a4, 0), csm8);
    }
    
    public static VoxelShape getFaceShape(final VoxelShape ctc, final Direction fb) {
        if (ctc == block()) {
            return block();
        }
        final Direction.Axis a5 = fb.getAxis();
        boolean boolean3;
        int integer4;
        if (fb.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            boolean3 = DoubleMath.fuzzyEquals(ctc.max(a5), 1.0, 1.0E-7);
            integer4 = ctc.shape.getSize(a5) - 1;
        }
        else {
            boolean3 = DoubleMath.fuzzyEquals(ctc.min(a5), 0.0, 1.0E-7);
            integer4 = 0;
        }
        if (!boolean3) {
            return empty();
        }
        return new SliceShape(ctc, a5, integer4);
    }
    
    public static boolean mergedFaceOccludes(final VoxelShape ctc1, final VoxelShape ctc2, final Direction fb) {
        if (ctc1 == block() || ctc2 == block()) {
            return true;
        }
        final Direction.Axis a4 = fb.getAxis();
        final Direction.AxisDirection b5 = fb.getAxisDirection();
        VoxelShape ctc3 = (b5 == Direction.AxisDirection.POSITIVE) ? ctc1 : ctc2;
        VoxelShape ctc4 = (b5 == Direction.AxisDirection.POSITIVE) ? ctc2 : ctc1;
        if (!DoubleMath.fuzzyEquals(ctc3.max(a4), 1.0, 1.0E-7)) {
            ctc3 = empty();
        }
        if (!DoubleMath.fuzzyEquals(ctc4.min(a4), 0.0, 1.0E-7)) {
            ctc4 = empty();
        }
        return !joinIsNotEmpty(block(), joinUnoptimized(new SliceShape(ctc3, a4, ctc3.shape.getSize(a4) - 1), new SliceShape(ctc4, a4, 0), BooleanOp.OR), BooleanOp.ONLY_FIRST);
    }
    
    public static boolean faceShapeOccludes(final VoxelShape ctc1, final VoxelShape ctc2) {
        return ctc1 == block() || ctc2 == block() || ((!ctc1.isEmpty() || !ctc2.isEmpty()) && !joinIsNotEmpty(block(), joinUnoptimized(ctc1, ctc2, BooleanOp.OR), BooleanOp.ONLY_FIRST));
    }
    
    @VisibleForTesting
    protected static IndexMerger createIndexMerger(final int integer, final DoubleList doubleList2, final DoubleList doubleList3, final boolean boolean4, final boolean boolean5) {
        final int integer2 = doubleList2.size() - 1;
        final int integer3 = doubleList3.size() - 1;
        if (doubleList2 instanceof CubePointRange && doubleList3 instanceof CubePointRange) {
            final long long8 = lcm(integer2, integer3);
            if (integer * long8 <= 256L) {
                return new DiscreteCubeMerger(integer2, integer3);
            }
        }
        if (doubleList2.getDouble(integer2) < doubleList3.getDouble(0) - 1.0E-7) {
            return new NonOverlappingMerger(doubleList2, doubleList3, false);
        }
        if (doubleList3.getDouble(integer3) < doubleList2.getDouble(0) - 1.0E-7) {
            return new NonOverlappingMerger(doubleList3, doubleList2, true);
        }
        if (integer2 != integer3 || !Objects.equals(doubleList2, doubleList3)) {
            return new IndirectMerger(doubleList2, doubleList3, boolean4, boolean5);
        }
        if (doubleList2 instanceof IdenticalMerger) {
            return (IndexMerger)doubleList2;
        }
        if (doubleList3 instanceof IdenticalMerger) {
            return (IndexMerger)doubleList3;
        }
        return new IdenticalMerger(doubleList2);
    }
    
    static {
        BLOCK = Util.<VoxelShape>make((java.util.function.Supplier<VoxelShape>)(() -> {
            final DiscreteVoxelShape csr1 = new BitSetDiscreteVoxelShape(1, 1, 1);
            csr1.setFull(0, 0, 0, true, true);
            return new CubeVoxelShape(csr1);
        }));
        INFINITY = box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        EMPTY = new ArrayVoxelShape(new BitSetDiscreteVoxelShape(0, 0, 0), (DoubleList)new DoubleArrayList(new double[] { 0.0 }), (DoubleList)new DoubleArrayList(new double[] { 0.0 }), (DoubleList)new DoubleArrayList(new double[] { 0.0 }));
    }
    
    public interface DoubleLineConsumer {
        void consume(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6);
    }
}
