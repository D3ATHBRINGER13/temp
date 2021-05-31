package net.minecraft.realms;

import com.mojang.blaze3d.vertex.Tesselator;

public class Tezzelator {
    public static final Tesselator t;
    public static final Tezzelator instance;
    
    public void end() {
        Tezzelator.t.end();
    }
    
    public Tezzelator vertex(final double double1, final double double2, final double double3) {
        Tezzelator.t.getBuilder().vertex(double1, double2, double3);
        return this;
    }
    
    public void color(final float float1, final float float2, final float float3, final float float4) {
        Tezzelator.t.getBuilder().color(float1, float2, float3, float4);
    }
    
    public void tex2(final short short1, final short short2) {
        Tezzelator.t.getBuilder().uv2(short1, short2);
    }
    
    public void normal(final float float1, final float float2, final float float3) {
        Tezzelator.t.getBuilder().normal(float1, float2, float3);
    }
    
    public void begin(final int integer, final RealmsVertexFormat realmsVertexFormat) {
        Tezzelator.t.getBuilder().begin(integer, realmsVertexFormat.getVertexFormat());
    }
    
    public void endVertex() {
        Tezzelator.t.getBuilder().endVertex();
    }
    
    public void offset(final double double1, final double double2, final double double3) {
        Tezzelator.t.getBuilder().offset(double1, double2, double3);
    }
    
    public RealmsBufferBuilder color(final int integer1, final int integer2, final int integer3, final int integer4) {
        return new RealmsBufferBuilder(Tezzelator.t.getBuilder().color(integer1, integer2, integer3, integer4));
    }
    
    public Tezzelator tex(final double double1, final double double2) {
        Tezzelator.t.getBuilder().uv(double1, double2);
        return this;
    }
    
    static {
        t = Tesselator.getInstance();
        instance = new Tezzelator();
    }
}
