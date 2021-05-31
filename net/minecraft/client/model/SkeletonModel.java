package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.model.geom.ModelPart;

public class SkeletonModel<T extends Mob> extends HumanoidModel<T> {
    public SkeletonModel() {
        this(0.0f, false);
    }
    
    public SkeletonModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, 32);
        if (!boolean2) {
            (this.rightArm = new ModelPart(this, 40, 16)).addBox(-1.0f, -2.0f, -1.0f, 2, 12, 2, float1);
            this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 40, 16);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0f, -2.0f, -1.0f, 2, 12, 2, float1);
            this.leftArm.setPos(5.0f, 2.0f, 0.0f);
            (this.rightLeg = new ModelPart(this, 0, 16)).addBox(-1.0f, 0.0f, -1.0f, 2, 12, 2, float1);
            this.rightLeg.setPos(-2.0f, 12.0f, 0.0f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 12, 2, float1);
            this.leftLeg.setPos(2.0f, 12.0f, 0.0f);
        }
    }
    
    @Override
    public void prepareMobModel(final T aiy, final float float2, final float float3, final float float4) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        final ItemStack bcj6 = ((LivingEntity)aiy).getItemInHand(InteractionHand.MAIN_HAND);
        if (bcj6.getItem() == Items.BOW && ((Mob)aiy).isAggressive()) {
            if (((Mob)aiy).getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = ArmPose.BOW_AND_ARROW;
            }
            else {
                this.leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }
        super.prepareMobModel(aiy, float2, float3, float4);
    }
    
    @Override
    public void setupAnim(final T aiy, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aiy, float2, float3, float4, float5, float6, float7);
        final ItemStack bcj9 = ((LivingEntity)aiy).getMainHandItem();
        if (((Mob)aiy).isAggressive() && (bcj9.isEmpty() || bcj9.getItem() != Items.BOW)) {
            final float float8 = Mth.sin(this.attackTime * 3.1415927f);
            final float float9 = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * 3.1415927f);
            this.rightArm.zRot = 0.0f;
            this.leftArm.zRot = 0.0f;
            this.rightArm.yRot = -(0.1f - float8 * 0.6f);
            this.leftArm.yRot = 0.1f - float8 * 0.6f;
            this.rightArm.xRot = -1.5707964f;
            this.leftArm.xRot = -1.5707964f;
            final ModelPart rightArm = this.rightArm;
            rightArm.xRot -= float8 * 1.2f - float9 * 0.4f;
            final ModelPart leftArm = this.leftArm;
            leftArm.xRot -= float8 * 1.2f - float9 * 0.4f;
            final ModelPart rightArm2 = this.rightArm;
            rightArm2.zRot += Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
            final ModelPart leftArm2 = this.leftArm;
            leftArm2.zRot -= Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
            final ModelPart rightArm3 = this.rightArm;
            rightArm3.xRot += Mth.sin(float4 * 0.067f) * 0.05f;
            final ModelPart leftArm3 = this.leftArm;
            leftArm3.xRot -= Mth.sin(float4 * 0.067f) * 0.05f;
        }
    }
    
    @Override
    public void translateToHand(final float float1, final HumanoidArm aiw) {
        final float float2 = (aiw == HumanoidArm.RIGHT) ? 1.0f : -1.0f;
        final ModelPart arm;
        final ModelPart djv5 = arm = this.getArm(aiw);
        arm.x += float2;
        djv5.translateTo(float1);
        final ModelPart modelPart = djv5;
        modelPart.x -= float2;
    }
}
