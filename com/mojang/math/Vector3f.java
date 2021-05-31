package com.mojang.math;

import net.minecraft.world.phys.Vec3;
import java.util.Arrays;

public final class Vector3f {
    private final float[] values;
    
    public Vector3f(final Vector3f b) {
        this.values = Arrays.copyOf(b.values, 3);
    }
    
    public Vector3f() {
        this.values = new float[3];
    }
    
    public Vector3f(final float float1, final float float2, final float float3) {
        this.values = new float[] { float1, float2, float3 };
    }
    
    public Vector3f(final Vec3 csi) {
        this.values = new float[] { (float)csi.x, (float)csi.y, (float)csi.z };
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final Vector3f b3 = (Vector3f)object;
        return Arrays.equals(this.values, b3.values);
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
    
    public float x() {
        return this.values[0];
    }
    
    public float y() {
        return this.values[1];
    }
    
    public float z() {
        return this.values[2];
    }
    
    public void mul(final float float1) {
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            final float[] values = this.values;
            final int n = integer3;
            values[n] *= float1;
        }
    }
    
    private static float clamp(final float float1, final float float2, final float float3) {
        if (float1 < float2) {
            return float2;
        }
        if (float1 > float3) {
            return float3;
        }
        return float1;
    }
    
    public void clamp(final float float1, final float float2) {
        this.values[0] = clamp(this.values[0], float1, float2);
        this.values[1] = clamp(this.values[1], float1, float2);
        this.values[2] = clamp(this.values[2], float1, float2);
    }
    
    public void set(final float float1, final float float2, final float float3) {
        this.values[0] = float1;
        this.values[1] = float2;
        this.values[2] = float3;
    }
    
    public void add(final float float1, final float float2, final float float3) {
        final float[] values = this.values;
        final int n = 0;
        values[n] += float1;
        final float[] values2 = this.values;
        final int n2 = 1;
        values2[n2] += float2;
        final float[] values3 = this.values;
        final int n3 = 2;
        values3[n3] += float3;
    }
    
    public void sub(final Vector3f b) {
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            final float[] values = this.values;
            final int n = integer3;
            values[n] -= b.values[integer3];
        }
    }
    
    public float dot(final Vector3f b) {
        float float3 = 0.0f;
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            float3 += this.values[integer4] * b.values[integer4];
        }
        return float3;
    }
    
    public void normalize() {
        float float2 = 0.0f;
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            float2 += this.values[integer3] * this.values[integer3];
        }
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            final float[] values = this.values;
            final int n = integer3;
            values[n] /= float2;
        }
    }
    
    public void cross(final Vector3f b) {
        final float float3 = this.values[0];
        final float float4 = this.values[1];
        final float float5 = this.values[2];
        final float float6 = b.x();
        final float float7 = b.y();
        final float float8 = b.z();
        this.values[0] = float4 * float8 - float5 * float7;
        this.values[1] = float5 * float6 - float3 * float8;
        this.values[2] = float3 * float7 - float4 * float6;
    }
    
    public void transform(final Quaternion a) {
        final Quaternion a2 = new Quaternion(a);
        a2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0f));
        final Quaternion a3 = new Quaternion(a);
        a3.conj();
        a2.mul(a3);
        this.set(a2.i(), a2.j(), a2.k());
    }
}
