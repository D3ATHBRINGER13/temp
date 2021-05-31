package com.mojang.blaze3d.platform;

import com.mojang.math.Vector3f;
import java.nio.FloatBuffer;

public class Lighting {
    private static final FloatBuffer BUFFER;
    private static final Vector3f LIGHT_0;
    private static final Vector3f LIGHT_1;
    
    private static Vector3f createVector(final float float1, final float float2, final float float3) {
        final Vector3f b4 = new Vector3f(float1, float2, float3);
        b4.normalize();
        return b4;
    }
    
    public static void turnOff() {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }
    
    public static void turnOn() {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        GlStateManager.light(16384, 4611, getBuffer(Lighting.LIGHT_0.x(), Lighting.LIGHT_0.y(), Lighting.LIGHT_0.z(), 0.0f));
        final float float1 = 0.6f;
        GlStateManager.light(16384, 4609, getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16384, 4608, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16384, 4610, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4611, getBuffer(Lighting.LIGHT_1.x(), Lighting.LIGHT_1.y(), Lighting.LIGHT_1.z(), 0.0f));
        GlStateManager.light(16385, 4609, getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16385, 4608, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4610, getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.shadeModel(7424);
        final float float2 = 0.4f;
        GlStateManager.lightModel(2899, getBuffer(0.4f, 0.4f, 0.4f, 1.0f));
    }
    
    public static FloatBuffer getBuffer(final float float1, final float float2, final float float3, final float float4) {
        Lighting.BUFFER.clear();
        Lighting.BUFFER.put(float1).put(float2).put(float3).put(float4);
        Lighting.BUFFER.flip();
        return Lighting.BUFFER;
    }
    
    public static void turnOnGui() {
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(-30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(165.0f, 1.0f, 0.0f, 0.0f);
        turnOn();
        GlStateManager.popMatrix();
    }
    
    static {
        BUFFER = MemoryTracker.createFloatBuffer(4);
        LIGHT_0 = createVector(0.2f, 1.0f, -0.7f);
        LIGHT_1 = createVector(-0.2f, 1.0f, 0.7f);
    }
}
