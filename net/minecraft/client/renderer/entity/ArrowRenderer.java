package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.projectile.AbstractArrow;

public abstract class ArrowRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
    public ArrowRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    public void render(final T awk, final double double2, final double double3, final double double4, final float float5, final float float6) {
        this.bindTexture(awk);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.rotatef(Mth.lerp(float6, awk.yRotO, awk.yRot) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(Mth.lerp(float6, awk.xRotO, awk.xRot), 0.0f, 0.0f, 1.0f);
        final Tesselator cuz11 = Tesselator.getInstance();
        final BufferBuilder cuw12 = cuz11.getBuilder();
        final int integer13 = 0;
        final float float7 = 0.0f;
        final float float8 = 0.5f;
        final float float9 = 0.0f;
        final float float10 = 0.15625f;
        final float float11 = 0.0f;
        final float float12 = 0.15625f;
        final float float13 = 0.15625f;
        final float float14 = 0.3125f;
        final float float15 = 0.05625f;
        GlStateManager.enableRescaleNormal();
        final float float16 = awk.shakeTime - float6;
        if (float16 > 0.0f) {
            final float float17 = -Mth.sin(float16 * 3.0f) * float16;
            GlStateManager.rotatef(float17, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(0.05625f, 0.05625f, 0.05625f);
        GlStateManager.translatef(-4.0f, 0.0f, 0.0f);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(awk));
        }
        GlStateManager.normal3f(0.05625f, 0.0f, 0.0f);
        cuw12.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw12.vertex(-7.0, -2.0, -2.0).uv(0.0, 0.15625).endVertex();
        cuw12.vertex(-7.0, -2.0, 2.0).uv(0.15625, 0.15625).endVertex();
        cuw12.vertex(-7.0, 2.0, 2.0).uv(0.15625, 0.3125).endVertex();
        cuw12.vertex(-7.0, 2.0, -2.0).uv(0.0, 0.3125).endVertex();
        cuz11.end();
        GlStateManager.normal3f(-0.05625f, 0.0f, 0.0f);
        cuw12.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw12.vertex(-7.0, 2.0, -2.0).uv(0.0, 0.15625).endVertex();
        cuw12.vertex(-7.0, 2.0, 2.0).uv(0.15625, 0.15625).endVertex();
        cuw12.vertex(-7.0, -2.0, 2.0).uv(0.15625, 0.3125).endVertex();
        cuw12.vertex(-7.0, -2.0, -2.0).uv(0.0, 0.3125).endVertex();
        cuz11.end();
        for (int integer14 = 0; integer14 < 4; ++integer14) {
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.normal3f(0.0f, 0.0f, 0.05625f);
            cuw12.begin(7, DefaultVertexFormat.POSITION_TEX);
            cuw12.vertex(-8.0, -2.0, 0.0).uv(0.0, 0.0).endVertex();
            cuw12.vertex(8.0, -2.0, 0.0).uv(0.5, 0.0).endVertex();
            cuw12.vertex(8.0, 2.0, 0.0).uv(0.5, 0.15625).endVertex();
            cuw12.vertex(-8.0, 2.0, 0.0).uv(0.0, 0.15625).endVertex();
            cuz11.end();
        }
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.render(awk, double2, double3, double4, float5, float6);
    }
}
