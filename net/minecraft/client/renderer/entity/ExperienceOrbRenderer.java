package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;

public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb> {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION;
    
    public ExperienceOrbRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }
    
    @Override
    public void render(final ExperienceOrb aiu, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (this.solidRender || Minecraft.getInstance().getEntityRenderDispatcher().options == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        this.bindTexture(aiu);
        Lighting.turnOn();
        final int integer11 = aiu.getIcon();
        final float float7 = (integer11 % 4 * 16 + 0) / 64.0f;
        final float float8 = (integer11 % 4 * 16 + 16) / 64.0f;
        final float float9 = (integer11 / 4 * 16 + 0) / 64.0f;
        final float float10 = (integer11 / 4 * 16 + 16) / 64.0f;
        final float float11 = 1.0f;
        final float float12 = 0.5f;
        final float float13 = 0.25f;
        final int integer12 = aiu.getLightColor();
        final int integer13 = integer12 % 65536;
        final int integer14 = integer12 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer13, (float)integer14);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float14 = 255.0f;
        final float float15 = (aiu.tickCount + float6) / 2.0f;
        final int integer15 = (int)((Mth.sin(float15 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        final int integer16 = 255;
        final int integer17 = (int)((Mth.sin(float15 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        GlStateManager.translatef(0.0f, 0.1f, 0.0f);
        GlStateManager.rotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.entityRenderDispatcher.options.thirdPersonView == 2) ? -1 : 1) * -this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        final float float16 = 0.3f;
        GlStateManager.scalef(0.3f, 0.3f, 0.3f);
        final Tesselator cuz28 = Tesselator.getInstance();
        final BufferBuilder cuw29 = cuz28.getBuilder();
        cuw29.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        cuw29.vertex(-0.5, -0.25, 0.0).uv(float7, float10).color(integer15, 255, integer17, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw29.vertex(0.5, -0.25, 0.0).uv(float8, float10).color(integer15, 255, integer17, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw29.vertex(0.5, 0.75, 0.0).uv(float8, float9).color(integer15, 255, integer17, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw29.vertex(-0.5, 0.75, 0.0).uv(float7, float9).color(integer15, 255, integer17, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuz28.end();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(aiu, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final ExperienceOrb aiu) {
        return ExperienceOrbRenderer.EXPERIENCE_ORB_LOCATION;
    }
    
    static {
        EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
    }
}
