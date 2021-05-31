package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.client.model.geom.ModelPart;

public class ArmorStandModel extends ArmorStandArmorModel {
    private final ModelPart bodyStick1;
    private final ModelPart bodyStick2;
    private final ModelPart shoulderStick;
    private final ModelPart basePlate;
    
    public ArmorStandModel() {
        this(0.0f);
    }
    
    public ArmorStandModel(final float float1) {
        super(float1, 64, 64);
        (this.head = new ModelPart(this, 0, 0)).addBox(-1.0f, -7.0f, -1.0f, 2, 7, 2, float1);
        this.head.setPos(0.0f, 0.0f, 0.0f);
        (this.body = new ModelPart(this, 0, 26)).addBox(-6.0f, 0.0f, -1.5f, 12, 3, 3, float1);
        this.body.setPos(0.0f, 0.0f, 0.0f);
        (this.rightArm = new ModelPart(this, 24, 0)).addBox(-2.0f, -2.0f, -1.0f, 2, 12, 2, float1);
        this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addBox(0.0f, -2.0f, -1.0f, 2, 12, 2, float1);
        this.leftArm.setPos(5.0f, 2.0f, 0.0f);
        (this.rightLeg = new ModelPart(this, 8, 0)).addBox(-1.0f, 0.0f, -1.0f, 2, 11, 2, float1);
        this.rightLeg.setPos(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 11, 2, float1);
        this.leftLeg.setPos(1.9f, 12.0f, 0.0f);
        (this.bodyStick1 = new ModelPart(this, 16, 0)).addBox(-3.0f, 3.0f, -1.0f, 2, 7, 2, float1);
        this.bodyStick1.setPos(0.0f, 0.0f, 0.0f);
        this.bodyStick1.visible = true;
        (this.bodyStick2 = new ModelPart(this, 48, 16)).addBox(1.0f, 3.0f, -1.0f, 2, 7, 2, float1);
        this.bodyStick2.setPos(0.0f, 0.0f, 0.0f);
        (this.shoulderStick = new ModelPart(this, 0, 48)).addBox(-4.0f, 10.0f, -1.0f, 8, 2, 2, float1);
        this.shoulderStick.setPos(0.0f, 0.0f, 0.0f);
        (this.basePlate = new ModelPart(this, 0, 32)).addBox(-6.0f, 11.0f, -6.0f, 12, 1, 12, float1);
        this.basePlate.setPos(0.0f, 12.0f, 0.0f);
        this.hat.visible = false;
    }
    
    @Override
    public void setupAnim(final ArmorStand atl, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(atl, float2, float3, float4, float5, float6, float7);
        this.leftArm.visible = atl.isShowArms();
        this.rightArm.visible = atl.isShowArms();
        this.basePlate.visible = !atl.isNoBasePlate();
        this.leftLeg.setPos(1.9f, 12.0f, 0.0f);
        this.rightLeg.setPos(-1.9f, 12.0f, 0.0f);
        this.bodyStick1.xRot = 0.017453292f * atl.getBodyPose().getX();
        this.bodyStick1.yRot = 0.017453292f * atl.getBodyPose().getY();
        this.bodyStick1.zRot = 0.017453292f * atl.getBodyPose().getZ();
        this.bodyStick2.xRot = 0.017453292f * atl.getBodyPose().getX();
        this.bodyStick2.yRot = 0.017453292f * atl.getBodyPose().getY();
        this.bodyStick2.zRot = 0.017453292f * atl.getBodyPose().getZ();
        this.shoulderStick.xRot = 0.017453292f * atl.getBodyPose().getX();
        this.shoulderStick.yRot = 0.017453292f * atl.getBodyPose().getY();
        this.shoulderStick.zRot = 0.017453292f * atl.getBodyPose().getZ();
        this.basePlate.xRot = 0.0f;
        this.basePlate.yRot = 0.017453292f * -atl.yRot;
        this.basePlate.zRot = 0.0f;
    }
    
    @Override
    public void render(final ArmorStand atl, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.render(atl, float2, float3, float4, float5, float6, float7);
        GlStateManager.pushMatrix();
        if (this.young) {
            final float float8 = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * float7, 0.0f);
            this.bodyStick1.render(float7);
            this.bodyStick2.render(float7);
            this.shoulderStick.render(float7);
            this.basePlate.render(float7);
        }
        else {
            if (atl.isSneaking()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.bodyStick1.render(float7);
            this.bodyStick2.render(float7);
            this.shoulderStick.render(float7);
            this.basePlate.render(float7);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public void translateToHand(final float float1, final HumanoidArm aiw) {
        final ModelPart djv4 = this.getArm(aiw);
        final boolean boolean5 = djv4.visible;
        djv4.visible = true;
        super.translateToHand(float1, aiw);
        djv4.visible = boolean5;
    }
}
