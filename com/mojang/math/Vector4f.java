package com.mojang.math;

import java.util.Arrays;

public class Vector4f {
    private final float[] values;
    
    public Vector4f() {
        this.values = new float[4];
    }
    
    public Vector4f(final float float1, final float float2, final float float3, final float float4) {
        this.values = new float[] { float1, float2, float3, float4 };
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final Vector4f library. = (Vector4f)object;
        return Arrays.equals(this.values, library..values);
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
    
    public float w() {
        return this.values[3];
    }
    
    public void mul(final Vector3f b) {
        final float[] values = this.values;
        final int n = 0;
        values[n] *= b.x();
        final float[] values2 = this.values;
        final int n2 = 1;
        values2[n2] *= b.y();
        final float[] values3 = this.values;
        final int n3 = 2;
        values3[n3] *= b.z();
    }
    
    public void set(final float float1, final float float2, final float float3, final float float4) {
        this.values[0] = float1;
        this.values[1] = float2;
        this.values[2] = float3;
        this.values[3] = float4;
    }
    
    public void transform(final Quaternion a) {
        final Quaternion a2 = new Quaternion(a);
        a2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0f));
        final Quaternion a3 = new Quaternion(a);
        a3.conj();
        a2.mul(a3);
        this.set(a2.i(), a2.j(), a2.k(), this.w());
    }
}
