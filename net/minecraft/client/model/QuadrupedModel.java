package net.minecraft.client.model;

import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class QuadrupedModel<T extends Entity> extends EntityModel<T> {
    protected ModelPart head;
    protected ModelPart body;
    protected ModelPart leg0;
    protected ModelPart leg1;
    protected ModelPart leg2;
    protected ModelPart leg3;
    protected float yHeadOffs;
    protected float zHeadOffs;
    
    public QuadrupedModel(final int integer, final float float2) {
        this.yHeadOffs = 8.0f;
        this.zHeadOffs = 4.0f;
        (this.head = new ModelPart(this, 0, 0)).addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, float2);
        this.head.setPos(0.0f, (float)(18 - integer), -6.0f);
        (this.body = new ModelPart(this, 28, 8)).addBox(-5.0f, -10.0f, -7.0f, 10, 16, 8, float2);
        this.body.setPos(0.0f, (float)(17 - integer), 2.0f);
        (this.leg0 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg0.setPos(-3.0f, (float)(24 - integer), 7.0f);
        (this.leg1 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg1.setPos(3.0f, (float)(24 - integer), 7.0f);
        (this.leg2 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg2.setPos(-3.0f, (float)(24 - integer), -5.0f);
        (this.leg3 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg3.setPos(3.0f, (float)(24 - integer), -5.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.yHeadOffs * float7, this.zHeadOffs * float7);
            this.head.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
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
            this.head.render(float7);
            this.body.render(float7);
            this.leg0.render(float7);
            this.leg1.render(float7);
            this.leg2.render(float7);
            this.leg3.render(float7);
        }
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.xRot = float6 * 0.017453292f;
        this.head.yRot = float5 * 0.017453292f;
        this.body.xRot = 1.5707964f;
        this.leg0.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
        this.leg1.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.leg2.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.leg3.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
    }
}
