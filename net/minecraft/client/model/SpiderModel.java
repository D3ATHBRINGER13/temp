package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SpiderModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart body0;
    private final ModelPart body1;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart leg5;
    private final ModelPart leg6;
    private final ModelPart leg7;
    
    public SpiderModel() {
        final float float2 = 0.0f;
        final int integer3 = 15;
        (this.head = new ModelPart(this, 32, 4)).addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, 0.0f);
        this.head.setPos(0.0f, 15.0f, -3.0f);
        (this.body0 = new ModelPart(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, 0.0f);
        this.body0.setPos(0.0f, 15.0f, 0.0f);
        (this.body1 = new ModelPart(this, 0, 12)).addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, 0.0f);
        this.body1.setPos(0.0f, 15.0f, 9.0f);
        (this.leg0 = new ModelPart(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg0.setPos(-4.0f, 15.0f, 2.0f);
        (this.leg1 = new ModelPart(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg1.setPos(4.0f, 15.0f, 2.0f);
        (this.leg2 = new ModelPart(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg2.setPos(-4.0f, 15.0f, 1.0f);
        (this.leg3 = new ModelPart(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg3.setPos(4.0f, 15.0f, 1.0f);
        (this.leg4 = new ModelPart(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg4.setPos(-4.0f, 15.0f, 0.0f);
        (this.leg5 = new ModelPart(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg5.setPos(4.0f, 15.0f, 0.0f);
        (this.leg6 = new ModelPart(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg6.setPos(-4.0f, 15.0f, -1.0f);
        (this.leg7 = new ModelPart(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.leg7.setPos(4.0f, 15.0f, -1.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        this.body0.render(float7);
        this.body1.render(float7);
        this.leg0.render(float7);
        this.leg1.render(float7);
        this.leg2.render(float7);
        this.leg3.render(float7);
        this.leg4.render(float7);
        this.leg5.render(float7);
        this.leg6.render(float7);
        this.leg7.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        final float float8 = 0.7853982f;
        this.leg0.zRot = -0.7853982f;
        this.leg1.zRot = 0.7853982f;
        this.leg2.zRot = -0.58119464f;
        this.leg3.zRot = 0.58119464f;
        this.leg4.zRot = -0.58119464f;
        this.leg5.zRot = 0.58119464f;
        this.leg6.zRot = -0.7853982f;
        this.leg7.zRot = 0.7853982f;
        final float float9 = -0.0f;
        final float float10 = 0.3926991f;
        this.leg0.yRot = 0.7853982f;
        this.leg1.yRot = -0.7853982f;
        this.leg2.yRot = 0.3926991f;
        this.leg3.yRot = -0.3926991f;
        this.leg4.yRot = -0.3926991f;
        this.leg5.yRot = 0.3926991f;
        this.leg6.yRot = -0.7853982f;
        this.leg7.yRot = 0.7853982f;
        final float float11 = -(Mth.cos(float2 * 0.6662f * 2.0f + 0.0f) * 0.4f) * float3;
        final float float12 = -(Mth.cos(float2 * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * float3;
        final float float13 = -(Mth.cos(float2 * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * float3;
        final float float14 = -(Mth.cos(float2 * 0.6662f * 2.0f + 4.712389f) * 0.4f) * float3;
        final float float15 = Math.abs(Mth.sin(float2 * 0.6662f + 0.0f) * 0.4f) * float3;
        final float float16 = Math.abs(Mth.sin(float2 * 0.6662f + 3.1415927f) * 0.4f) * float3;
        final float float17 = Math.abs(Mth.sin(float2 * 0.6662f + 1.5707964f) * 0.4f) * float3;
        final float float18 = Math.abs(Mth.sin(float2 * 0.6662f + 4.712389f) * 0.4f) * float3;
        final ModelPart leg0 = this.leg0;
        leg0.yRot += float11;
        final ModelPart leg2 = this.leg1;
        leg2.yRot += -float11;
        final ModelPart leg3 = this.leg2;
        leg3.yRot += float12;
        final ModelPart leg4 = this.leg3;
        leg4.yRot += -float12;
        final ModelPart leg5 = this.leg4;
        leg5.yRot += float13;
        final ModelPart leg6 = this.leg5;
        leg6.yRot += -float13;
        final ModelPart leg7 = this.leg6;
        leg7.yRot += float14;
        final ModelPart leg8 = this.leg7;
        leg8.yRot += -float14;
        final ModelPart leg9 = this.leg0;
        leg9.zRot += float15;
        final ModelPart leg10 = this.leg1;
        leg10.zRot += -float15;
        final ModelPart leg11 = this.leg2;
        leg11.zRot += float16;
        final ModelPart leg12 = this.leg3;
        leg12.zRot += -float16;
        final ModelPart leg13 = this.leg4;
        leg13.zRot += float17;
        final ModelPart leg14 = this.leg5;
        leg14.zRot += -float17;
        final ModelPart leg15 = this.leg6;
        leg15.zRot += float18;
        final ModelPart leg16 = this.leg7;
        leg16.zRot += -float18;
    }
}
