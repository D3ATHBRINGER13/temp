package com.mojang.blaze3d.platform;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import net.minecraft.SharedConstants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import java.nio.ByteBuffer;

public class ClipboardManager {
    private final ByteBuffer clipboardScratchBuffer;
    
    public ClipboardManager() {
        this.clipboardScratchBuffer = ByteBuffer.allocateDirect(1024);
    }
    
    public String getClipboard(final long long1, final GLFWErrorCallbackI gLFWErrorCallbackI) {
        final GLFWErrorCallback gLFWErrorCallback5 = GLFW.glfwSetErrorCallback(gLFWErrorCallbackI);
        String string6 = GLFW.glfwGetClipboardString(long1);
        string6 = ((string6 != null) ? SharedConstants.filterUnicodeSupplementary(string6) : "");
        final GLFWErrorCallback gLFWErrorCallback6 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback5);
        if (gLFWErrorCallback6 != null) {
            gLFWErrorCallback6.free();
        }
        return string6;
    }
    
    private void setClipboard(final long long1, final ByteBuffer byteBuffer, final String string) {
        MemoryUtil.memUTF8((CharSequence)string, true, byteBuffer);
        GLFW.glfwSetClipboardString(long1, byteBuffer);
    }
    
    public void setClipboard(final long long1, final String string) {
        final int integer5 = MemoryUtil.memLengthUTF8((CharSequence)string, true);
        if (integer5 < this.clipboardScratchBuffer.capacity()) {
            this.setClipboard(long1, this.clipboardScratchBuffer, string);
            this.clipboardScratchBuffer.clear();
        }
        else {
            final ByteBuffer byteBuffer6 = ByteBuffer.allocateDirect(integer5);
            this.setClipboard(long1, byteBuffer6, string);
        }
    }
}
