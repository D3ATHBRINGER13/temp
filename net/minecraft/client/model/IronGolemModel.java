package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemModel<T extends IronGolem> extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    public final ModelPart arm0;
    private final ModelPart arm1;
    private final ModelPart leg0;
    private final ModelPart leg1;
    
    public IronGolemModel() {
        this(0.0f);
    }
    
    public IronGolemModel(final float float1) {
        this(float1, -7.0f);
    }
    
    public IronGolemModel(final float float1, final float float2) {
        final int integer4 = 128;
        final int integer5 = 128;
        (this.head = new ModelPart(this).setTexSize(128, 128)).setPos(0.0f, 0.0f + float2, -2.0f);
        this.head.texOffs(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8, 10, 8, float1);
        this.head.texOffs(24, 0).addBox(-1.0f, -5.0f, -7.5f, 2, 4, 2, float1);
        (this.body = new ModelPart(this).setTexSize(128, 128)).setPos(0.0f, 0.0f + float2, 0.0f);
        this.body.texOffs(0, 40).addBox(-9.0f, -2.0f, -6.0f, 18, 12, 11, float1);
        this.body.texOffs(0, 70).addBox(-4.5f, 10.0f, -3.0f, 9, 5, 6, float1 + 0.5f);
        (this.arm0 = new ModelPart(this).setTexSize(128, 128)).setPos(0.0f, -7.0f, 0.0f);
        this.arm0.texOffs(60, 21).addBox(-13.0f, -2.5f, -3.0f, 4, 30, 6, float1);
        (this.arm1 = new ModelPart(this).setTexSize(128, 128)).setPos(0.0f, -7.0f, 0.0f);
        this.arm1.texOffs(60, 58).addBox(9.0f, -2.5f, -3.0f, 4, 30, 6, float1);
        (this.leg0 = new ModelPart(this, 0, 22).setTexSize(128, 128)).setPos(-4.0f, 18.0f + float2, 0.0f);
        this.leg0.texOffs(37, 0).addBox(-3.5f, -3.0f, -3.0f, 6, 16, 5, float1);
        this.leg1 = new ModelPart(this, 0, 22).setTexSize(128, 128);
        this.leg1.mirror = true;
        this.leg1.texOffs(60, 0).setPos(5.0f, 18.0f + float2, 0.0f);
        this.leg1.addBox(-3.5f, -3.0f, -3.0f, 6, 16, 5, float1);
    }
    
    @Override
    public void setupAnim(final T ari, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(ari, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        this.body.render(float7);
        this.leg0.render(float7);
        this.leg1.render(float7);
        this.arm0.render(float7);
        this.arm1.render(float7);
    }
    
    @Override
    public void setupAnim(final T ari, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        this.leg0.xRot = -1.5f * this.triangleWave(float2, 13.0f) * float3;
        this.leg1.xRot = 1.5f * this.triangleWave(float2, 13.0f) * float3;
        this.leg0.yRot = 0.0f;
        this.leg1.yRot = 0.0f;
    }
    
    @Override
    public void prepareMobModel(final T ari, final float float2, final float float3, final float float4) {
        final int integer6 = ari.getAttackAnimationTick();
        if (integer6 > 0) {
            this.arm0.xRot = -2.0f + 1.5f * this.triangleWave(integer6 - float4, 10.0f);
            this.arm1.xRot = -2.0f + 1.5f * this.triangleWave(integer6 - float4, 10.0f);
        }
        else {
            final int integer7 = ari.getOfferFlowerTick();
            if (integer7 > 0) {
                this.arm0.xRot = -0.8f + 0.025f * this.triangleWave((float)integer7, 70.0f);
                this.arm1.xRot = 0.0f;
            }
            else {
                this.arm0.xRot = (-0.2f + 1.5f * this.triangleWave(float2, 13.0f)) * float3;
                this.arm1.xRot = (-0.2f - 1.5f * this.triangleWave(float2, 13.0f)) * float3;
            }
        }
    }
    
    private float triangleWave(final float float1, final float float2) {
        return (Math.abs(float1 % float2 - float2 * 0.5f) - float2 * 0.25f) / (float2 * 0.25f);
    }
    
    public ModelPart getFlowerHoldingArm() {
        return this.arm0;
    }
}
