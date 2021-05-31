package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class HumanoidHeadModel extends SkullModel {
    private final ModelPart hat;
    
    public HumanoidHeadModel() {
        super(0, 0, 64, 64);
        (this.hat = new ModelPart(this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.25f);
        this.hat.setPos(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        super.render(float1, float2, float3, float4, float5, float6);
        this.hat.yRot = this.head.yRot;
        this.hat.xRot = this.head.xRot;
        this.hat.render(float6);
    }
}
