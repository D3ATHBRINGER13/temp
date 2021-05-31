package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class SkullModel extends Model {
    protected final ModelPart head;
    
    public SkullModel() {
        this(0, 35, 64, 64);
    }
    
    public SkullModel(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.texWidth = integer3;
        this.texHeight = integer4;
        (this.head = new ModelPart(this, integer1, integer2)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.0f);
        this.head.setPos(0.0f, 0.0f, 0.0f);
    }
    
    public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        this.head.yRot = float4 * 0.017453292f;
        this.head.xRot = float5 * 0.017453292f;
        this.head.render(float6);
    }
}
