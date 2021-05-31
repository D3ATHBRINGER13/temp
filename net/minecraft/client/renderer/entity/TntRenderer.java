package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.PrimedTnt;

public class TntRenderer extends EntityRenderer<PrimedTnt> {
    public TntRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.shadowRadius = 0.5f;
    }
    
    @Override
    public void render(final PrimedTnt aty, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final BlockRenderDispatcher dnw11 = Minecraft.getInstance().getBlockRenderer();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3 + 0.5f, (float)double4);
        if (aty.getLife() - float6 + 1.0f < 10.0f) {
            float float7 = 1.0f - (aty.getLife() - float6 + 1.0f) / 10.0f;
            float7 = Mth.clamp(float7, 0.0f, 1.0f);
            float7 *= float7;
            float7 *= float7;
            final float float8 = 1.0f + float7 * 0.3f;
            GlStateManager.scalef(float8, float8, float8);
        }
        float float7 = (1.0f - (aty.getLife() - float6 + 1.0f) / 100.0f) * 0.8f;
        this.bindTexture(aty);
        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        dnw11.renderSingleBlock(Blocks.TNT.defaultBlockState(), aty.getBrightness());
        GlStateManager.translatef(0.0f, 0.0f, 1.0f);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(aty));
            dnw11.renderSingleBlock(Blocks.TNT.defaultBlockState(), 1.0f);
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        else if (aty.getLife() / 5 % 2 == 0) {
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, float7);
            GlStateManager.polygonOffset(-3.0f, -3.0f);
            GlStateManager.enablePolygonOffset();
            dnw11.renderSingleBlock(Blocks.TNT.defaultBlockState(), 1.0f);
            GlStateManager.polygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture();
        }
        GlStateManager.popMatrix();
        super.render(aty, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final PrimedTnt aty) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
