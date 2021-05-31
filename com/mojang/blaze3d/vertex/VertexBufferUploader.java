package com.mojang.blaze3d.vertex;

public class VertexBufferUploader extends BufferUploader {
    private VertexBuffer buffer;
    
    @Override
    public void end(final BufferBuilder cuw) {
        cuw.clear();
        this.buffer.upload(cuw.getBuffer());
    }
    
    public void setBuffer(final VertexBuffer cva) {
        this.buffer = cva;
    }
}
