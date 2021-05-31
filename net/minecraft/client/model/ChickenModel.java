package net.minecraft.client.model;

import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ChickenModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart wing0;
    private final ModelPart wing1;
    private final ModelPart beak;
    private final ModelPart redThing;
    
    public ChickenModel() {
        final int integer2 = 16;
        (this.head = new ModelPart(this, 0, 0)).addBox(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setPos(0.0f, 15.0f, -4.0f);
        (this.beak = new ModelPart(this, 14, 0)).addBox(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.beak.setPos(0.0f, 15.0f, -4.0f);
        (this.redThing = new ModelPart(this, 14, 4)).addBox(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.redThing.setPos(0.0f, 15.0f, -4.0f);
        (this.body = new ModelPart(this, 0, 9)).addBox(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.body.setPos(0.0f, 16.0f, 0.0f);
        (this.leg0 = new ModelPart(this, 26, 0)).addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.leg0.setPos(-2.0f, 19.0f, 1.0f);
        (this.leg1 = new ModelPart(this, 26, 0)).addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.leg1.setPos(1.0f, 19.0f, 1.0f);
        (this.wing0 = new ModelPart(this, 24, 13)).addBox(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.wing0.setPos(-4.0f, 13.0f, 0.0f);
        (this.wing1 = new ModelPart(this, 24, 13)).addBox(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.wing1.setPos(4.0f, 13.0f, 0.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        if (this.young) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 5.0f * float7, 2.0f * float7);
            this.head.render(float7);
            this.beak.render(float7);
            this.redThing.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * float7, 0.0f);
            this.body.render(float7);
            this.leg0.render(float7);
            this.leg1.render(float7);
            this.wing0.render(float7);
            this.wing1.render(float7);
            GlStateManager.popMatrix();
        }
        else {
            this.head.render(float7);
            this.beak.render(float7);
            this.redThing.render(float7);
            this.body.render(float7);
            this.leg0.render(float7);
            this.leg1.render(float7);
            this.wing0.render(float7);
            this.wing1.render(float7);
        }
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.xRot = float6 * 0.017453292f;
        this.head.yRot = float5 * 0.017453292f;
        this.beak.xRot = this.head.xRot;
        this.beak.yRot = this.head.yRot;
        this.redThing.xRot = this.head.xRot;
        this.redThing.yRot = this.head.yRot;
        this.body.xRot = 1.5707964f;
        this.leg0.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
        this.leg1.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.wing0.zRot = float4;
        this.wing1.zRot = -float4;
    }
}
