package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SalmonModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart bodyFront;
    private final ModelPart bodyBack;
    private final ModelPart head;
    private final ModelPart topFin0;
    private final ModelPart topFin1;
    private final ModelPart backFin;
    private final ModelPart sideFin0;
    private final ModelPart sideFin1;
    
    public SalmonModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        final int integer2 = 20;
        (this.bodyFront = new ModelPart(this, 0, 0)).addBox(-1.5f, -2.5f, 0.0f, 3, 5, 8);
        this.bodyFront.setPos(0.0f, 20.0f, 0.0f);
        (this.bodyBack = new ModelPart(this, 0, 13)).addBox(-1.5f, -2.5f, 0.0f, 3, 5, 8);
        this.bodyBack.setPos(0.0f, 20.0f, 8.0f);
        (this.head = new ModelPart(this, 22, 0)).addBox(-1.0f, -2.0f, -3.0f, 2, 4, 3);
        this.head.setPos(0.0f, 20.0f, 0.0f);
        (this.backFin = new ModelPart(this, 20, 10)).addBox(0.0f, -2.5f, 0.0f, 0, 5, 6);
        this.backFin.setPos(0.0f, 0.0f, 8.0f);
        this.bodyBack.addChild(this.backFin);
        (this.topFin0 = new ModelPart(this, 2, 1)).addBox(0.0f, 0.0f, 0.0f, 0, 2, 3);
        this.topFin0.setPos(0.0f, -4.5f, 5.0f);
        this.bodyFront.addChild(this.topFin0);
        (this.topFin1 = new ModelPart(this, 0, 2)).addBox(0.0f, 0.0f, 0.0f, 0, 2, 4);
        this.topFin1.setPos(0.0f, -4.5f, -1.0f);
        this.bodyBack.addChild(this.topFin1);
        (this.sideFin0 = new ModelPart(this, -4, 0)).addBox(-2.0f, 0.0f, 0.0f, 2, 0, 2);
        this.sideFin0.setPos(-1.5f, 21.5f, 0.0f);
        this.sideFin0.zRot = -0.7853982f;
        (this.sideFin1 = new ModelPart(this, 0, 0)).addBox(0.0f, 0.0f, 0.0f, 2, 0, 2);
        this.sideFin1.setPos(1.5f, 21.5f, 0.0f);
        this.sideFin1.zRot = 0.7853982f;
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.bodyFront.render(float7);
        this.bodyBack.render(float7);
        this.head.render(float7);
        this.sideFin0.render(float7);
        this.sideFin1.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        float float8 = 1.0f;
        float float9 = 1.0f;
        if (!aio.isInWater()) {
            float8 = 1.3f;
            float9 = 1.7f;
        }
        this.bodyBack.yRot = -float8 * 0.25f * Mth.sin(float9 * 0.6f * float4);
    }
}
