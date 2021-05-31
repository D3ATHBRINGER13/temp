package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleModel<T extends Turtle> extends QuadrupedModel<T> {
    private final ModelPart eggBelly;
    
    public TurtleModel(final float float1) {
        super(12, float1);
        this.texWidth = 128;
        this.texHeight = 64;
        (this.head = new ModelPart(this, 3, 0)).addBox(-3.0f, -1.0f, -3.0f, 6, 5, 6, 0.0f);
        this.head.setPos(0.0f, 19.0f, -10.0f);
        this.body = new ModelPart(this);
        this.body.texOffs(7, 37).addBox(-9.5f, 3.0f, -10.0f, 19, 20, 6, 0.0f);
        this.body.texOffs(31, 1).addBox(-5.5f, 3.0f, -13.0f, 11, 18, 3, 0.0f);
        this.body.setPos(0.0f, 11.0f, -10.0f);
        this.eggBelly = new ModelPart(this);
        this.eggBelly.texOffs(70, 33).addBox(-4.5f, 3.0f, -14.0f, 9, 18, 1, 0.0f);
        this.eggBelly.setPos(0.0f, 11.0f, -10.0f);
        final int integer3 = 1;
        (this.leg0 = new ModelPart(this, 1, 23)).addBox(-2.0f, 0.0f, 0.0f, 4, 1, 10, 0.0f);
        this.leg0.setPos(-3.5f, 22.0f, 11.0f);
        (this.leg1 = new ModelPart(this, 1, 12)).addBox(-2.0f, 0.0f, 0.0f, 4, 1, 10, 0.0f);
        this.leg1.setPos(3.5f, 22.0f, 11.0f);
        (this.leg2 = new ModelPart(this, 27, 30)).addBox(-13.0f, 0.0f, -2.0f, 13, 1, 5, 0.0f);
        this.leg2.setPos(-5.0f, 21.0f, -4.0f);
        (this.leg3 = new ModelPart(this, 27, 24)).addBox(0.0f, 0.0f, -2.0f, 13, 1, 5, 0.0f);
        this.leg3.setPos(5.0f, 21.0f, -4.0f);
    }
    
    @Override
    public void setupAnim(final T arx, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(arx, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            final float float8 = 6.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.16666667f, 0.16666667f, 0.16666667f);
            GlStateManager.translatef(0.0f, 120.0f * float7, 0.0f);
            this.head.render(float7);
            this.body.render(float7);
            this.leg0.render(float7);
            this.leg1.render(float7);
            this.leg2.render(float7);
            this.leg3.render(float7);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.pushMatrix();
            if (arx.hasEgg()) {
                GlStateManager.translatef(0.0f, -0.08f, 0.0f);
            }
            this.head.render(float7);
            this.body.render(float7);
            GlStateManager.pushMatrix();
            this.leg0.render(float7);
            this.leg1.render(float7);
            GlStateManager.popMatrix();
            this.leg2.render(float7);
            this.leg3.render(float7);
            if (arx.hasEgg()) {
                this.eggBelly.render(float7);
            }
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void setupAnim(final T arx, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(arx, float2, float3, float4, float5, float6, float7);
        this.leg0.xRot = Mth.cos(float2 * 0.6662f * 0.6f) * 0.5f * float3;
        this.leg1.xRot = Mth.cos(float2 * 0.6662f * 0.6f + 3.1415927f) * 0.5f * float3;
        this.leg2.zRot = Mth.cos(float2 * 0.6662f * 0.6f + 3.1415927f) * 0.5f * float3;
        this.leg3.zRot = Mth.cos(float2 * 0.6662f * 0.6f) * 0.5f * float3;
        this.leg2.xRot = 0.0f;
        this.leg3.xRot = 0.0f;
        this.leg2.yRot = 0.0f;
        this.leg3.yRot = 0.0f;
        this.leg0.yRot = 0.0f;
        this.leg1.yRot = 0.0f;
        this.eggBelly.xRot = 1.5707964f;
        if (!arx.isInWater() && arx.onGround) {
            final float float8 = arx.isLayingEgg() ? 4.0f : 1.0f;
            final float float9 = arx.isLayingEgg() ? 2.0f : 1.0f;
            final float float10 = 5.0f;
            this.leg2.yRot = Mth.cos(float8 * float2 * 5.0f + 3.1415927f) * 8.0f * float3 * float9;
            this.leg2.zRot = 0.0f;
            this.leg3.yRot = Mth.cos(float8 * float2 * 5.0f) * 8.0f * float3 * float9;
            this.leg3.zRot = 0.0f;
            this.leg0.yRot = Mth.cos(float2 * 5.0f + 3.1415927f) * 3.0f * float3;
            this.leg0.xRot = 0.0f;
            this.leg1.yRot = Mth.cos(float2 * 5.0f) * 3.0f * float3;
            this.leg1.xRot = 0.0f;
        }
    }
}
