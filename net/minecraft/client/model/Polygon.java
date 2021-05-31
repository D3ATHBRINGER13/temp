package net.minecraft.client.model;

import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;

public class Polygon {
    public Vertex[] vertices;
    public final int vertexCount;
    private boolean flipNormal;
    
    public Polygon(final Vertex[] arr) {
        this.vertices = arr;
        this.vertexCount = arr.length;
    }
    
    public Polygon(final Vertex[] arr, final int integer2, final int integer3, final int integer4, final int integer5, final float float6, final float float7) {
        this(arr);
        final float float8 = 0.0f / float6;
        final float float9 = 0.0f / float7;
        arr[0] = arr[0].remap(integer4 / float6 - float8, integer3 / float7 + float9);
        arr[1] = arr[1].remap(integer2 / float6 + float8, integer3 / float7 + float9);
        arr[2] = arr[2].remap(integer2 / float6 + float8, integer5 / float7 - float9);
        arr[3] = arr[3].remap(integer4 / float6 - float8, integer5 / float7 - float9);
    }
    
    public void mirror() {
        final Vertex[] arr2 = new Vertex[this.vertices.length];
        for (int integer3 = 0; integer3 < this.vertices.length; ++integer3) {
            arr2[integer3] = this.vertices[this.vertices.length - integer3 - 1];
        }
        this.vertices = arr2;
    }
    
    public void render(final BufferBuilder cuw, final float float2) {
        final Vec3 csi4 = this.vertices[1].pos.vectorTo(this.vertices[0].pos);
        final Vec3 csi5 = this.vertices[1].pos.vectorTo(this.vertices[2].pos);
        final Vec3 csi6 = csi5.cross(csi4).normalize();
        float float3 = (float)csi6.x;
        float float4 = (float)csi6.y;
        float float5 = (float)csi6.z;
        if (this.flipNormal) {
            float3 = -float3;
            float4 = -float4;
            float5 = -float5;
        }
        cuw.begin(7, DefaultVertexFormat.ENTITY);
        for (int integer10 = 0; integer10 < 4; ++integer10) {
            final Vertex djh11 = this.vertices[integer10];
            cuw.vertex(djh11.pos.x * float2, djh11.pos.y * float2, djh11.pos.z * float2).uv(djh11.u, djh11.v).normal(float3, float4, float5).endVertex();
        }
        Tesselator.getInstance().end();
    }
}
