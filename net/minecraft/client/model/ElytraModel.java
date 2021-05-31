package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class ElytraModel<T extends LivingEntity> extends EntityModel<T> {
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    
    public ElytraModel() {
        (this.leftWing = new ModelPart(this, 22, 0)).addBox(-10.0f, 0.0f, 0.0f, 10, 20, 2, 1.0f);
        this.rightWing = new ModelPart(this, 22, 0);
        this.rightWing.mirror = true;
        this.rightWing.addBox(0.0f, 0.0f, 0.0f, 10, 20, 2, 1.0f);
    }
    
    @Override
    public void setupAnim(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        if (aix.isBaby()) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 1.5f, -0.1f);
            this.leftWing.render(float7);
            this.rightWing.render(float7);
            GlStateManager.popMatrix();
        }
        else {
            this.leftWing.render(float7);
            this.rightWing.render(float7);
        }
    }
    
    @Override
    public void setupAnim(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aix, float2, float3, float4, float5, float6, float7);
        float float8 = 0.2617994f;
        float float9 = -0.2617994f;
        float float10 = 0.0f;
        float float11 = 0.0f;
        if (aix.isFallFlying()) {
            float float12 = 1.0f;
            final Vec3 csi14 = aix.getDeltaMovement();
            if (csi14.y < 0.0) {
                final Vec3 csi15 = csi14.normalize();
                float12 = 1.0f - (float)Math.pow(-csi15.y, 1.5);
            }
            float8 = float12 * 0.34906584f + (1.0f - float12) * float8;
            float9 = float12 * -1.5707964f + (1.0f - float12) * float9;
        }
        else if (aix.isVisuallySneaking()) {
            float8 = 0.6981317f;
            float9 = -0.7853982f;
            float10 = 3.0f;
            float11 = 0.08726646f;
        }
        this.leftWing.x = 5.0f;
        this.leftWing.y = float10;
        if (aix instanceof AbstractClientPlayer) {
            final AbstractClientPlayer abstractClientPlayer;
            final AbstractClientPlayer dmm13 = abstractClientPlayer = (AbstractClientPlayer)aix;
            abstractClientPlayer.elytraRotX += (float)((float8 - dmm13.elytraRotX) * 0.1);
            final AbstractClientPlayer abstractClientPlayer2 = dmm13;
            abstractClientPlayer2.elytraRotY += (float)((float11 - dmm13.elytraRotY) * 0.1);
            final AbstractClientPlayer abstractClientPlayer3 = dmm13;
            abstractClientPlayer3.elytraRotZ += (float)((float9 - dmm13.elytraRotZ) * 0.1);
            this.leftWing.xRot = dmm13.elytraRotX;
            this.leftWing.yRot = dmm13.elytraRotY;
            this.leftWing.zRot = dmm13.elytraRotZ;
        }
        else {
            this.leftWing.xRot = float8;
            this.leftWing.zRot = float9;
            this.leftWing.yRot = float11;
        }
        this.rightWing.x = -this.leftWing.x;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.y = this.leftWing.y;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.zRot = -this.leftWing.zRot;
    }
}
