package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class HorseModel<T extends AbstractHorse> extends EntityModel<T> {
    protected final ModelPart body;
    protected final ModelPart headParts;
    private final ModelPart leg1A;
    private final ModelPart leg2A;
    private final ModelPart leg3A;
    private final ModelPart leg4A;
    private final ModelPart tail;
    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;
    
    public HorseModel(final float float1) {
        this.texWidth = 64;
        this.texHeight = 64;
        (this.body = new ModelPart(this, 0, 32)).addBox(-5.0f, -8.0f, -17.0f, 10, 10, 22, 0.05f);
        this.body.setPos(0.0f, 11.0f, 5.0f);
        (this.headParts = new ModelPart(this, 0, 35)).addBox(-2.05f, -6.0f, -2.0f, 4, 12, 7);
        this.headParts.xRot = 0.5235988f;
        final ModelPart djv3 = new ModelPart(this, 0, 13);
        djv3.addBox(-3.0f, -11.0f, -2.0f, 6, 5, 7, float1);
        final ModelPart djv4 = new ModelPart(this, 56, 36);
        djv4.addBox(-1.0f, -11.0f, 5.01f, 2, 16, 2, float1);
        final ModelPart djv5 = new ModelPart(this, 0, 25);
        djv5.addBox(-2.0f, -11.0f, -7.0f, 4, 5, 5, float1);
        this.headParts.addChild(djv3);
        this.headParts.addChild(djv4);
        this.headParts.addChild(djv5);
        this.addEarModels(this.headParts);
        this.leg1A = new ModelPart(this, 48, 21);
        this.leg1A.mirror = true;
        this.leg1A.addBox(-3.0f, -1.01f, -1.0f, 4, 11, 4, float1);
        this.leg1A.setPos(4.0f, 14.0f, 7.0f);
        (this.leg2A = new ModelPart(this, 48, 21)).addBox(-1.0f, -1.01f, -1.0f, 4, 11, 4, float1);
        this.leg2A.setPos(-4.0f, 14.0f, 7.0f);
        this.leg3A = new ModelPart(this, 48, 21);
        this.leg3A.mirror = true;
        this.leg3A.addBox(-3.0f, -1.01f, -1.9f, 4, 11, 4, float1);
        this.leg3A.setPos(4.0f, 6.0f, -12.0f);
        (this.leg4A = new ModelPart(this, 48, 21)).addBox(-1.0f, -1.01f, -1.9f, 4, 11, 4, float1);
        this.leg4A.setPos(-4.0f, 6.0f, -12.0f);
        (this.tail = new ModelPart(this, 42, 36)).addBox(-1.5f, 0.0f, 0.0f, 3, 14, 4, float1);
        this.tail.setPos(0.0f, -5.0f, 2.0f);
        this.tail.xRot = 0.5235988f;
        this.body.addChild(this.tail);
        final ModelPart djv6 = new ModelPart(this, 26, 0);
        djv6.addBox(-5.0f, -8.0f, -9.0f, 10, 9, 9, 0.5f);
        this.body.addChild(djv6);
        final ModelPart djv7 = new ModelPart(this, 29, 5);
        djv7.addBox(2.0f, -9.0f, -6.0f, 1, 2, 2, float1);
        this.headParts.addChild(djv7);
        final ModelPart djv8 = new ModelPart(this, 29, 5);
        djv8.addBox(-3.0f, -9.0f, -6.0f, 1, 2, 2, float1);
        this.headParts.addChild(djv8);
        final ModelPart djv9 = new ModelPart(this, 32, 2);
        djv9.addBox(3.1f, -6.0f, -8.0f, 0, 3, 16, float1);
        djv9.xRot = -0.5235988f;
        this.headParts.addChild(djv9);
        final ModelPart djv10 = new ModelPart(this, 32, 2);
        djv10.addBox(-3.1f, -6.0f, -8.0f, 0, 3, 16, float1);
        djv10.xRot = -0.5235988f;
        this.headParts.addChild(djv10);
        final ModelPart djv11 = new ModelPart(this, 1, 1);
        djv11.addBox(-3.0f, -11.0f, -1.9f, 6, 5, 6, 0.2f);
        this.headParts.addChild(djv11);
        final ModelPart djv12 = new ModelPart(this, 19, 0);
        djv12.addBox(-2.0f, -11.0f, -4.0f, 4, 5, 2, 0.2f);
        this.headParts.addChild(djv12);
        this.saddleParts = new ModelPart[] { djv6, djv7, djv8, djv11, djv12 };
        this.ridingParts = new ModelPart[] { djv9, djv10 };
    }
    
    protected void addEarModels(final ModelPart djv) {
        final ModelPart djv2 = new ModelPart(this, 19, 16);
        djv2.addBox(0.55f, -13.0f, 4.0f, 2, 3, 1, -0.001f);
        final ModelPart djv3 = new ModelPart(this, 19, 16);
        djv3.addBox(-2.55f, -13.0f, 4.0f, 2, 3, 1, -0.001f);
        djv.addChild(djv2);
        djv.addChild(djv3);
    }
    
    @Override
    public void render(final T asb, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final boolean boolean9 = asb.isBaby();
        final float float8 = asb.getScale();
        final boolean boolean10 = asb.isSaddled();
        final boolean boolean11 = asb.isVehicle();
        for (final ModelPart djv16 : this.saddleParts) {
            djv16.visible = boolean10;
        }
        for (final ModelPart djv16 : this.ridingParts) {
            djv16.visible = (boolean11 && boolean10);
        }
        if (boolean9) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(float8, 0.5f + float8 * 0.5f, float8);
            GlStateManager.translatef(0.0f, 0.95f * (1.0f - float8), 0.0f);
        }
        this.leg1A.render(float7);
        this.leg2A.render(float7);
        this.leg3A.render(float7);
        this.leg4A.render(float7);
        if (boolean9) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(float8, float8, float8);
            GlStateManager.translatef(0.0f, 2.3f * (1.0f - float8), 0.0f);
        }
        this.body.render(float7);
        if (boolean9) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float9 = float8 + 0.1f * float8;
            GlStateManager.scalef(float9, float9, float9);
            GlStateManager.translatef(0.0f, 2.25f * (1.0f - float9), 0.1f * (1.4f - float9));
        }
        this.headParts.render(float7);
        if (boolean9) {
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void prepareMobModel(final T asb, final float float2, final float float3, final float float4) {
        super.prepareMobModel(asb, float2, float3, float4);
        final float float5 = this.rotlerp(asb.yBodyRotO, asb.yBodyRot, float4);
        final float float6 = this.rotlerp(asb.yHeadRotO, asb.yHeadRot, float4);
        final float float7 = Mth.lerp(float4, asb.xRotO, asb.xRot);
        float float8 = float6 - float5;
        float float9 = float7 * 0.017453292f;
        if (float8 > 20.0f) {
            float8 = 20.0f;
        }
        if (float8 < -20.0f) {
            float8 = -20.0f;
        }
        if (float3 > 0.2f) {
            float9 += Mth.cos(float2 * 0.4f) * 0.15f * float3;
        }
        final float float10 = asb.getEatAnim(float4);
        final float float11 = asb.getStandAnim(float4);
        final float float12 = 1.0f - float11;
        final float float13 = asb.getMouthAnim(float4);
        final boolean boolean15 = asb.tailCounter != 0;
        final float float14 = asb.tickCount + float4;
        this.headParts.y = 4.0f;
        this.headParts.z = -12.0f;
        this.body.xRot = 0.0f;
        this.headParts.xRot = 0.5235988f + float9;
        this.headParts.yRot = float8 * 0.017453292f;
        final float float15 = asb.isInWater() ? 0.2f : 1.0f;
        final float float16 = Mth.cos(float15 * float2 * 0.6662f + 3.1415927f);
        final float float17 = float16 * 0.8f * float3;
        final float float18 = (1.0f - Math.max(float11, float10)) * (0.5235988f + float9 + float13 * Mth.sin(float14) * 0.05f);
        this.headParts.xRot = float11 * (0.2617994f + float9) + float10 * (2.1816616f + Mth.sin(float14) * 0.05f) + float18;
        this.headParts.yRot = float11 * float8 * 0.017453292f + (1.0f - Math.max(float11, float10)) * this.headParts.yRot;
        this.headParts.y = float11 * -4.0f + float10 * 11.0f + (1.0f - Math.max(float11, float10)) * this.headParts.y;
        this.headParts.z = float11 * -4.0f + float10 * -12.0f + (1.0f - Math.max(float11, float10)) * this.headParts.z;
        this.body.xRot = float11 * -0.7853982f + float12 * this.body.xRot;
        final float float19 = 0.2617994f * float11;
        final float float20 = Mth.cos(float14 * 0.6f + 3.1415927f);
        this.leg3A.y = 2.0f * float11 + 14.0f * float12;
        this.leg3A.z = -6.0f * float11 - 10.0f * float12;
        this.leg4A.y = this.leg3A.y;
        this.leg4A.z = this.leg3A.z;
        final float float21 = (-1.0471976f + float20) * float11 + float17 * float12;
        final float float22 = (-1.0471976f - float20) * float11 - float17 * float12;
        this.leg1A.xRot = float19 - float16 * 0.5f * float3 * float12;
        this.leg2A.xRot = float19 + float16 * 0.5f * float3 * float12;
        this.leg3A.xRot = float21;
        this.leg4A.xRot = float22;
        this.tail.xRot = 0.5235988f + float3 * 0.75f;
        this.tail.y = -5.0f + float3;
        this.tail.z = 2.0f + float3 * 2.0f;
        if (boolean15) {
            this.tail.yRot = Mth.cos(float14 * 0.7f);
        }
        else {
            this.tail.yRot = 0.0f;
        }
    }
    
    private float rotlerp(final float float1, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - float1; float4 < -180.0f; float4 += 360.0f) {}
        while (float4 >= 180.0f) {
            float4 -= 360.0f;
        }
        return float1 + float3 * float4;
    }
}
