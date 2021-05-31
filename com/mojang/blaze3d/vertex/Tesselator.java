package com.mojang.blaze3d.vertex;

public class Tesselator {
    private final BufferBuilder builder;
    private final BufferUploader uploader;
    private static final Tesselator INSTANCE;
    
    public static Tesselator getInstance() {
        return Tesselator.INSTANCE;
    }
    
    public Tesselator(final int integer) {
        this.uploader = new BufferUploader();
        this.builder = new BufferBuilder(integer);
    }
    
    public void end() {
        this.builder.end();
        this.uploader.end(this.builder);
    }
    
    public BufferBuilder getBuilder() {
        return this.builder;
    }
    
    static {
        INSTANCE = new Tesselator(2097152);
    }
}
