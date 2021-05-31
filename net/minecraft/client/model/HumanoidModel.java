package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.HeadedModel;
import net.minecraft.client.renderer.entity.ArmedModel;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidModel<T extends LivingEntity> extends EntityModel<T> implements ArmedModel, HeadedModel {
    public ModelPart head;
    public ModelPart hat;
    public ModelPart body;
    public ModelPart rightArm;
    public ModelPart leftArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ArmPose leftArmPose;
    public ArmPose rightArmPose;
    public boolean sneaking;
    public float swimAmount;
    private float itemUseTicks;
    
    public HumanoidModel() {
        this(0.0f);
    }
    
    public HumanoidModel(final float float1) {
        this(float1, 0.0f, 64, 32);
    }
    
    public HumanoidModel(final float float1, final float float2, final int integer3, final int integer4) {
        this.leftArmPose = ArmPose.EMPTY;
        this.rightArmPose = ArmPose.EMPTY;
        this.texWidth = integer3;
        this.texHeight = integer4;
        (this.head = new ModelPart(this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1);
        this.head.setPos(0.0f, 0.0f + float2, 0.0f);
        (this.hat = new ModelPart(this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1 + 0.5f);
        this.hat.setPos(0.0f, 0.0f + float2, 0.0f);
        (this.body = new ModelPart(this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1);
        this.body.setPos(0.0f, 0.0f + float2, 0.0f);
        (this.rightArm = new ModelPart(this, 40, 16)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, float1);
        this.rightArm.setPos(-5.0f, 2.0f + float2, 0.0f);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1);
        this.leftArm.setPos(5.0f, 2.0f + float2, 0.0f);
        (this.rightLeg = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        this.rightLeg.setPos(-1.9f, 12.0f + float2, 0.0f);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        this.leftLeg.setPos(1.9f, 12.0f + float2, 0.0f);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aix, float2, float3, float4, float5, float6, float7);
        GlStateManager.pushMatrix();
        if (this.young) {
            final float float8 = 2.0f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 16.0f * float7, 0.0f);
            this.head.render(float7);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * float7, 0.0f);
            this.body.render(float7);
            this.rightArm.render(float7);
            this.leftArm.render(float7);
            this.rightLeg.render(float7);
            this.leftLeg.render(float7);
            this.hat.render(float7);
        }
        else {
            if (aix.isVisuallySneaking()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.head.render(float7);
            this.body.render(float7);
            this.rightArm.render(float7);
            this.leftArm.render(float7);
            this.rightLeg.render(float7);
            this.leftLeg.render(float7);
            this.hat.render(float7);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public void prepareMobModel(final T aix, final float float2, final float float3, final float float4) {
        this.swimAmount = aix.getSwimAmount(float4);
        this.itemUseTicks = (float)aix.getTicksUsingItem();
        super.prepareMobModel(aix, float2, float3, float4);
    }
    
    @Override
    public void setupAnim(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final boolean boolean9 = aix.getFallFlyingTicks() > 4;
        final boolean boolean10 = aix.isVisuallySwimming();
        this.head.yRot = float5 * 0.017453292f;
        if (boolean9) {
            this.head.xRot = -0.7853982f;
        }
        else if (this.swimAmount > 0.0f) {
            if (boolean10) {
                this.head.xRot = this.rotlerpRad(this.head.xRot, -0.7853982f, this.swimAmount);
            }
            else {
                this.head.xRot = this.rotlerpRad(this.head.xRot, float6 * 0.017453292f, this.swimAmount);
            }
        }
        else {
            this.head.xRot = float6 * 0.017453292f;
        }
        this.body.yRot = 0.0f;
        this.rightArm.z = 0.0f;
        this.rightArm.x = -5.0f;
        this.leftArm.z = 0.0f;
        this.leftArm.x = 5.0f;
        float float8 = 1.0f;
        if (boolean9) {
            float8 = (float)aix.getDeltaMovement().lengthSqr();
            float8 /= 0.2f;
            float8 *= float8 * float8;
        }
        if (float8 < 1.0f) {
            float8 = 1.0f;
        }
        this.rightArm.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 2.0f * float3 * 0.5f / float8;
        this.leftArm.xRot = Mth.cos(float2 * 0.6662f) * 2.0f * float3 * 0.5f / float8;
        this.rightArm.zRot = 0.0f;
        this.leftArm.zRot = 0.0f;
        this.rightLeg.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3 / float8;
        this.leftLeg.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3 / float8;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.yRot = 0.0f;
        this.rightLeg.zRot = 0.0f;
        this.leftLeg.zRot = 0.0f;
        if (this.riding) {
            final ModelPart rightArm = this.rightArm;
            rightArm.xRot -= 0.62831855f;
            final ModelPart leftArm = this.leftArm;
            leftArm.xRot -= 0.62831855f;
            this.rightLeg.xRot = -1.4137167f;
            this.rightLeg.yRot = 0.31415927f;
            this.rightLeg.zRot = 0.07853982f;
            this.leftLeg.xRot = -1.4137167f;
            this.leftLeg.yRot = -0.31415927f;
            this.leftLeg.zRot = -0.07853982f;
        }
        this.rightArm.yRot = 0.0f;
        this.rightArm.zRot = 0.0f;
        switch (this.leftArmPose) {
            case EMPTY: {
                this.leftArm.yRot = 0.0f;
                break;
            }
            case BLOCK: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - 0.9424779f;
                this.leftArm.yRot = 0.5235988f;
                break;
            }
            case ITEM: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - 0.31415927f;
                this.leftArm.yRot = 0.0f;
                break;
            }
        }
        switch (this.rightArmPose) {
            case EMPTY: {
                this.rightArm.yRot = 0.0f;
                break;
            }
            case BLOCK: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.9424779f;
                this.rightArm.yRot = -0.5235988f;
                break;
            }
            case ITEM: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.31415927f;
                this.rightArm.yRot = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 3.1415927f;
                this.rightArm.yRot = 0.0f;
                break;
            }
        }
        if (this.leftArmPose == ArmPose.THROW_SPEAR && this.rightArmPose != ArmPose.BLOCK && this.rightArmPose != ArmPose.THROW_SPEAR && this.rightArmPose != ArmPose.BOW_AND_ARROW) {
            this.leftArm.xRot = this.leftArm.xRot * 0.5f - 3.1415927f;
            this.leftArm.yRot = 0.0f;
        }
        if (this.attackTime > 0.0f) {
            final HumanoidArm aiw12 = this.getAttackArm(aix);
            final ModelPart djv13 = this.getArm(aiw12);
            float float9 = this.attackTime;
            this.body.yRot = Mth.sin(Mth.sqrt(float9) * 6.2831855f) * 0.2f;
            if (aiw12 == HumanoidArm.LEFT) {
                final ModelPart body = this.body;
                body.yRot *= -1.0f;
            }
            this.rightArm.z = Mth.sin(this.body.yRot) * 5.0f;
            this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0f;
            this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0f;
            this.leftArm.x = Mth.cos(this.body.yRot) * 5.0f;
            final ModelPart rightArm2 = this.rightArm;
            rightArm2.yRot += this.body.yRot;
            final ModelPart leftArm2 = this.leftArm;
            leftArm2.yRot += this.body.yRot;
            final ModelPart leftArm3 = this.leftArm;
            leftArm3.xRot += this.body.yRot;
            float9 = 1.0f - this.attackTime;
            float9 *= float9;
            float9 *= float9;
            float9 = 1.0f - float9;
            final float float10 = Mth.sin(float9 * 3.1415927f);
            final float float11 = Mth.sin(this.attackTime * 3.1415927f) * -(this.head.xRot - 0.7f) * 0.75f;
            final ModelPart modelPart = djv13;
            modelPart.xRot -= (float)(float10 * 1.2 + float11);
            final ModelPart modelPart2 = djv13;
            modelPart2.yRot += this.body.yRot * 2.0f;
            final ModelPart modelPart3 = djv13;
            modelPart3.zRot += Mth.sin(this.attackTime * 3.1415927f) * -0.4f;
        }
        if (this.sneaking) {
            this.body.xRot = 0.5f;
            final ModelPart rightArm3 = this.rightArm;
            rightArm3.xRot += 0.4f;
            final ModelPart leftArm4 = this.leftArm;
            leftArm4.xRot += 0.4f;
            this.rightLeg.z = 4.0f;
            this.leftLeg.z = 4.0f;
            this.rightLeg.y = 9.0f;
            this.leftLeg.y = 9.0f;
            this.head.y = 1.0f;
        }
        else {
            this.body.xRot = 0.0f;
            this.rightLeg.z = 0.1f;
            this.leftLeg.z = 0.1f;
            this.rightLeg.y = 12.0f;
            this.leftLeg.y = 12.0f;
            this.head.y = 0.0f;
        }
        final ModelPart rightArm4 = this.rightArm;
        rightArm4.zRot += Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
        final ModelPart leftArm5 = this.leftArm;
        leftArm5.zRot -= Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
        final ModelPart rightArm5 = this.rightArm;
        rightArm5.xRot += Mth.sin(float4 * 0.067f) * 0.05f;
        final ModelPart leftArm6 = this.leftArm;
        leftArm6.xRot -= Mth.sin(float4 * 0.067f) * 0.05f;
        if (this.rightArmPose == ArmPose.BOW_AND_ARROW) {
            this.rightArm.yRot = -0.1f + this.head.yRot;
            this.leftArm.yRot = 0.1f + this.head.yRot + 0.4f;
            this.rightArm.xRot = -1.5707964f + this.head.xRot;
            this.leftArm.xRot = -1.5707964f + this.head.xRot;
        }
        else if (this.leftArmPose == ArmPose.BOW_AND_ARROW && this.rightArmPose != ArmPose.THROW_SPEAR && this.rightArmPose != ArmPose.BLOCK) {
            this.rightArm.yRot = -0.1f + this.head.yRot - 0.4f;
            this.leftArm.yRot = 0.1f + this.head.yRot;
            this.rightArm.xRot = -1.5707964f + this.head.xRot;
            this.leftArm.xRot = -1.5707964f + this.head.xRot;
        }
        final float float12 = (float)CrossbowItem.getChargeDuration(aix.getUseItem());
        if (this.rightArmPose == ArmPose.CROSSBOW_CHARGE) {
            this.rightArm.yRot = -0.8f;
            this.rightArm.xRot = -0.97079635f;
            this.leftArm.xRot = -0.97079635f;
            final float float13 = Mth.clamp(this.itemUseTicks, 0.0f, float12);
            this.leftArm.yRot = Mth.lerp(float13 / float12, 0.4f, 0.85f);
            this.leftArm.xRot = Mth.lerp(float13 / float12, this.leftArm.xRot, -1.5707964f);
        }
        else if (this.leftArmPose == ArmPose.CROSSBOW_CHARGE) {
            this.leftArm.yRot = 0.8f;
            this.rightArm.xRot = -0.97079635f;
            this.leftArm.xRot = -0.97079635f;
            final float float13 = Mth.clamp(this.itemUseTicks, 0.0f, float12);
            this.rightArm.yRot = Mth.lerp(float13 / float12, -0.4f, -0.85f);
            this.rightArm.xRot = Mth.lerp(float13 / float12, this.rightArm.xRot, -1.5707964f);
        }
        if (this.rightArmPose == ArmPose.CROSSBOW_HOLD && this.attackTime <= 0.0f) {
            this.rightArm.yRot = -0.3f + this.head.yRot;
            this.leftArm.yRot = 0.6f + this.head.yRot;
            this.rightArm.xRot = -1.5707964f + this.head.xRot + 0.1f;
            this.leftArm.xRot = -1.5f + this.head.xRot;
        }
        else if (this.leftArmPose == ArmPose.CROSSBOW_HOLD) {
            this.rightArm.yRot = -0.6f + this.head.yRot;
            this.leftArm.yRot = 0.3f + this.head.yRot;
            this.rightArm.xRot = -1.5f + this.head.xRot;
            this.leftArm.xRot = -1.5707964f + this.head.xRot + 0.1f;
        }
        if (this.swimAmount > 0.0f) {
            final float float13 = float2 % 26.0f;
            final float float9 = (this.attackTime > 0.0f) ? 0.0f : this.swimAmount;
            if (float13 < 14.0f) {
                this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 0.0f, this.swimAmount);
                this.rightArm.xRot = Mth.lerp(float9, this.rightArm.xRot, 0.0f);
                this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, 3.1415927f, this.swimAmount);
                this.rightArm.yRot = Mth.lerp(float9, this.rightArm.yRot, 3.1415927f);
                this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 3.1415927f + 1.8707964f * this.quadraticArmUpdate(float13) / this.quadraticArmUpdate(14.0f), this.swimAmount);
                this.rightArm.zRot = Mth.lerp(float9, this.rightArm.zRot, 3.1415927f - 1.8707964f * this.quadraticArmUpdate(float13) / this.quadraticArmUpdate(14.0f));
            }
            else if (float13 >= 14.0f && float13 < 22.0f) {
                final float float10 = (float13 - 14.0f) / 8.0f;
                this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 1.5707964f * float10, this.swimAmount);
                this.rightArm.xRot = Mth.lerp(float9, this.rightArm.xRot, 1.5707964f * float10);
                this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, 3.1415927f, this.swimAmount);
                this.rightArm.yRot = Mth.lerp(float9, this.rightArm.yRot, 3.1415927f);
                this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 5.012389f - 1.8707964f * float10, this.swimAmount);
                this.rightArm.zRot = Mth.lerp(float9, this.rightArm.zRot, 1.2707963f + 1.8707964f * float10);
            }
            else if (float13 >= 22.0f && float13 < 26.0f) {
                final float float10 = (float13 - 22.0f) / 4.0f;
                this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 1.5707964f - 1.5707964f * float10, this.swimAmount);
                this.rightArm.xRot = Mth.lerp(float9, this.rightArm.xRot, 1.5707964f - 1.5707964f * float10);
                this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, 3.1415927f, this.swimAmount);
                this.rightArm.yRot = Mth.lerp(float9, this.rightArm.yRot, 3.1415927f);
                this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 3.1415927f, this.swimAmount);
                this.rightArm.zRot = Mth.lerp(float9, this.rightArm.zRot, 3.1415927f);
            }
            final float float10 = 0.3f;
            final float float11 = 0.33333334f;
            this.leftLeg.xRot = Mth.lerp(this.swimAmount, this.leftLeg.xRot, 0.3f * Mth.cos(float2 * 0.33333334f + 3.1415927f));
            this.rightLeg.xRot = Mth.lerp(this.swimAmount, this.rightLeg.xRot, 0.3f * Mth.cos(float2 * 0.33333334f));
        }
        this.hat.copyFrom(this.head);
    }
    
    protected float rotlerpRad(final float float1, final float float2, final float float3) {
        float float4 = (float2 - float1) % 6.2831855f;
        if (float4 < -3.1415927f) {
            float4 += 6.2831855f;
        }
        if (float4 >= 3.1415927f) {
            float4 -= 6.2831855f;
        }
        return float1 + float3 * float4;
    }
    
    private float quadraticArmUpdate(final float float1) {
        return -65.0f * float1 + float1 * float1;
    }
    
    public void copyPropertiesTo(final HumanoidModel<T> dhp) {
        super.copyPropertiesTo(dhp);
        dhp.leftArmPose = this.leftArmPose;
        dhp.rightArmPose = this.rightArmPose;
        dhp.sneaking = this.sneaking;
    }
    
    public void setAllVisible(final boolean boolean1) {
        this.head.visible = boolean1;
        this.hat.visible = boolean1;
        this.body.visible = boolean1;
        this.rightArm.visible = boolean1;
        this.leftArm.visible = boolean1;
        this.rightLeg.visible = boolean1;
        this.leftLeg.visible = boolean1;
    }
    
    @Override
    public void translateToHand(final float float1, final HumanoidArm aiw) {
        this.getArm(aiw).translateTo(float1);
    }
    
    protected ModelPart getArm(final HumanoidArm aiw) {
        if (aiw == HumanoidArm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }
    
    @Override
    public ModelPart getHead() {
        return this.head;
    }
    
    protected HumanoidArm getAttackArm(final T aix) {
        final HumanoidArm aiw3 = aix.getMainArm();
        return (aix.swingingArm == InteractionHand.MAIN_HAND) ? aiw3 : aiw3.getOpposite();
    }
    
    public enum ArmPose {
        EMPTY, 
        ITEM, 
        BLOCK, 
        BOW_AND_ARROW, 
        THROW_SPEAR, 
        CROSSBOW_CHARGE, 
        CROSSBOW_HOLD;
    }
}
