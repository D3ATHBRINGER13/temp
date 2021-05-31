package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.HeadedModel;
import net.minecraft.client.renderer.entity.ArmedModel;
import net.minecraft.world.entity.monster.AbstractIllager;

public class IllagerModel<T extends AbstractIllager> extends EntityModel<T> implements ArmedModel, HeadedModel {
    protected final ModelPart head;
    private final ModelPart hat;
    protected final ModelPart body;
    protected final ModelPart arms;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;
    private final ModelPart nose;
    protected final ModelPart rightArm;
    protected final ModelPart leftArm;
    private float itemUseTicks;
    
    public IllagerModel(final float float1, final float float2, final int integer3, final int integer4) {
        (this.head = new ModelPart(this).setTexSize(integer3, integer4)).setPos(0.0f, 0.0f + float2, 0.0f);
        this.head.texOffs(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, float1);
        (this.hat = new ModelPart(this, 32, 0).setTexSize(integer3, integer4)).addBox(-4.0f, -10.0f, -4.0f, 8, 12, 8, float1 + 0.45f);
        this.head.addChild(this.hat);
        this.hat.visible = false;
        (this.nose = new ModelPart(this).setTexSize(integer3, integer4)).setPos(0.0f, float2 - 2.0f, 0.0f);
        this.nose.texOffs(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2, 4, 2, float1);
        this.head.addChild(this.nose);
        (this.body = new ModelPart(this).setTexSize(integer3, integer4)).setPos(0.0f, 0.0f + float2, 0.0f);
        this.body.texOffs(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, float1);
        this.body.texOffs(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, float1 + 0.5f);
        (this.arms = new ModelPart(this).setTexSize(integer3, integer4)).setPos(0.0f, 0.0f + float2 + 2.0f, 0.0f);
        this.arms.texOffs(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4, 8, 4, float1);
        final ModelPart djv6 = new ModelPart(this, 44, 22).setTexSize(integer3, integer4);
        djv6.mirror = true;
        djv6.addBox(4.0f, -2.0f, -2.0f, 4, 8, 4, float1);
        this.arms.addChild(djv6);
        this.arms.texOffs(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8, 4, 4, float1);
        (this.leftLeg = new ModelPart(this, 0, 22).setTexSize(integer3, integer4)).setPos(-2.0f, 12.0f + float2, 0.0f);
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        this.rightLeg = new ModelPart(this, 0, 22).setTexSize(integer3, integer4);
        this.rightLeg.mirror = true;
        this.rightLeg.setPos(2.0f, 12.0f + float2, 0.0f);
        this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        (this.rightArm = new ModelPart(this, 40, 46).setTexSize(integer3, integer4)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, float1);
        this.rightArm.setPos(-5.0f, 2.0f + float2, 0.0f);
        this.leftArm = new ModelPart(this, 40, 46).setTexSize(integer3, integer4);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1);
        this.leftArm.setPos(5.0f, 2.0f + float2, 0.0f);
    }
    
    @Override
    public void render(final T aua, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.render(aua, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        this.body.render(float7);
        this.leftLeg.render(float7);
        this.rightLeg.render(float7);
        if (aua.getArmPose() == AbstractIllager.IllagerArmPose.CROSSED) {
            this.arms.render(float7);
        }
        else {
            this.rightArm.render(float7);
            this.leftArm.render(float7);
        }
    }
    
    @Override
    public void render(final T aua, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        this.arms.y = 3.0f;
        this.arms.z = -1.0f;
        this.arms.xRot = -0.75f;
        if (this.riding) {
            this.rightArm.xRot = -0.62831855f;
            this.rightArm.yRot = 0.0f;
            this.rightArm.zRot = 0.0f;
            this.leftArm.xRot = -0.62831855f;
            this.leftArm.yRot = 0.0f;
            this.leftArm.zRot = 0.0f;
            this.leftLeg.xRot = -1.4137167f;
            this.leftLeg.yRot = 0.31415927f;
            this.leftLeg.zRot = 0.07853982f;
            this.rightLeg.xRot = -1.4137167f;
            this.rightLeg.yRot = -0.31415927f;
            this.rightLeg.zRot = -0.07853982f;
        }
        else {
            this.rightArm.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 2.0f * float3 * 0.5f;
            this.rightArm.yRot = 0.0f;
            this.rightArm.zRot = 0.0f;
            this.leftArm.xRot = Mth.cos(float2 * 0.6662f) * 2.0f * float3 * 0.5f;
            this.leftArm.yRot = 0.0f;
            this.leftArm.zRot = 0.0f;
            this.leftLeg.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3 * 0.5f;
            this.leftLeg.yRot = 0.0f;
            this.leftLeg.zRot = 0.0f;
            this.rightLeg.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3 * 0.5f;
            this.rightLeg.yRot = 0.0f;
            this.rightLeg.zRot = 0.0f;
        }
        final AbstractIllager.IllagerArmPose a9 = aua.getArmPose();
        if (a9 == AbstractIllager.IllagerArmPose.ATTACKING) {
            final float float8 = Mth.sin(this.attackTime * 3.1415927f);
            final float float9 = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * 3.1415927f);
            this.rightArm.zRot = 0.0f;
            this.leftArm.zRot = 0.0f;
            this.rightArm.yRot = 0.15707964f;
            this.leftArm.yRot = -0.15707964f;
            if (aua.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArm.xRot = -1.8849558f + Mth.cos(float4 * 0.09f) * 0.15f;
                this.leftArm.xRot = -0.0f + Mth.cos(float4 * 0.19f) * 0.5f;
                final ModelPart rightArm = this.rightArm;
                rightArm.xRot += float8 * 2.2f - float9 * 0.4f;
                final ModelPart leftArm = this.leftArm;
                leftArm.xRot += float8 * 1.2f - float9 * 0.4f;
            }
            else {
                this.rightArm.xRot = -0.0f + Mth.cos(float4 * 0.19f) * 0.5f;
                this.leftArm.xRot = -1.8849558f + Mth.cos(float4 * 0.09f) * 0.15f;
                final ModelPart rightArm2 = this.rightArm;
                rightArm2.xRot += float8 * 1.2f - float9 * 0.4f;
                final ModelPart leftArm2 = this.leftArm;
                leftArm2.xRot += float8 * 2.2f - float9 * 0.4f;
            }
            final ModelPart rightArm3 = this.rightArm;
            rightArm3.zRot += Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
            final ModelPart leftArm3 = this.leftArm;
            leftArm3.zRot -= Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
            final ModelPart rightArm4 = this.rightArm;
            rightArm4.xRot += Mth.sin(float4 * 0.067f) * 0.05f;
            final ModelPart leftArm4 = this.leftArm;
            leftArm4.xRot -= Mth.sin(float4 * 0.067f) * 0.05f;
        }
        else if (a9 == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            this.rightArm.z = 0.0f;
            this.rightArm.x = -5.0f;
            this.leftArm.z = 0.0f;
            this.leftArm.x = 5.0f;
            this.rightArm.xRot = Mth.cos(float4 * 0.6662f) * 0.25f;
            this.leftArm.xRot = Mth.cos(float4 * 0.6662f) * 0.25f;
            this.rightArm.zRot = 2.3561945f;
            this.leftArm.zRot = -2.3561945f;
            this.rightArm.yRot = 0.0f;
            this.leftArm.yRot = 0.0f;
        }
        else if (a9 == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            this.rightArm.yRot = -0.1f + this.head.yRot;
            this.rightArm.xRot = -1.5707964f + this.head.xRot;
            this.leftArm.xRot = -0.9424779f + this.head.xRot;
            this.leftArm.yRot = this.head.yRot - 0.4f;
            this.leftArm.zRot = 1.5707964f;
        }
        else if (a9 == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
            this.rightArm.yRot = -0.3f + this.head.yRot;
            this.leftArm.yRot = 0.6f + this.head.yRot;
            this.rightArm.xRot = -1.5707964f + this.head.xRot + 0.1f;
            this.leftArm.xRot = -1.5f + this.head.xRot;
        }
        else if (a9 == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
            this.rightArm.yRot = -0.8f;
            this.rightArm.xRot = -0.97079635f;
            this.leftArm.xRot = -0.97079635f;
            final float float8 = Mth.clamp(this.itemUseTicks, 0.0f, 25.0f);
            this.leftArm.yRot = Mth.lerp(float8 / 25.0f, 0.4f, 0.85f);
            this.leftArm.xRot = Mth.lerp(float8 / 25.0f, this.leftArm.xRot, -1.5707964f);
        }
        else if (a9 == AbstractIllager.IllagerArmPose.CELEBRATING) {
            this.rightArm.z = 0.0f;
            this.rightArm.x = -5.0f;
            this.rightArm.xRot = Mth.cos(float4 * 0.6662f) * 0.05f;
            this.rightArm.zRot = 2.670354f;
            this.rightArm.yRot = 0.0f;
            this.leftArm.z = 0.0f;
            this.leftArm.x = 5.0f;
            this.leftArm.xRot = Mth.cos(float4 * 0.6662f) * 0.05f;
            this.leftArm.zRot = -2.3561945f;
            this.leftArm.yRot = 0.0f;
        }
    }
    
    @Override
    public void prepareMobModel(final T aua, final float float2, final float float3, final float float4) {
        this.itemUseTicks = (float)aua.getTicksUsingItem();
        super.prepareMobModel(aua, float2, float3, float4);
    }
    
    private ModelPart getArm(final HumanoidArm aiw) {
        if (aiw == HumanoidArm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }
    
    public ModelPart getHat() {
        return this.hat;
    }
    
    @Override
    public ModelPart getHead() {
        return this.head;
    }
    
    @Override
    public void translateToHand(final float float1, final HumanoidArm aiw) {
        this.getArm(aiw).translateTo(0.0625f);
    }
}
