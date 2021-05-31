package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;

public class EvokerFangsRenderer extends EntityRenderer<EvokerFangs> {
    private static final ResourceLocation TEXTURE_LOCATION;
    private final EvokerFangsModel<EvokerFangs> model;
    
    public EvokerFangsRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new EvokerFangsModel<EvokerFangs>();
    }
    
    @Override
    public void render(final EvokerFangs awo, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final float float7 = awo.getAnimationProgress(float6);
        if (float7 == 0.0f) {
            return;
        }
        float float8 = 2.0f;
        if (float7 > 0.9f) {
            float8 *= (float)((1.0 - float7) / 0.10000000149011612);
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableAlphaTest();
        this.bindTexture(awo);
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.rotatef(90.0f - awo.yRot, 0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(-float8, -float8, float8);
        final float float9 = 0.03125f;
        GlStateManager.translatef(0.0f, -0.626f, 0.0f);
        this.model.render(awo, float7, 0.0f, 0.0f, awo.yRot, awo.xRot, 0.03125f);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        super.render(awo, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final EvokerFangs awo) {
        return EvokerFangsRenderer.TEXTURE_LOCATION;
    }
    
    static {
        TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
    }
}
