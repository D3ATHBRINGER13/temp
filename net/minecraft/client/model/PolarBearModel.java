package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearModel<T extends PolarBear> extends QuadrupedModel<T> {
    public PolarBearModel() {
        super(12, 0.0f);
        this.texWidth = 128;
        this.texHeight = 64;
        (this.head = new ModelPart(this, 0, 0)).addBox(-3.5f, -3.0f, -3.0f, 7, 7, 7, 0.0f);
        this.head.setPos(0.0f, 10.0f, -16.0f);
        this.head.texOffs(0, 44).addBox(-2.5f, 1.0f, -6.0f, 5, 3, 3, 0.0f);
        this.head.texOffs(26, 0).addBox(-4.5f, -4.0f, -1.0f, 2, 2, 1, 0.0f);
        final ModelPart djv2 = this.head.texOffs(26, 0);
        djv2.mirror = true;
        djv2.addBox(2.5f, -4.0f, -1.0f, 2, 2, 1, 0.0f);
        this.body = new ModelPart(this);
        this.body.texOffs(0, 19).addBox(-5.0f, -13.0f, -7.0f, 14, 14, 11, 0.0f);
        this.body.texOffs(39, 0).addBox(-4.0f, -25.0f, -7.0f, 12, 12, 10, 0.0f);
        this.body.setPos(-2.0f, 9.0f, 12.0f);
        final int integer3 = 10;
        (this.leg0 = new ModelPart(this, 50, 22)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 8, 0.0f);
        this.leg0.setPos(-3.5f, 14.0f, 6.0f);
        (this.leg1 = new ModelPart(this, 50, 22)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 8, 0.0f);
        this.leg1.setPos(3.5f, 14.0f, 6.0f);
        (this.leg2 = new ModelPart(this, 50, 40)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 6, 0.0f);
        this.leg2.setPos(-2.5f, 14.0f, -7.0f);
        (this.leg3 = new ModelPart(this, 50, 40)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 6, 0.0f);
        this.leg3.setPos(2.5f, 14.0f, -7.0f);
        final ModelPart leg0 = this.leg0;
        --leg0.x;
        final ModelPart leg2 = this.leg1;
        ++leg2.x;
        final ModelPart leg3 = this.leg0;
        leg3.z += 0.0f;
        final ModelPart leg4 = this.leg1;
        leg4.z += 0.0f;
        final ModelPart leg5 = this.leg2;
        --leg5.x;
        final ModelPart leg6 = this.leg3;
        ++leg6.x;
        final ModelPart leg7 = this.leg2;
        --leg7.z;
        final ModelPart leg8 = this.leg3;
        --leg8.z;
        this.zHeadOffs += 2.0f;
    }
    
    @Override
    public void setupAnim(final T aro, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aro, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            final float float8 = 2.0f;
            this.yHeadOffs = 16.0f;
            this.zHeadOffs = 4.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6666667f, 0.6666667f, 0.6666667f);
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
    public void setupAnim(final T aro, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aro, float2, float3, float4, float5, float6, float7);
        final float float8 = float4 - aro.tickCount;
        float float9 = aro.getStandingAnimationScale(float8);
        float9 *= float9;
        final float float10 = 1.0f - float9;
        this.body.xRot = 1.5707964f - float9 * 3.1415927f * 0.35f;
        this.body.y = 9.0f * float10 + 11.0f * float9;
        this.leg2.y = 14.0f * float10 - 6.0f * float9;
        this.leg2.z = -8.0f * float10 - 4.0f * float9;
        final ModelPart leg2 = this.leg2;
        leg2.xRot -= float9 * 3.1415927f * 0.45f;
        this.leg3.y = this.leg2.y;
        this.leg3.z = this.leg2.z;
        final ModelPart leg3 = this.leg3;
        leg3.xRot -= float9 * 3.1415927f * 0.45f;
        if (this.young) {
            this.head.y = 10.0f * float10 - 9.0f * float9;
            this.head.z = -16.0f * float10 - 7.0f * float9;
        }
        else {
            this.head.y = 10.0f * float10 - 14.0f * float9;
            this.head.z = -16.0f * float10 - 3.0f * float9;
        }
        final ModelPart head = this.head;
        head.xRot += float9 * 3.1415927f * 0.15f;
    }
}
