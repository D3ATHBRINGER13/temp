package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.DragonFireball;

public class DragonFireballRenderer extends EntityRenderer<DragonFireball> {
    private static final ResourceLocation TEXTURE_LOCATION;
    
    public DragonFireballRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    public void render(final DragonFireball awn, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        this.bindTexture(awn);
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        final Tesselator cuz11 = Tesselator.getInstance();
        final BufferBuilder cuw12 = cuz11.getBuilder();
        final float float7 = 1.0f;
        final float float8 = 0.5f;
        final float float9 = 0.25f;
        GlStateManager.rotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.entityRenderDispatcher.options.thirdPersonView == 2) ? -1 : 1) * -this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(awn));
        }
        cuw12.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
        cuw12.vertex(-0.5, -0.25, 0.0).uv(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw12.vertex(0.5, -0.25, 0.0).uv(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw12.vertex(0.5, 0.75, 0.0).uv(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuw12.vertex(-0.5, 0.75, 0.0).uv(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        cuz11.end();
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(awn, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final DragonFireball awn) {
        return DragonFireballRenderer.TEXTURE_LOCATION;
    }
    
    static {
        TEXTURE_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    }
}
