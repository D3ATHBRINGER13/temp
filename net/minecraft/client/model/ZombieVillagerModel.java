package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.VillagerHeadModel;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieVillagerModel<T extends Zombie> extends HumanoidModel<T> implements VillagerHeadModel {
    private ModelPart hatRim;
    
    public ZombieVillagerModel() {
        this(0.0f, false);
    }
    
    public ZombieVillagerModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, boolean2 ? 32 : 64);
        if (boolean2) {
            (this.head = new ModelPart(this, 0, 0)).addBox(-4.0f, -10.0f, -4.0f, 8, 8, 8, float1);
            (this.body = new ModelPart(this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1 + 0.1f);
            (this.rightLeg = new ModelPart(this, 0, 16)).setPos(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1 + 0.1f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.setPos(2.0f, 12.0f, 0.0f);
            this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1 + 0.1f);
        }
        else {
            this.head = new ModelPart(this, 0, 0);
            this.head.texOffs(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, float1);
            this.head.texOffs(24, 0).addBox(-1.0f, -3.0f, -6.0f, 2, 4, 2, float1);
            (this.hat = new ModelPart(this, 32, 0)).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, float1 + 0.5f);
            this.hatRim = new ModelPart(this);
            this.hatRim.texOffs(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16, 16, 1, float1);
            this.hatRim.xRot = -1.5707964f;
            this.hat.addChild(this.hatRim);
            (this.body = new ModelPart(this, 16, 20)).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, float1);
            this.body.texOffs(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, float1 + 0.05f);
            (this.rightArm = new ModelPart(this, 44, 22)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, float1);
            this.rightArm.setPos(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 44, 22);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1);
            this.leftArm.setPos(5.0f, 2.0f, 0.0f);
            (this.rightLeg = new ModelPart(this, 0, 22)).setPos(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
            this.leftLeg = new ModelPart(this, 0, 22);
            this.leftLeg.mirror = true;
            this.leftLeg.setPos(2.0f, 12.0f, 0.0f);
            this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        }
    }
    
    @Override
    public void setupAnim(final T avm, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(avm, float2, float3, float4, float5, float6, float7);
        final float float8 = Mth.sin(this.attackTime * 3.1415927f);
        final float float9 = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * 3.1415927f);
        this.rightArm.zRot = 0.0f;
        this.leftArm.zRot = 0.0f;
        this.rightArm.yRot = -(0.1f - float8 * 0.6f);
        this.leftArm.yRot = 0.1f - float8 * 0.6f;
        final float float10 = -3.1415927f / (avm.isAggressive() ? 1.5f : 2.25f);
        this.rightArm.xRot = float10;
        this.leftArm.xRot = float10;
        final ModelPart rightArm = this.rightArm;
        rightArm.xRot += float8 * 1.2f - float9 * 0.4f;
        final ModelPart leftArm = this.leftArm;
        leftArm.xRot += float8 * 1.2f - float9 * 0.4f;
        final ModelPart rightArm2 = this.rightArm;
        rightArm2.zRot += Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
        final ModelPart leftArm2 = this.leftArm;
        leftArm2.zRot -= Mth.cos(float4 * 0.09f) * 0.05f + 0.05f;
        final ModelPart rightArm3 = this.rightArm;
        rightArm3.xRot += Mth.sin(float4 * 0.067f) * 0.05f;
        final ModelPart leftArm3 = this.leftArm;
        leftArm3.xRot -= Mth.sin(float4 * 0.067f) * 0.05f;
    }
    
    @Override
    public void hatVisible(final boolean boolean1) {
        this.head.visible = boolean1;
        this.hat.visible = boolean1;
        this.hatRim.visible = boolean1;
    }
}
