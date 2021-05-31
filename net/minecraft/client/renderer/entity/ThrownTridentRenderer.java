package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.TridentModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class ThrownTridentRenderer extends EntityRenderer<ThrownTrident> {
    public static final ResourceLocation TRIDENT_LOCATION;
    private final TridentModel model;
    
    public ThrownTridentRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new TridentModel();
    }
    
    @Override
    public void render(final ThrownTrident axh, final double double2, final double double3, final double double4, final float float5, final float float6) {
        this.bindTexture(axh);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.rotatef(Mth.lerp(float6, axh.yRotO, axh.yRot) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(Mth.lerp(float6, axh.xRotO, axh.xRot) + 90.0f, 0.0f, 0.0f, 1.0f);
        this.model.render();
        GlStateManager.popMatrix();
        this.renderLeash(axh, double2, double3, double4, float5, float6);
        super.render(axh, double2, double3, double4, float5, float6);
        GlStateManager.enableLighting();
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final ThrownTrident axh) {
        return ThrownTridentRenderer.TRIDENT_LOCATION;
    }
    
    protected void renderLeash(final ThrownTrident axh, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final Entity aio11 = axh.getOwner();
        if (aio11 == null || !axh.isNoPhysics()) {
            return;
        }
        final Tesselator cuz12 = Tesselator.getInstance();
        final BufferBuilder cuw13 = cuz12.getBuilder();
        final double double5 = Mth.lerp(float6 * 0.5f, aio11.yRot, aio11.yRotO) * 0.017453292f;
        final double double6 = Math.cos(double5);
        final double double7 = Math.sin(double5);
        final double double8 = Mth.lerp(float6, aio11.xo, aio11.x);
        final double double9 = Mth.lerp(float6, aio11.yo + aio11.getEyeHeight() * 0.8, aio11.y + aio11.getEyeHeight() * 0.8);
        final double double10 = Mth.lerp(float6, aio11.zo, aio11.z);
        final double double11 = double6 - double7;
        final double double12 = double7 + double6;
        final double double13 = Mth.lerp(float6, axh.xo, axh.x);
        final double double14 = Mth.lerp(float6, axh.yo, axh.y);
        final double double15 = Mth.lerp(float6, axh.zo, axh.z);
        final double double16 = (float)(double8 - double13);
        final double double17 = (float)(double9 - double14);
        final double double18 = (float)(double10 - double15);
        final double double19 = Math.sqrt(double16 * double16 + double17 * double17 + double18 * double18);
        final int integer44 = axh.getId() + axh.tickCount;
        final double double20 = (integer44 + float6) * -0.1;
        final double double21 = Math.min(0.5, double19 / 30.0);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 255.0f, 255.0f);
        cuw13.begin(5, DefaultVertexFormat.POSITION_COLOR);
        final int integer45 = 37;
        final int integer46 = 7 - integer44 % 7;
        final double double22 = 0.1;
        for (int integer47 = 0; integer47 <= 37; ++integer47) {
            final double double23 = integer47 / 37.0;
            final float float7 = 1.0f - (integer47 + integer46) % 7 / 7.0f;
            double double24 = double23 * 2.0 - 1.0;
            double24 = (1.0 - double24 * double24) * double21;
            final double double25 = double2 + double16 * double23 + Math.sin(double23 * 3.141592653589793 * 8.0 + double20) * double11 * double24;
            final double double26 = double3 + double17 * double23 + Math.cos(double23 * 3.141592653589793 * 8.0 + double20) * 0.02 + (0.1 + double24) * 1.0;
            final double double27 = double4 + double18 * double23 + Math.sin(double23 * 3.141592653589793 * 8.0 + double20) * double12 * double24;
            final float float8 = 0.87f * float7 + 0.3f * (1.0f - float7);
            final float float9 = 0.91f * float7 + 0.6f * (1.0f - float7);
            final float float10 = 0.85f * float7 + 0.5f * (1.0f - float7);
            cuw13.vertex(double25, double26, double27).color(float8, float9, float10, 1.0f).endVertex();
            cuw13.vertex(double25 + 0.1 * double24, double26 + 0.1 * double24, double27).color(float8, float9, float10, 1.0f).endVertex();
            if (integer47 > axh.clientSideReturnTridentTickCount * 2) {
                break;
            }
        }
        cuz12.end();
        cuw13.begin(5, DefaultVertexFormat.POSITION_COLOR);
        for (int integer47 = 0; integer47 <= 37; ++integer47) {
            final double double23 = integer47 / 37.0;
            final float float7 = 1.0f - (integer47 + integer46) % 7 / 7.0f;
            double double24 = double23 * 2.0 - 1.0;
            double24 = (1.0 - double24 * double24) * double21;
            final double double25 = double2 + double16 * double23 + Math.sin(double23 * 3.141592653589793 * 8.0 + double20) * double11 * double24;
            final double double26 = double3 + double17 * double23 + Math.cos(double23 * 3.141592653589793 * 8.0 + double20) * 0.01 + (0.1 + double24) * 1.0;
            final double double27 = double4 + double18 * double23 + Math.sin(double23 * 3.141592653589793 * 8.0 + double20) * double12 * double24;
            final float float8 = 0.87f * float7 + 0.3f * (1.0f - float7);
            final float float9 = 0.91f * float7 + 0.6f * (1.0f - float7);
            final float float10 = 0.85f * float7 + 0.5f * (1.0f - float7);
            cuw13.vertex(double25, double26, double27).color(float8, float9, float10, 1.0f).endVertex();
            cuw13.vertex(double25 + 0.1 * double24, double26, double27 + 0.1 * double24).color(float8, float9, float10, 1.0f).endVertex();
            if (integer47 > axh.clientSideReturnTridentTickCount * 2) {
                break;
            }
        }
        cuz12.end();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }
    
    static {
        TRIDENT_LOCATION = new ResourceLocation("textures/entity/trident.png");
    }
}
