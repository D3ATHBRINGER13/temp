package net.minecraft.world.phys;

import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.BlockPos;

public class AABB {
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;
    
    public AABB(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        this.minX = Math.min(double1, double4);
        this.minY = Math.min(double2, double5);
        this.minZ = Math.min(double3, double6);
        this.maxX = Math.max(double1, double4);
        this.maxY = Math.max(double2, double5);
        this.maxZ = Math.max(double3, double6);
    }
    
    public AABB(final BlockPos ew) {
        this(ew.getX(), ew.getY(), ew.getZ(), ew.getX() + 1, ew.getY() + 1, ew.getZ() + 1);
    }
    
    public AABB(final BlockPos ew1, final BlockPos ew2) {
        this(ew1.getX(), ew1.getY(), ew1.getZ(), ew2.getX(), ew2.getY(), ew2.getZ());
    }
    
    public AABB(final Vec3 csi1, final Vec3 csi2) {
        this(csi1.x, csi1.y, csi1.z, csi2.x, csi2.y, csi2.z);
    }
    
    public static AABB of(final BoundingBox cic) {
        return new AABB(cic.x0, cic.y0, cic.z0, cic.x1 + 1, cic.y1 + 1, cic.z1 + 1);
    }
    
    public double min(final Direction.Axis a) {
        return a.choose(this.minX, this.minY, this.minZ);
    }
    
    public double max(final Direction.Axis a) {
        return a.choose(this.maxX, this.maxY, this.maxZ);
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AABB)) {
            return false;
        }
        final AABB csc3 = (AABB)object;
        return Double.compare(csc3.minX, this.minX) == 0 && Double.compare(csc3.minY, this.minY) == 0 && Double.compare(csc3.minZ, this.minZ) == 0 && Double.compare(csc3.maxX, this.maxX) == 0 && Double.compare(csc3.maxY, this.maxY) == 0 && Double.compare(csc3.maxZ, this.maxZ) == 0;
    }
    
    public int hashCode() {
        long long2 = Double.doubleToLongBits(this.minX);
        int integer4 = (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.minY);
        integer4 = 31 * integer4 + (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.minZ);
        integer4 = 31 * integer4 + (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.maxX);
        integer4 = 31 * integer4 + (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.maxY);
        integer4 = 31 * integer4 + (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.maxZ);
        integer4 = 31 * integer4 + (int)(long2 ^ long2 >>> 32);
        return integer4;
    }
    
    public AABB contract(final double double1, final double double2, final double double3) {
        double double4 = this.minX;
        double double5 = this.minY;
        double double6 = this.minZ;
        double double7 = this.maxX;
        double double8 = this.maxY;
        double double9 = this.maxZ;
        if (double1 < 0.0) {
            double4 -= double1;
        }
        else if (double1 > 0.0) {
            double7 -= double1;
        }
        if (double2 < 0.0) {
            double5 -= double2;
        }
        else if (double2 > 0.0) {
            double8 -= double2;
        }
        if (double3 < 0.0) {
            double6 -= double3;
        }
        else if (double3 > 0.0) {
            double9 -= double3;
        }
        return new AABB(double4, double5, double6, double7, double8, double9);
    }
    
    public AABB expandTowards(final Vec3 csi) {
        return this.expandTowards(csi.x, csi.y, csi.z);
    }
    
    public AABB expandTowards(final double double1, final double double2, final double double3) {
        double double4 = this.minX;
        double double5 = this.minY;
        double double6 = this.minZ;
        double double7 = this.maxX;
        double double8 = this.maxY;
        double double9 = this.maxZ;
        if (double1 < 0.0) {
            double4 += double1;
        }
        else if (double1 > 0.0) {
            double7 += double1;
        }
        if (double2 < 0.0) {
            double5 += double2;
        }
        else if (double2 > 0.0) {
            double8 += double2;
        }
        if (double3 < 0.0) {
            double6 += double3;
        }
        else if (double3 > 0.0) {
            double9 += double3;
        }
        return new AABB(double4, double5, double6, double7, double8, double9);
    }
    
    public AABB inflate(final double double1, final double double2, final double double3) {
        final double double4 = this.minX - double1;
        final double double5 = this.minY - double2;
        final double double6 = this.minZ - double3;
        final double double7 = this.maxX + double1;
        final double double8 = this.maxY + double2;
        final double double9 = this.maxZ + double3;
        return new AABB(double4, double5, double6, double7, double8, double9);
    }
    
    public AABB inflate(final double double1) {
        return this.inflate(double1, double1, double1);
    }
    
    public AABB intersect(final AABB csc) {
        final double double3 = Math.max(this.minX, csc.minX);
        final double double4 = Math.max(this.minY, csc.minY);
        final double double5 = Math.max(this.minZ, csc.minZ);
        final double double6 = Math.min(this.maxX, csc.maxX);
        final double double7 = Math.min(this.maxY, csc.maxY);
        final double double8 = Math.min(this.maxZ, csc.maxZ);
        return new AABB(double3, double4, double5, double6, double7, double8);
    }
    
    public AABB minmax(final AABB csc) {
        final double double3 = Math.min(this.minX, csc.minX);
        final double double4 = Math.min(this.minY, csc.minY);
        final double double5 = Math.min(this.minZ, csc.minZ);
        final double double6 = Math.max(this.maxX, csc.maxX);
        final double double7 = Math.max(this.maxY, csc.maxY);
        final double double8 = Math.max(this.maxZ, csc.maxZ);
        return new AABB(double3, double4, double5, double6, double7, double8);
    }
    
    public AABB move(final double double1, final double double2, final double double3) {
        return new AABB(this.minX + double1, this.minY + double2, this.minZ + double3, this.maxX + double1, this.maxY + double2, this.maxZ + double3);
    }
    
    public AABB move(final BlockPos ew) {
        return new AABB(this.minX + ew.getX(), this.minY + ew.getY(), this.minZ + ew.getZ(), this.maxX + ew.getX(), this.maxY + ew.getY(), this.maxZ + ew.getZ());
    }
    
    public AABB move(final Vec3 csi) {
        return this.move(csi.x, csi.y, csi.z);
    }
    
    public boolean intersects(final AABB csc) {
        return this.intersects(csc.minX, csc.minY, csc.minZ, csc.maxX, csc.maxY, csc.maxZ);
    }
    
    public boolean intersects(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        return this.minX < double4 && this.maxX > double1 && this.minY < double5 && this.maxY > double2 && this.minZ < double6 && this.maxZ > double3;
    }
    
    public boolean intersects(final Vec3 csi1, final Vec3 csi2) {
        return this.intersects(Math.min(csi1.x, csi2.x), Math.min(csi1.y, csi2.y), Math.min(csi1.z, csi2.z), Math.max(csi1.x, csi2.x), Math.max(csi1.y, csi2.y), Math.max(csi1.z, csi2.z));
    }
    
    public boolean contains(final Vec3 csi) {
        return this.contains(csi.x, csi.y, csi.z);
    }
    
    public boolean contains(final double double1, final double double2, final double double3) {
        return double1 >= this.minX && double1 < this.maxX && double2 >= this.minY && double2 < this.maxY && double3 >= this.minZ && double3 < this.maxZ;
    }
    
    public double getSize() {
        final double double2 = this.getXsize();
        final double double3 = this.getYsize();
        final double double4 = this.getZsize();
        return (double2 + double3 + double4) / 3.0;
    }
    
    public double getXsize() {
        return this.maxX - this.minX;
    }
    
    public double getYsize() {
        return this.maxY - this.minY;
    }
    
    public double getZsize() {
        return this.maxZ - this.minZ;
    }
    
    public AABB deflate(final double double1) {
        return this.inflate(-double1);
    }
    
    public Optional<Vec3> clip(final Vec3 csi1, final Vec3 csi2) {
        final double[] arr4 = { 1.0 };
        final double double5 = csi2.x - csi1.x;
        final double double6 = csi2.y - csi1.y;
        final double double7 = csi2.z - csi1.z;
        final Direction fb11 = getDirection(this, csi1, arr4, null, double5, double6, double7);
        if (fb11 == null) {
            return (Optional<Vec3>)Optional.empty();
        }
        final double double8 = arr4[0];
        return (Optional<Vec3>)Optional.of(csi1.add(double8 * double5, double8 * double6, double8 * double7));
    }
    
    @Nullable
    public static BlockHitResult clip(final Iterable<AABB> iterable, final Vec3 csi2, final Vec3 csi3, final BlockPos ew) {
        final double[] arr5 = { 1.0 };
        Direction fb6 = null;
        final double double7 = csi3.x - csi2.x;
        final double double8 = csi3.y - csi2.y;
        final double double9 = csi3.z - csi2.z;
        for (final AABB csc14 : iterable) {
            fb6 = getDirection(csc14.move(ew), csi2, arr5, fb6, double7, double8, double9);
        }
        if (fb6 == null) {
            return null;
        }
        final double double10 = arr5[0];
        return new BlockHitResult(csi2.add(double10 * double7, double10 * double8, double10 * double9), fb6, ew, false);
    }
    
    @Nullable
    private static Direction getDirection(final AABB csc, final Vec3 csi, final double[] arr, @Nullable Direction fb, final double double5, final double double6, final double double7) {
        if (double5 > 1.0E-7) {
            fb = clipPoint(arr, fb, double5, double6, double7, csc.minX, csc.minY, csc.maxY, csc.minZ, csc.maxZ, Direction.WEST, csi.x, csi.y, csi.z);
        }
        else if (double5 < -1.0E-7) {
            fb = clipPoint(arr, fb, double5, double6, double7, csc.maxX, csc.minY, csc.maxY, csc.minZ, csc.maxZ, Direction.EAST, csi.x, csi.y, csi.z);
        }
        if (double6 > 1.0E-7) {
            fb = clipPoint(arr, fb, double6, double7, double5, csc.minY, csc.minZ, csc.maxZ, csc.minX, csc.maxX, Direction.DOWN, csi.y, csi.z, csi.x);
        }
        else if (double6 < -1.0E-7) {
            fb = clipPoint(arr, fb, double6, double7, double5, csc.maxY, csc.minZ, csc.maxZ, csc.minX, csc.maxX, Direction.UP, csi.y, csi.z, csi.x);
        }
        if (double7 > 1.0E-7) {
            fb = clipPoint(arr, fb, double7, double5, double6, csc.minZ, csc.minX, csc.maxX, csc.minY, csc.maxY, Direction.NORTH, csi.z, csi.x, csi.y);
        }
        else if (double7 < -1.0E-7) {
            fb = clipPoint(arr, fb, double7, double5, double6, csc.maxZ, csc.minX, csc.maxX, csc.minY, csc.maxY, Direction.SOUTH, csi.z, csi.x, csi.y);
        }
        return fb;
    }
    
    @Nullable
    private static Direction clipPoint(final double[] arr, @Nullable final Direction fb2, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8, final double double9, final double double10, final Direction fb11, final double double12, final double double13, final double double14) {
        final double double15 = (double6 - double12) / double3;
        final double double16 = double13 + double15 * double4;
        final double double17 = double14 + double15 * double5;
        if (0.0 < double15 && double15 < arr[0] && double7 - 1.0E-7 < double16 && double16 < double8 + 1.0E-7 && double9 - 1.0E-7 < double17 && double17 < double10 + 1.0E-7) {
            arr[0] = double15;
            return fb11;
        }
        return fb2;
    }
    
    public String toString() {
        return new StringBuilder().append("box[").append(this.minX).append(", ").append(this.minY).append(", ").append(this.minZ).append("] -> [").append(this.maxX).append(", ").append(this.maxY).append(", ").append(this.maxZ).append("]").toString();
    }
    
    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }
    
    public Vec3 getCenter() {
        return new Vec3(Mth.lerp(0.5, this.minX, this.maxX), Mth.lerp(0.5, this.minY, this.maxY), Mth.lerp(0.5, this.minZ, this.maxZ));
    }
}
