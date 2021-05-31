package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.ambient.Bat;

public class BatModel extends EntityModel<Bat> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;
    
    public BatModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        (this.head = new ModelPart(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        final ModelPart djv2 = new ModelPart(this, 24, 0);
        djv2.addBox(-4.0f, -6.0f, -2.0f, 3, 4, 1);
        this.head.addChild(djv2);
        final ModelPart djv3 = new ModelPart(this, 24, 0);
        djv3.mirror = true;
        djv3.addBox(1.0f, -6.0f, -2.0f, 3, 4, 1);
        this.head.addChild(djv3);
        (this.body = new ModelPart(this, 0, 16)).addBox(-3.0f, 4.0f, -3.0f, 6, 12, 6);
        this.body.texOffs(0, 34).addBox(-5.0f, 16.0f, 0.0f, 10, 6, 1);
        (this.rightWing = new ModelPart(this, 42, 0)).addBox(-12.0f, 1.0f, 1.5f, 10, 16, 1);
        (this.rightWingTip = new ModelPart(this, 24, 16)).setPos(-12.0f, 1.0f, 1.5f);
        this.rightWingTip.addBox(-8.0f, 1.0f, 0.0f, 8, 12, 1);
        this.leftWing = new ModelPart(this, 42, 0);
        this.leftWing.mirror = true;
        this.leftWing.addBox(2.0f, 1.0f, 1.5f, 10, 16, 1);
        this.leftWingTip = new ModelPart(this, 24, 16);
        this.leftWingTip.mirror = true;
        this.leftWingTip.setPos(12.0f, 1.0f, 1.5f);
        this.leftWingTip.addBox(0.0f, 1.0f, 0.0f, 8, 12, 1);
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.rightWingTip);
        this.leftWing.addChild(this.leftWingTip);
    }
    
    @Override
    public void render(final Bat aqv, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.render(aqv, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        this.body.render(float7);
    }
    
    @Override
    public void render(final Bat aqv, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        if (aqv.isResting()) {
            this.head.xRot = float6 * 0.017453292f;
            this.head.yRot = 3.1415927f - float5 * 0.017453292f;
            this.head.zRot = 3.1415927f;
            this.head.setPos(0.0f, -2.0f, 0.0f);
            this.rightWing.setPos(-3.0f, 0.0f, 3.0f);
            this.leftWing.setPos(3.0f, 0.0f, 3.0f);
            this.body.xRot = 3.1415927f;
            this.rightWing.xRot = -0.15707964f;
            this.rightWing.yRot = -1.2566371f;
            this.rightWingTip.yRot = -1.7278761f;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.leftWingTip.yRot = -this.rightWingTip.yRot;
        }
        else {
            this.head.xRot = float6 * 0.017453292f;
            this.head.yRot = float5 * 0.017453292f;
            this.head.zRot = 0.0f;
            this.head.setPos(0.0f, 0.0f, 0.0f);
            this.rightWing.setPos(0.0f, 0.0f, 0.0f);
            this.leftWing.setPos(0.0f, 0.0f, 0.0f);
            this.body.xRot = 0.7853982f + Mth.cos(float4 * 0.1f) * 0.15f;
            this.body.yRot = 0.0f;
            this.rightWing.yRot = Mth.cos(float4 * 1.3f) * 3.1415927f * 0.25f;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.rightWingTip.yRot = this.rightWing.yRot * 0.5f;
            this.leftWingTip.yRot = -this.rightWing.yRot * 0.5f;
        }
    }
}
