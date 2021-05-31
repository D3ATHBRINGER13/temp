package net.minecraft.client.model;

import net.minecraft.world.phys.Vec3;

public class Vertex {
    public final Vec3 pos;
    public final float u;
    public final float v;
    
    public Vertex(final float float1, final float float2, final float float3, final float float4, final float float5) {
        this(new Vec3(float1, float2, float3), float4, float5);
    }
    
    public Vertex remap(final float float1, final float float2) {
        return new Vertex(this, float1, float2);
    }
    
    public Vertex(final Vertex djh, final float float2, final float float3) {
        this.pos = djh.pos;
        this.u = float2;
        this.v = float3;
    }
    
    public Vertex(final Vec3 csi, final float float2, final float float3) {
        this.pos = csi;
        this.u = float2;
        this.v = float3;
    }
}
