package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel<T extends Rabbit> extends EntityModel<T> {
    private final ModelPart rearFootLeft;
    private final ModelPart rearFootRight;
    private final ModelPart haunchLeft;
    private final ModelPart haunchRight;
    private final ModelPart body;
    private final ModelPart frontLegLeft;
    private final ModelPart frontLegRight;
    private final ModelPart head;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart tail;
    private final ModelPart nose;
    private float jumpRotation;
    
    public RabbitModel() {
        (this.rearFootLeft = new ModelPart(this, 26, 24)).addBox(-1.0f, 5.5f, -3.7f, 2, 1, 7);
        this.rearFootLeft.setPos(3.0f, 17.5f, 3.7f);
        this.rearFootLeft.mirror = true;
        this.setRotation(this.rearFootLeft, 0.0f, 0.0f, 0.0f);
        (this.rearFootRight = new ModelPart(this, 8, 24)).addBox(-1.0f, 5.5f, -3.7f, 2, 1, 7);
        this.rearFootRight.setPos(-3.0f, 17.5f, 3.7f);
        this.rearFootRight.mirror = true;
        this.setRotation(this.rearFootRight, 0.0f, 0.0f, 0.0f);
        (this.haunchLeft = new ModelPart(this, 30, 15)).addBox(-1.0f, 0.0f, 0.0f, 2, 4, 5);
        this.haunchLeft.setPos(3.0f, 17.5f, 3.7f);
        this.haunchLeft.mirror = true;
        this.setRotation(this.haunchLeft, -0.34906584f, 0.0f, 0.0f);
        (this.haunchRight = new ModelPart(this, 16, 15)).addBox(-1.0f, 0.0f, 0.0f, 2, 4, 5);
        this.haunchRight.setPos(-3.0f, 17.5f, 3.7f);
        this.haunchRight.mirror = true;
        this.setRotation(this.haunchRight, -0.34906584f, 0.0f, 0.0f);
        (this.body = new ModelPart(this, 0, 0)).addBox(-3.0f, -2.0f, -10.0f, 6, 5, 10);
        this.body.setPos(0.0f, 19.0f, 8.0f);
        this.body.mirror = true;
        this.setRotation(this.body, -0.34906584f, 0.0f, 0.0f);
        (this.frontLegLeft = new ModelPart(this, 8, 15)).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
        this.frontLegLeft.setPos(3.0f, 17.0f, -1.0f);
        this.frontLegLeft.mirror = true;
        this.setRotation(this.frontLegLeft, -0.17453292f, 0.0f, 0.0f);
        (this.frontLegRight = new ModelPart(this, 0, 15)).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
        this.frontLegRight.setPos(-3.0f, 17.0f, -1.0f);
        this.frontLegRight.mirror = true;
        this.setRotation(this.frontLegRight, -0.17453292f, 0.0f, 0.0f);
        (this.head = new ModelPart(this, 32, 0)).addBox(-2.5f, -4.0f, -5.0f, 5, 4, 5);
        this.head.setPos(0.0f, 16.0f, -1.0f);
        this.head.mirror = true;
        this.setRotation(this.head, 0.0f, 0.0f, 0.0f);
        (this.earRight = new ModelPart(this, 52, 0)).addBox(-2.5f, -9.0f, -1.0f, 2, 5, 1);
        this.earRight.setPos(0.0f, 16.0f, -1.0f);
        this.earRight.mirror = true;
        this.setRotation(this.earRight, 0.0f, -0.2617994f, 0.0f);
        (this.earLeft = new ModelPart(this, 58, 0)).addBox(0.5f, -9.0f, -1.0f, 2, 5, 1);
        this.earLeft.setPos(0.0f, 16.0f, -1.0f);
        this.earLeft.mirror = true;
        this.setRotation(this.earLeft, 0.0f, 0.2617994f, 0.0f);
        (this.tail = new ModelPart(this, 52, 6)).addBox(-1.5f, -1.5f, 0.0f, 3, 3, 2);
        this.tail.setPos(0.0f, 20.0f, 7.0f);
        this.tail.mirror = true;
        this.setRotation(this.tail, -0.3490659f, 0.0f, 0.0f);
        (this.nose = new ModelPart(this, 32, 9)).addBox(-0.5f, -2.5f, -5.5f, 1, 1, 1);
        this.nose.setPos(0.0f, 16.0f, -1.0f);
        this.nose.mirror = true;
        this.setRotation(this.nose, 0.0f, 0.0f, 0.0f);
    }
    
    private void setRotation(final ModelPart djv, final float float2, final float float3, final float float4) {
        djv.xRot = float2;
        djv.yRot = float3;
        djv.zRot = float4;
    }
    
    @Override
    public void setupAnim(final T arq, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(arq, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            final float float8 = 1.5f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.56666666f, 0.56666666f, 0.56666666f);
            GlStateManager.translatef(0.0f, 22.0f * float7, 2.0f * float7);
            this.head.render(float7);
            this.earLeft.render(float7);
            this.earRight.render(float7);
            this.nose.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.4f, 0.4f, 0.4f);
            GlStateManager.translatef(0.0f, 36.0f * float7, 0.0f);
            this.rearFootLeft.render(float7);
            this.rearFootRight.render(float7);
            this.haunchLeft.render(float7);
            this.haunchRight.render(float7);
            this.body.render(float7);
            this.frontLegLeft.render(float7);
            this.frontLegRight.render(float7);
            this.tail.render(float7);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6f, 0.6f, 0.6f);
            GlStateManager.translatef(0.0f, 16.0f * float7, 0.0f);
            this.rearFootLeft.render(float7);
            this.rearFootRight.render(float7);
            this.haunchLeft.render(float7);
            this.haunchRight.render(float7);
            this.body.render(float7);
            this.frontLegLeft.render(float7);
            this.frontLegRight.render(float7);
            this.head.render(float7);
            this.earRight.render(float7);
            this.earLeft.render(float7);
            this.tail.render(float7);
            this.nose.render(float7);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void setupAnim(final T arq, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final float float8 = float4 - arq.tickCount;
        this.nose.xRot = float6 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        this.earRight.xRot = float6 * 0.017453292f;
        this.earLeft.xRot = float6 * 0.017453292f;
        this.nose.yRot = float5 * 0.017453292f;
        this.head.yRot = float5 * 0.017453292f;
        this.earRight.yRot = this.nose.yRot - 0.2617994f;
        this.earLeft.yRot = this.nose.yRot + 0.2617994f;
        this.jumpRotation = Mth.sin(arq.getJumpCompletion(float8) * 3.1415927f);
        this.haunchLeft.xRot = (this.jumpRotation * 50.0f - 21.0f) * 0.017453292f;
        this.haunchRight.xRot = (this.jumpRotation * 50.0f - 21.0f) * 0.017453292f;
        this.rearFootLeft.xRot = this.jumpRotation * 50.0f * 0.017453292f;
        this.rearFootRight.xRot = this.jumpRotation * 50.0f * 0.017453292f;
        this.frontLegLeft.xRot = (this.jumpRotation * -40.0f - 11.0f) * 0.017453292f;
        this.frontLegRight.xRot = (this.jumpRotation * -40.0f - 11.0f) * 0.017453292f;
    }
    
    @Override
    public void prepareMobModel(final T arq, final float float2, final float float3, final float float4) {
        super.prepareMobModel(arq, float2, float3, float4);
        this.jumpRotation = Mth.sin(arq.getJumpCompletion(float4) * 3.1415927f);
    }
}
