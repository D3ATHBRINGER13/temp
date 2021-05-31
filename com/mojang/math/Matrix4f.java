package com.mojang.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

public final class Matrix4f {
    private final float[] values;
    
    public Matrix4f() {
        this.values = new float[16];
    }
    
    public Matrix4f(final Quaternion a) {
        this();
        final float float3 = a.i();
        final float float4 = a.j();
        final float float5 = a.k();
        final float float6 = a.r();
        final float float7 = 2.0f * float3 * float3;
        final float float8 = 2.0f * float4 * float4;
        final float float9 = 2.0f * float5 * float5;
        this.values[0] = 1.0f - float8 - float9;
        this.values[5] = 1.0f - float9 - float7;
        this.values[10] = 1.0f - float7 - float8;
        this.values[15] = 1.0f;
        final float float10 = float3 * float4;
        final float float11 = float4 * float5;
        final float float12 = float5 * float3;
        final float float13 = float3 * float6;
        final float float14 = float4 * float6;
        final float float15 = float5 * float6;
        this.values[1] = 2.0f * (float10 + float15);
        this.values[4] = 2.0f * (float10 - float15);
        this.values[2] = 2.0f * (float12 - float14);
        this.values[8] = 2.0f * (float12 + float14);
        this.values[6] = 2.0f * (float11 + float13);
        this.values[9] = 2.0f * (float11 - float13);
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final Matrix4f blaze3D. = (Matrix4f)object;
        return Arrays.equals(this.values, blaze3D..values);
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
    
    public void load(final FloatBuffer floatBuffer) {
        this.load(floatBuffer, false);
    }
    
    public void load(final FloatBuffer floatBuffer, final boolean boolean2) {
        if (boolean2) {
            for (int integer4 = 0; integer4 < 4; ++integer4) {
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    this.values[integer4 * 4 + integer5] = floatBuffer.get(integer5 * 4 + integer4);
                }
            }
        }
        else {
            floatBuffer.get(this.values);
        }
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Matrix4f:\n");
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            for (int integer4 = 0; integer4 < 4; ++integer4) {
                stringBuilder2.append(this.values[integer3 + integer4 * 4]);
                if (integer4 != 3) {
                    stringBuilder2.append(" ");
                }
            }
            stringBuilder2.append("\n");
        }
        return stringBuilder2.toString();
    }
    
    public void store(final FloatBuffer floatBuffer) {
        this.store(floatBuffer, false);
    }
    
    public void store(final FloatBuffer floatBuffer, final boolean boolean2) {
        if (boolean2) {
            for (int integer4 = 0; integer4 < 4; ++integer4) {
                for (int integer5 = 0; integer5 < 4; ++integer5) {
                    floatBuffer.put(integer5 * 4 + integer4, this.values[integer4 * 4 + integer5]);
                }
            }
        }
        else {
            floatBuffer.put(this.values);
        }
    }
    
    public void set(final int integer1, final int integer2, final float float3) {
        this.values[integer1 + 4 * integer2] = float3;
    }
    
    public static Matrix4f perspective(final double double1, final float float2, final float float3, final float float4) {
        final float float5 = (float)(1.0 / Math.tan(double1 * 0.01745329238474369 / 2.0));
        final Matrix4f blaze3D. = new Matrix4f();
        blaze3D..set(0, 0, float5 / float2);
        blaze3D..set(1, 1, float5);
        blaze3D..set(2, 2, (float4 + float3) / (float3 - float4));
        blaze3D..set(3, 2, -1.0f);
        blaze3D..set(2, 3, 2.0f * float4 * float3 / (float3 - float4));
        return blaze3D.;
    }
    
    public static Matrix4f orthographic(final float float1, final float float2, final float float3, final float float4) {
        final Matrix4f blaze3D. = new Matrix4f();
        blaze3D..set(0, 0, 2.0f / float1);
        blaze3D..set(1, 1, 2.0f / float2);
        final float float5 = float4 - float3;
        blaze3D..set(2, 2, -2.0f / float5);
        blaze3D..set(3, 3, 1.0f);
        blaze3D..set(0, 3, -1.0f);
        blaze3D..set(1, 3, -1.0f);
        blaze3D..set(2, 3, -(float4 + float3) / float5);
        return blaze3D.;
    }
}
