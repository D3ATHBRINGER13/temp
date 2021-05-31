package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

public class FireworkEntityRenderer extends EntityRenderer<FireworkRocketEntity> {
    private final ItemRenderer itemRenderer;
    
    public FireworkEntityRenderer(final EntityRenderDispatcher dsa, final ItemRenderer dsv) {
        super(dsa);
        this.itemRenderer = dsv;
    }
    
    @Override
    public void render(final FireworkRocketEntity awr, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.entityRenderDispatcher.options.thirdPersonView == 2) ? -1 : 1) * this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        if (awr.isShotAtAngle()) {
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        }
        else {
            GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(awr));
        }
        this.itemRenderer.renderStatic(awr.getItem(), ItemTransforms.TransformType.GROUND);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(awr, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final FireworkRocketEntity awr) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
