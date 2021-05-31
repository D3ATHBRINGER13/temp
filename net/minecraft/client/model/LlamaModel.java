package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class LlamaModel<T extends AbstractChestedHorse> extends QuadrupedModel<T> {
    private final ModelPart chest1;
    private final ModelPart chest2;
    
    public LlamaModel(final float float1) {
        super(15, float1);
        this.texWidth = 128;
        this.texHeight = 64;
        (this.head = new ModelPart(this, 0, 0)).addBox(-2.0f, -14.0f, -10.0f, 4, 4, 9, float1);
        this.head.setPos(0.0f, 7.0f, -6.0f);
        this.head.texOffs(0, 14).addBox(-4.0f, -16.0f, -6.0f, 8, 18, 6, float1);
        this.head.texOffs(17, 0).addBox(-4.0f, -19.0f, -4.0f, 3, 3, 2, float1);
        this.head.texOffs(17, 0).addBox(1.0f, -19.0f, -4.0f, 3, 3, 2, float1);
        (this.body = new ModelPart(this, 29, 0)).addBox(-6.0f, -10.0f, -7.0f, 12, 18, 10, float1);
        this.body.setPos(0.0f, 5.0f, 2.0f);
        (this.chest1 = new ModelPart(this, 45, 28)).addBox(-3.0f, 0.0f, 0.0f, 8, 8, 3, float1);
        this.chest1.setPos(-8.5f, 3.0f, 3.0f);
        this.chest1.yRot = 1.5707964f;
        (this.chest2 = new ModelPart(this, 45, 41)).addBox(-3.0f, 0.0f, 0.0f, 8, 8, 3, float1);
        this.chest2.setPos(5.5f, 3.0f, 3.0f);
        this.chest2.yRot = 1.5707964f;
        final int integer3 = 4;
        final int integer4 = 14;
        (this.leg0 = new ModelPart(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg0.setPos(-2.5f, 10.0f, 6.0f);
        (this.leg1 = new ModelPart(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg1.setPos(2.5f, 10.0f, 6.0f);
        (this.leg2 = new ModelPart(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg2.setPos(-2.5f, 10.0f, -4.0f);
        (this.leg3 = new ModelPart(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg3.setPos(2.5f, 10.0f, -4.0f);
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
    public void render(final T asa, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final boolean boolean9 = !asa.isBaby() && asa.hasChest();
        this.setupAnim(asa, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.yHeadOffs * float7, this.zHeadOffs * float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float9 = 0.7f;
            GlStateManager.scalef(0.71428573f, 0.64935064f, 0.7936508f);
            GlStateManager.translatef(0.0f, 21.0f * float7, 0.22f);
            this.head.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float10 = 1.1f;
            GlStateManager.scalef(0.625f, 0.45454544f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * float7, 0.0f);
            this.body.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.45454544f, 0.41322312f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * float7, 0.0f);
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
        if (boolean9) {
            this.chest1.render(float7);
            this.chest2.render(float7);
        }
    }
}
