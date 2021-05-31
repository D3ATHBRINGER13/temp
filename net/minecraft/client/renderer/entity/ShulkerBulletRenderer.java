package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerBulletRenderer extends EntityRenderer<ShulkerBullet> {
    private static final ResourceLocation TEXTURE_LOCATION;
    private final ShulkerBulletModel<ShulkerBullet> model;
    
    public ShulkerBulletRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new ShulkerBulletModel<ShulkerBullet>();
    }
    
    private float rotlerp(final float float1, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - float1; float4 < -180.0f; float4 += 360.0f) {}
        while (float4 >= 180.0f) {
            float4 -= 360.0f;
        }
        return float1 + float3 * float4;
    }
    
    @Override
    public void render(final ShulkerBullet awx, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        final float float7 = this.rotlerp(awx.yRotO, awx.yRot, float6);
        final float float8 = Mth.lerp(float6, awx.xRotO, awx.xRot);
        final float float9 = awx.tickCount + float6;
        GlStateManager.translatef((float)double2, (float)double3 + 0.15f, (float)double4);
        GlStateManager.rotatef(Mth.sin(float9 * 0.1f) * 180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(Mth.cos(float9 * 0.1f) * 180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(Mth.sin(float9 * 0.15f) * 360.0f, 0.0f, 0.0f, 1.0f);
        final float float10 = 0.03125f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.bindTexture(awx);
        this.model.render(awx, 0.0f, 0.0f, 0.0f, float7, float8, 0.03125f);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.scalef(1.5f, 1.5f, 1.5f);
        this.model.render(awx, 0.0f, 0.0f, 0.0f, float7, float8, 0.03125f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.render(awx, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final ShulkerBullet awx) {
        return ShulkerBulletRenderer.TEXTURE_LOCATION;
    }
    
    static {
        TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    }
}
