package com.mojang.math;

import java.util.Arrays;

public final class Quaternion {
    private final float[] values;
    
    public Quaternion() {
        (this.values = new float[4])[4] = 1.0f;
    }
    
    public Quaternion(final float float1, final float float2, final float float3, final float float4) {
        (this.values = new float[4])[0] = float1;
        this.values[1] = float2;
        this.values[2] = float3;
        this.values[3] = float4;
    }
    
    public Quaternion(final Vector3f b, float float2, final boolean boolean3) {
        if (boolean3) {
            float2 *= 0.017453292f;
        }
        final float float3 = sin(float2 / 2.0f);
        (this.values = new float[4])[0] = b.x() * float3;
        this.values[1] = b.y() * float3;
        this.values[2] = b.z() * float3;
        this.values[3] = cos(float2 / 2.0f);
    }
    
    public Quaternion(float float1, float float2, float float3, final boolean boolean4) {
        if (boolean4) {
            float1 *= 0.017453292f;
            float2 *= 0.017453292f;
            float3 *= 0.017453292f;
        }
        final float float4 = sin(0.5f * float1);
        final float float5 = cos(0.5f * float1);
        final float float6 = sin(0.5f * float2);
        final float float7 = cos(0.5f * float2);
        final float float8 = sin(0.5f * float3);
        final float float9 = cos(0.5f * float3);
        (this.values = new float[4])[0] = float4 * float7 * float9 + float5 * float6 * float8;
        this.values[1] = float5 * float6 * float9 - float4 * float7 * float8;
        this.values[2] = float4 * float6 * float9 + float5 * float7 * float8;
        this.values[3] = float5 * float7 * float9 - float4 * float6 * float8;
    }
    
    public Quaternion(final Quaternion a) {
        this.values = Arrays.copyOf(a.values, 4);
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final Quaternion a3 = (Quaternion)object;
        return Arrays.equals(this.values, a3.values);
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Quaternion[").append(this.r()).append(" + ");
        stringBuilder2.append(this.i()).append("i + ");
        stringBuilder2.append(this.j()).append("j + ");
        stringBuilder2.append(this.k()).append("k]");
        return stringBuilder2.toString();
    }
    
    public float i() {
        return this.values[0];
    }
    
    public float j() {
        return this.values[1];
    }
    
    public float k() {
        return this.values[2];
    }
    
    public float r() {
        return this.values[3];
    }
    
    public void mul(final Quaternion a) {
        final float float3 = this.i();
        final float float4 = this.j();
        final float float5 = this.k();
        final float float6 = this.r();
        final float float7 = a.i();
        final float float8 = a.j();
        final float float9 = a.k();
        final float float10 = a.r();
        this.values[0] = float6 * float7 + float3 * float10 + float4 * float9 - float5 * float8;
        this.values[1] = float6 * float8 - float3 * float9 + float4 * float10 + float5 * float7;
        this.values[2] = float6 * float9 + float3 * float8 - float4 * float7 + float5 * float10;
        this.values[3] = float6 * float10 - float3 * float7 - float4 * float8 - float5 * float9;
    }
    
    public void conj() {
        this.values[0] = -this.values[0];
        this.values[1] = -this.values[1];
        this.values[2] = -this.values[2];
    }
    
    private static float cos(final float float1) {
        return (float)Math.cos((double)float1);
    }
    
    private static float sin(final float float1) {
        return (float)Math.sin((double)float1);
    }
}
