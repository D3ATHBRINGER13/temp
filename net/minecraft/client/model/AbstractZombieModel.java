package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;

public abstract class AbstractZombieModel<T extends Monster> extends HumanoidModel<T> {
    protected AbstractZombieModel(final float float1, final float float2, final int integer3, final int integer4) {
        super(float1, float2, integer3, integer4);
    }
    
    @Override
    public void setupAnim(final T aus, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aus, float2, float3, float4, float5, float6, float7);
        final boolean boolean9 = this.isAggressive(aus);
        final float float8 = Mth.sin(this.attackTime * 3.1415927f);
        final float float9 = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * 3.1415927f);
        this.rightArm.zRot = 0.0f;
        this.leftArm.zRot = 0.0f;
        this.rightArm.yRot = -(0.1f - float8 * 0.6f);
        this.leftArm.yRot = 0.1f - float8 * 0.6f;
        final float float10 = -3.1415927f / (boolean9 ? 1.5f : 2.25f);
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
    
    public abstract boolean isAggressive(final T aus);
}
