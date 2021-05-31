package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Vex;

public class VexModel extends HumanoidModel<Vex> {
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    
    public VexModel() {
        this(0.0f);
    }
    
    public VexModel(final float float1) {
        super(float1, 0.0f, 64, 64);
        this.leftLeg.visible = false;
        this.hat.visible = false;
        (this.rightLeg = new ModelPart(this, 32, 0)).addBox(-1.0f, -1.0f, -2.0f, 6, 10, 4, 0.0f);
        this.rightLeg.setPos(-1.9f, 12.0f, 0.0f);
        (this.rightWing = new ModelPart(this, 0, 32)).addBox(-20.0f, 0.0f, 0.0f, 20, 12, 1);
        this.leftWing = new ModelPart(this, 0, 32);
        this.leftWing.mirror = true;
        this.leftWing.addBox(0.0f, 0.0f, 0.0f, 20, 12, 1);
    }
    
    @Override
    public void render(final Vex avi, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.render(avi, float2, float3, float4, float5, float6, float7);
        this.rightWing.render(float7);
        this.leftWing.render(float7);
    }
    
    @Override
    public void render(final Vex avi, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(avi, float2, float3, float4, float5, float6, float7);
        if (avi.isCharging()) {
            if (avi.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArm.xRot = 3.7699115f;
            }
            else {
                this.leftArm.xRot = 3.7699115f;
            }
        }
        final ModelPart rightLeg = this.rightLeg;
        rightLeg.xRot += 0.62831855f;
        this.rightWing.z = 2.0f;
        this.leftWing.z = 2.0f;
        this.rightWing.y = 1.0f;
        this.leftWing.y = 1.0f;
        this.rightWing.yRot = 0.47123894f + Mth.cos(float4 * 0.8f) * 3.1415927f * 0.05f;
        this.leftWing.yRot = -this.rightWing.yRot;
        this.leftWing.zRot = -0.47123894f;
        this.leftWing.xRot = 0.47123894f;
        this.rightWing.xRot = 0.47123894f;
        this.rightWing.zRot = 0.47123894f;
    }
}
