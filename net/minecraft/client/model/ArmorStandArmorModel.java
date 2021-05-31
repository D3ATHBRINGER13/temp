package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandArmorModel extends HumanoidModel<ArmorStand> {
    public ArmorStandArmorModel() {
        this(0.0f);
    }
    
    public ArmorStandArmorModel(final float float1) {
        this(float1, 64, 32);
    }
    
    protected ArmorStandArmorModel(final float float1, final int integer2, final int integer3) {
        super(float1, 0.0f, integer2, integer3);
    }
    
    @Override
    public void setupAnim(final ArmorStand atl, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.xRot = 0.017453292f * atl.getHeadPose().getX();
        this.head.yRot = 0.017453292f * atl.getHeadPose().getY();
        this.head.zRot = 0.017453292f * atl.getHeadPose().getZ();
        this.head.setPos(0.0f, 1.0f, 0.0f);
        this.body.xRot = 0.017453292f * atl.getBodyPose().getX();
        this.body.yRot = 0.017453292f * atl.getBodyPose().getY();
        this.body.zRot = 0.017453292f * atl.getBodyPose().getZ();
        this.leftArm.xRot = 0.017453292f * atl.getLeftArmPose().getX();
        this.leftArm.yRot = 0.017453292f * atl.getLeftArmPose().getY();
        this.leftArm.zRot = 0.017453292f * atl.getLeftArmPose().getZ();
        this.rightArm.xRot = 0.017453292f * atl.getRightArmPose().getX();
        this.rightArm.yRot = 0.017453292f * atl.getRightArmPose().getY();
        this.rightArm.zRot = 0.017453292f * atl.getRightArmPose().getZ();
        this.leftLeg.xRot = 0.017453292f * atl.getLeftLegPose().getX();
        this.leftLeg.yRot = 0.017453292f * atl.getLeftLegPose().getY();
        this.leftLeg.zRot = 0.017453292f * atl.getLeftLegPose().getZ();
        this.leftLeg.setPos(1.9f, 11.0f, 0.0f);
        this.rightLeg.xRot = 0.017453292f * atl.getRightLegPose().getX();
        this.rightLeg.yRot = 0.017453292f * atl.getRightLegPose().getY();
        this.rightLeg.zRot = 0.017453292f * atl.getRightLegPose().getZ();
        this.rightLeg.setPos(-1.9f, 11.0f, 0.0f);
        this.hat.copyFrom(this.head);
    }
}
