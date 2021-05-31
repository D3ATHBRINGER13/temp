package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Fox;

public class FoxModel<T extends Fox> extends EntityModel<T> {
    public final ModelPart head;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart tail;
    private float legMotionPos;
    
    public FoxModel() {
        this.texWidth = 48;
        this.texHeight = 32;
        (this.head = new ModelPart(this, 1, 5)).addBox(-3.0f, -2.0f, -5.0f, 8, 6, 6);
        this.head.setPos(-1.0f, 16.5f, -3.0f);
        (this.earL = new ModelPart(this, 8, 1)).addBox(-3.0f, -4.0f, -4.0f, 2, 2, 1);
        (this.earR = new ModelPart(this, 15, 1)).addBox(3.0f, -4.0f, -4.0f, 2, 2, 1);
        (this.nose = new ModelPart(this, 6, 18)).addBox(-1.0f, 2.01f, -8.0f, 4, 2, 3);
        this.head.addChild(this.earL);
        this.head.addChild(this.earR);
        this.head.addChild(this.nose);
        (this.body = new ModelPart(this, 24, 15)).addBox(-3.0f, 3.999f, -3.5f, 6, 11, 6);
        this.body.setPos(0.0f, 16.0f, -6.0f);
        final float float2 = 0.001f;
        (this.leg0 = new ModelPart(this, 13, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.leg0.setPos(-5.0f, 17.5f, 7.0f);
        (this.leg1 = new ModelPart(this, 4, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.leg1.setPos(-1.0f, 17.5f, 7.0f);
        (this.leg2 = new ModelPart(this, 13, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.leg2.setPos(-5.0f, 17.5f, 0.0f);
        (this.leg3 = new ModelPart(this, 4, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.leg3.setPos(-1.0f, 17.5f, 0.0f);
        (this.tail = new ModelPart(this, 30, 0)).addBox(2.0f, 0.0f, -1.0f, 4, 9, 5);
        this.tail.setPos(-4.0f, 15.0f, -1.0f);
        this.body.addChild(this.tail);
    }
    
    @Override
    public void prepareMobModel(final T arh, final float float2, final float float3, final float float4) {
        this.body.xRot = 1.5707964f;
        this.tail.xRot = -0.05235988f;
        this.leg0.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
        this.leg1.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.leg2.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.leg3.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
        this.head.setPos(-1.0f, 16.5f, -3.0f);
        this.head.yRot = 0.0f;
        this.head.zRot = arh.getHeadRollAngle(float4);
        this.leg0.visible = true;
        this.leg1.visible = true;
        this.leg2.visible = true;
        this.leg3.visible = true;
        this.body.setPos(0.0f, 16.0f, -6.0f);
        this.body.zRot = 0.0f;
        this.leg0.setPos(-5.0f, 17.5f, 7.0f);
        this.leg1.setPos(-1.0f, 17.5f, 7.0f);
        if (arh.isCrouching()) {
            this.body.xRot = 1.6755161f;
            final float float5 = arh.getCrouchAmount(float4);
            this.body.setPos(0.0f, 16.0f + arh.getCrouchAmount(float4), -6.0f);
            this.head.setPos(-1.0f, 16.5f + float5, -3.0f);
            this.head.yRot = 0.0f;
        }
        else if (arh.isSleeping()) {
            this.body.zRot = -1.5707964f;
            this.body.setPos(0.0f, 21.0f, -6.0f);
            this.tail.xRot = -2.6179938f;
            if (this.young) {
                this.tail.xRot = -2.1816616f;
                this.body.setPos(0.0f, 21.0f, -2.0f);
            }
            this.head.setPos(1.0f, 19.49f, -3.0f);
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = 0.0f;
            this.leg0.visible = false;
            this.leg1.visible = false;
            this.leg2.visible = false;
            this.leg3.visible = false;
        }
        else if (arh.isSitting()) {
            this.body.xRot = 0.5235988f;
            this.body.setPos(0.0f, 9.0f, -3.0f);
            this.tail.xRot = 0.7853982f;
            this.tail.setPos(-4.0f, 15.0f, -2.0f);
            this.head.setPos(-1.0f, 10.0f, -0.25f);
            this.head.xRot = 0.0f;
            this.head.yRot = 0.0f;
            if (this.young) {
                this.head.setPos(-1.0f, 13.0f, -3.75f);
            }
            this.leg0.xRot = -1.3089969f;
            this.leg0.setPos(-5.0f, 21.5f, 6.75f);
            this.leg1.xRot = -1.3089969f;
            this.leg1.setPos(-1.0f, 21.5f, 6.75f);
            this.leg2.xRot = -0.2617994f;
            this.leg3.xRot = -0.2617994f;
        }
    }
    
    @Override
    public void setupAnim(final T arh, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.render(arh, float2, float3, float4, float5, float6, float7);
        this.setupAnim(arh, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            GlStateManager.pushMatrix();
            final float float8 = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 8.0f * float7, 3.35f * float7);
            this.head.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float9 = 0.5f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * float7, 0.0f);
            this.body.render(float7);
            this.leg0.render(float7);
            this.leg1.render(float7);
            this.leg2.render(float7);
            this.leg3.render(float7);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.pushMatrix();
            this.head.render(float7);
            this.body.render(float7);
            this.leg0.render(float7);
            this.leg1.render(float7);
            this.leg2.render(float7);
            this.leg3.render(float7);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void setupAnim(final T arh, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(arh, float2, float3, float4, float5, float6, float7);
        if (!arh.isSleeping() && !arh.isFaceplanted() && !arh.isCrouching()) {
            this.head.xRot = float6 * 0.017453292f;
            this.head.yRot = float5 * 0.017453292f;
        }
        if (arh.isSleeping()) {
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = Mth.cos(float4 * 0.027f) / 22.0f;
        }
        if (arh.isCrouching()) {
            final float float8 = Mth.cos(float4) * 0.01f;
            this.body.yRot = float8;
            this.leg0.zRot = float8;
            this.leg1.zRot = float8;
            this.leg2.zRot = float8 / 2.0f;
            this.leg3.zRot = float8 / 2.0f;
        }
        if (arh.isFaceplanted()) {
            final float float8 = 0.1f;
            this.legMotionPos += 0.67f;
            this.leg0.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
            this.leg1.xRot = Mth.cos(this.legMotionPos * 0.4662f + 3.1415927f) * 0.1f;
            this.leg2.xRot = Mth.cos(this.legMotionPos * 0.4662f + 3.1415927f) * 0.1f;
            this.leg3.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
        }
    }
}
