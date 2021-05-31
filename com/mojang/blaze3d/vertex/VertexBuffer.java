package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import com.mojang.blaze3d.platform.GLX;

public class VertexBuffer {
    private int id;
    private final VertexFormat format;
    private int vertexCount;
    
    public VertexBuffer(final VertexFormat cvc) {
        this.format = cvc;
        this.id = GLX.glGenBuffers();
    }
    
    public void bind() {
        GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, this.id);
    }
    
    public void upload(final ByteBuffer byteBuffer) {
        this.bind();
        GLX.glBufferData(GLX.GL_ARRAY_BUFFER, byteBuffer, 35044);
        unbind();
        this.vertexCount = byteBuffer.limit() / this.format.getVertexSize();
    }
    
    public void draw(final int integer) {
        GlStateManager.drawArrays(integer, 0, this.vertexCount);
    }
    
    public static void unbind() {
        GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, 0);
    }
    
    public void delete() {
        if (this.id >= 0) {
            GLX.glDeleteBuffers(this.id);
            this.id = -1;
        }
    }
}
