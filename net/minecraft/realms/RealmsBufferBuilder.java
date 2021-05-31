package net.minecraft.realms;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import com.mojang.blaze3d.vertex.BufferBuilder;

public class RealmsBufferBuilder {
    private BufferBuilder b;
    
    public RealmsBufferBuilder(final BufferBuilder cuw) {
        this.b = cuw;
    }
    
    public RealmsBufferBuilder from(final BufferBuilder cuw) {
        this.b = cuw;
        return this;
    }
    
    public void sortQuads(final float float1, final float float2, final float float3) {
        this.b.sortQuads(float1, float2, float3);
    }
    
    public void fixupQuadColor(final int integer) {
        this.b.fixupQuadColor(integer);
    }
    
    public ByteBuffer getBuffer() {
        return this.b.getBuffer();
    }
    
    public void postNormal(final float float1, final float float2, final float float3) {
        this.b.postNormal(float1, float2, float3);
    }
    
    public int getDrawMode() {
        return this.b.getDrawMode();
    }
    
    public void offset(final double double1, final double double2, final double double3) {
        this.b.offset(double1, double2, double3);
    }
    
    public void restoreState(final BufferBuilder.State a) {
        this.b.restoreState(a);
    }
    
    public void endVertex() {
        this.b.endVertex();
    }
    
    public RealmsBufferBuilder normal(final float float1, final float float2, final float float3) {
        return this.from(this.b.normal(float1, float2, float3));
    }
    
    public void end() {
        this.b.end();
    }
    
    public void begin(final int integer, final VertexFormat cvc) {
        this.b.begin(integer, cvc);
    }
    
    public RealmsBufferBuilder color(final int integer1, final int integer2, final int integer3, final int integer4) {
        return this.from(this.b.color(integer1, integer2, integer3, integer4));
    }
    
    public void faceTex2(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.b.faceTex2(integer1, integer2, integer3, integer4);
    }
    
    public void postProcessFacePosition(final double double1, final double double2, final double double3) {
        this.b.postProcessFacePosition(double1, double2, double3);
    }
    
    public void fixupVertexColor(final float float1, final float float2, final float float3, final int integer) {
        this.b.fixupVertexColor(float1, float2, float3, integer);
    }
    
    public RealmsBufferBuilder color(final float float1, final float float2, final float float3, final float float4) {
        return this.from(this.b.color(float1, float2, float3, float4));
    }
    
    public RealmsVertexFormat getVertexFormat() {
        return new RealmsVertexFormat(this.b.getVertexFormat());
    }
    
    public void faceTint(final float float1, final float float2, final float float3, final int integer) {
        this.b.faceTint(float1, float2, float3, integer);
    }
    
    public RealmsBufferBuilder tex2(final int integer1, final int integer2) {
        return this.from(this.b.uv2(integer1, integer2));
    }
    
    public void putBulkData(final int[] arr) {
        this.b.putBulkData(arr);
    }
    
    public RealmsBufferBuilder tex(final double double1, final double double2) {
        return this.from(this.b.uv(double1, double2));
    }
    
    public int getVertexCount() {
        return this.b.getVertexCount();
    }
    
    public void clear() {
        this.b.clear();
    }
    
    public RealmsBufferBuilder vertex(final double double1, final double double2, final double double3) {
        return this.from(this.b.vertex(double1, double2, double3));
    }
    
    public void fixupQuadColor(final float float1, final float float2, final float float3) {
        this.b.fixupQuadColor(float1, float2, float3);
    }
    
    public void noColor() {
        this.b.noColor();
    }
}
