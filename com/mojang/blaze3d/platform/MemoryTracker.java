package com.mojang.blaze3d.platform;

import java.nio.FloatBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class MemoryTracker {
    public static synchronized int genLists(final int integer) {
        final int integer2 = GlStateManager.genLists(integer);
        if (integer2 == 0) {
            final int integer3 = GlStateManager.getError();
            String string4 = "No error code reported";
            if (integer3 != 0) {
                string4 = GLX.getErrorString(integer3);
            }
            throw new IllegalStateException(new StringBuilder().append("glGenLists returned an ID of 0 for a count of ").append(integer).append(", GL error (").append(integer3).append("): ").append(string4).toString());
        }
        return integer2;
    }
    
    public static synchronized void releaseLists(final int integer1, final int integer2) {
        GlStateManager.deleteLists(integer1, integer2);
    }
    
    public static synchronized void releaseList(final int integer) {
        releaseLists(integer, 1);
    }
    
    public static synchronized ByteBuffer createByteBuffer(final int integer) {
        return ByteBuffer.allocateDirect(integer).order(ByteOrder.nativeOrder());
    }
    
    public static FloatBuffer createFloatBuffer(final int integer) {
        return createByteBuffer(integer << 2).asFloatBuffer();
    }
}
