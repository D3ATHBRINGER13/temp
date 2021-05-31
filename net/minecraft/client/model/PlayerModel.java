package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModel<T extends LivingEntity> extends HumanoidModel<T> {
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final ModelPart cloak;
    private final ModelPart ear;
    private final boolean slim;
    
    public PlayerModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, 64);
        this.slim = boolean2;
        (this.ear = new ModelPart(this, 24, 0)).addBox(-3.0f, -6.0f, -1.0f, 6, 6, 1, float1);
        (this.cloak = new ModelPart(this, 0, 0)).setTexSize(64, 32);
        this.cloak.addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, float1);
        if (boolean2) {
            (this.leftArm = new ModelPart(this, 32, 48)).addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, float1);
            this.leftArm.setPos(5.0f, 2.5f, 0.0f);
            (this.rightArm = new ModelPart(this, 40, 16)).addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, float1);
            this.rightArm.setPos(-5.0f, 2.5f, 0.0f);
            (this.leftSleeve = new ModelPart(this, 48, 48)).addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, float1 + 0.25f);
            this.leftSleeve.setPos(5.0f, 2.5f, 0.0f);
            (this.rightSleeve = new ModelPart(this, 40, 32)).addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, float1 + 0.25f);
            this.rightSleeve.setPos(-5.0f, 2.5f, 10.0f);
        }
        else {
            (this.leftArm = new ModelPart(this, 32, 48)).addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1);
            this.leftArm.setPos(5.0f, 2.0f, 0.0f);
            (this.leftSleeve = new ModelPart(this, 48, 48)).addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1 + 0.25f);
            this.leftSleeve.setPos(5.0f, 2.0f, 0.0f);
            (this.rightSleeve = new ModelPart(this, 40, 32)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, float1 + 0.25f);
            this.rightSleeve.setPos(-5.0f, 2.0f, 10.0f);
        }
        (this.leftLeg = new ModelPart(this, 16, 48)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        this.leftLeg.setPos(1.9f, 12.0f, 0.0f);
        (this.leftPants = new ModelPart(this, 0, 48)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1 + 0.25f);
        this.leftPants.setPos(1.9f, 12.0f, 0.0f);
        (this.rightPants = new ModelPart(this, 0, 32)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1 + 0.25f);
        this.rightPants.setPos(-1.9f, 12.0f, 0.0f);
        (this.jacket = new ModelPart(this, 16, 32)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1 + 0.25f);
        this.jacket.setPos(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.render(aix, float2, float3, float4, float5, float6, float7);
        GlStateManager.pushMatrix();
        if (this.young) {
            final float float8 = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * float7, 0.0f);
            this.leftPants.render(float7);
            this.rightPants.render(float7);
            this.leftSleeve.render(float7);
            this.rightSleeve.render(float7);
            this.jacket.render(float7);
        }
        else {
            if (aix.isVisuallySneaking()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.leftPants.render(float7);
            this.rightPants.render(float7);
            this.leftSleeve.render(float7);
            this.rightSleeve.render(float7);
            this.jacket.render(float7);
        }
        GlStateManager.popMatrix();
    }
    
    public void renderEars(final float float1) {
        this.ear.copyFrom(this.head);
        this.ear.x = 0.0f;
        this.ear.y = 0.0f;
        this.ear.render(float1);
    }
    
    public void renderCloak(final float float1) {
        this.cloak.render(float1);
    }
    
    @Override
    public void setupAnim(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aix, float2, float3, float4, float5, float6, float7);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        if (aix.isVisuallySneaking()) {
            this.cloak.y = 2.0f;
        }
        else {
            this.cloak.y = 0.0f;
        }
    }
    
    @Override
    public void setAllVisible(final boolean boolean1) {
        super.setAllVisible(boolean1);
        this.leftSleeve.visible = boolean1;
        this.rightSleeve.visible = boolean1;
        this.leftPants.visible = boolean1;
        this.rightPants.visible = boolean1;
        this.jacket.visible = boolean1;
        this.cloak.visible = boolean1;
        this.ear.visible = boolean1;
    }
    
    @Override
    public void translateToHand(final float float1, final HumanoidArm aiw) {
        final ModelPart djv4 = this.getArm(aiw);
        if (this.slim) {
            final float float2 = 0.5f * ((aiw == HumanoidArm.RIGHT) ? 1 : -1);
            final ModelPart modelPart = djv4;
            modelPart.x += float2;
            djv4.translateTo(float1);
            final ModelPart modelPart2 = djv4;
            modelPart2.x -= float2;
        }
        else {
            djv4.translateTo(float1);
        }
    }
}
