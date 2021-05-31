package net.minecraft.core;

import com.google.common.base.MoreObjects;
import net.minecraft.util.Mth;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
    public static final Vec3i ZERO;
    private final int x;
    private final int y;
    private final int z;
    
    public Vec3i(final int integer1, final int integer2, final int integer3) {
        this.x = integer1;
        this.y = integer2;
        this.z = integer3;
    }
    
    public Vec3i(final double double1, final double double2, final double double3) {
        this(Mth.floor(double1), Mth.floor(double2), Mth.floor(double3));
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vec3i)) {
            return false;
        }
        final Vec3i fs3 = (Vec3i)object;
        return this.getX() == fs3.getX() && this.getY() == fs3.getY() && this.getZ() == fs3.getZ();
    }
    
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }
    
    public int compareTo(final Vec3i fs) {
        if (this.getY() != fs.getY()) {
            return this.getY() - fs.getY();
        }
        if (this.getZ() == fs.getZ()) {
            return this.getX() - fs.getX();
        }
        return this.getZ() - fs.getZ();
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public Vec3i cross(final Vec3i fs) {
        return new Vec3i(this.getY() * fs.getZ() - this.getZ() * fs.getY(), this.getZ() * fs.getX() - this.getX() * fs.getZ(), this.getX() * fs.getY() - this.getY() * fs.getX());
    }
    
    public boolean closerThan(final Vec3i fs, final double double2) {
        return this.distSqr(fs.x, fs.y, fs.z, false) < double2 * double2;
    }
    
    public boolean closerThan(final Position fl, final double double2) {
        return this.distSqr(fl.x(), fl.y(), fl.z(), true) < double2 * double2;
    }
    
    public double distSqr(final Vec3i fs) {
        return this.distSqr(fs.getX(), fs.getY(), fs.getZ(), true);
    }
    
    public double distSqr(final Position fl, final boolean boolean2) {
        return this.distSqr(fl.x(), fl.y(), fl.z(), boolean2);
    }
    
    public double distSqr(final double double1, final double double2, final double double3, final boolean boolean4) {
        final double double4 = boolean4 ? 0.5 : 0.0;
        final double double5 = this.getX() + double4 - double1;
        final double double6 = this.getY() + double4 - double2;
        final double double7 = this.getZ() + double4 - double3;
        return double5 * double5 + double6 * double6 + double7 * double7;
    }
    
    public int distManhattan(final Vec3i fs) {
        final float float3 = (float)Math.abs(fs.getX() - this.x);
        final float float4 = (float)Math.abs(fs.getY() - this.y);
        final float float5 = (float)Math.abs(fs.getZ() - this.z);
        return (int)(float3 + float4 + float5);
    }
    
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }
    
    static {
        ZERO = new Vec3i(0, 0, 0);
    }
}
