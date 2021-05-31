package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.model.Polygon;
import net.minecraft.client.model.Vertex;

public class Cube {
    private final Vertex[] vertices;
    private final Polygon[] polygons;
    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;
    public String id;
    
    public Cube(final ModelPart djv, final int integer2, final int integer3, final float float4, final float float5, final float float6, final int integer7, final int integer8, final int integer9, final float float10) {
        this(djv, integer2, integer3, float4, float5, float6, integer7, integer8, integer9, float10, djv.mirror);
    }
    
    public Cube(final ModelPart djv, final int integer2, final int integer3, float float4, float float5, float float6, final int integer7, final int integer8, final int integer9, final float float10, final boolean boolean11) {
        this.minX = float4;
        this.minY = float5;
        this.minZ = float6;
        this.maxX = float4 + integer7;
        this.maxY = float5 + integer8;
        this.maxZ = float6 + integer9;
        this.vertices = new Vertex[8];
        this.polygons = new Polygon[6];
        float float11 = float4 + integer7;
        float float12 = float5 + integer8;
        float float13 = float6 + integer9;
        float4 -= float10;
        float5 -= float10;
        float6 -= float10;
        float11 += float10;
        float12 += float10;
        float13 += float10;
        if (boolean11) {
            final float float14 = float11;
            float11 = float4;
            float4 = float14;
        }
        final Vertex djh16 = new Vertex(float4, float5, float6, 0.0f, 0.0f);
        final Vertex djh17 = new Vertex(float11, float5, float6, 0.0f, 8.0f);
        final Vertex djh18 = new Vertex(float11, float12, float6, 8.0f, 8.0f);
        final Vertex djh19 = new Vertex(float4, float12, float6, 8.0f, 0.0f);
        final Vertex djh20 = new Vertex(float4, float5, float13, 0.0f, 0.0f);
        final Vertex djh21 = new Vertex(float11, float5, float13, 0.0f, 8.0f);
        final Vertex djh22 = new Vertex(float11, float12, float13, 8.0f, 8.0f);
        final Vertex djh23 = new Vertex(float4, float12, float13, 8.0f, 0.0f);
        this.vertices[0] = djh16;
        this.vertices[1] = djh17;
        this.vertices[2] = djh18;
        this.vertices[3] = djh19;
        this.vertices[4] = djh20;
        this.vertices[5] = djh21;
        this.vertices[6] = djh22;
        this.vertices[7] = djh23;
        this.polygons[0] = new Polygon(new Vertex[] { djh21, djh17, djh18, djh22 }, integer2 + integer9 + integer7, integer3 + integer9, integer2 + integer9 + integer7 + integer9, integer3 + integer9 + integer8, djv.xTexSize, djv.yTexSize);
        this.polygons[1] = new Polygon(new Vertex[] { djh16, djh20, djh23, djh19 }, integer2, integer3 + integer9, integer2 + integer9, integer3 + integer9 + integer8, djv.xTexSize, djv.yTexSize);
        this.polygons[2] = new Polygon(new Vertex[] { djh21, djh20, djh16, djh17 }, integer2 + integer9, integer3, integer2 + integer9 + integer7, integer3 + integer9, djv.xTexSize, djv.yTexSize);
        this.polygons[3] = new Polygon(new Vertex[] { djh18, djh19, djh23, djh22 }, integer2 + integer9 + integer7, integer3 + integer9, integer2 + integer9 + integer7 + integer7, integer3, djv.xTexSize, djv.yTexSize);
        this.polygons[4] = new Polygon(new Vertex[] { djh17, djh16, djh19, djh18 }, integer2 + integer9, integer3 + integer9, integer2 + integer9 + integer7, integer3 + integer9 + integer8, djv.xTexSize, djv.yTexSize);
        this.polygons[5] = new Polygon(new Vertex[] { djh20, djh21, djh22, djh23 }, integer2 + integer9 + integer7 + integer9, integer3 + integer9, integer2 + integer9 + integer7 + integer9 + integer7, integer3 + integer9 + integer8, djv.xTexSize, djv.yTexSize);
        if (boolean11) {
            for (final Polygon dih27 : this.polygons) {
                dih27.mirror();
            }
        }
    }
    
    public void compile(final BufferBuilder cuw, final float float2) {
        for (final Polygon dih7 : this.polygons) {
            dih7.render(cuw, float2);
        }
    }
    
    public Cube setId(final String string) {
        this.id = string;
        return this;
    }
}
