package net.minecraft.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.GlStateManager;

public class ThrownItemRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    
    public ThrownItemRenderer(final EntityRenderDispatcher dsa, final ItemRenderer dsv, final float float3) {
        super(dsa);
        this.itemRenderer = dsv;
        this.scale = float3;
    }
    
    public ThrownItemRenderer(final EntityRenderDispatcher dsa, final ItemRenderer dsv) {
        this(dsa, dsv, 1.0f);
    }
    
    @Override
    public void render(final T aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.entityRenderDispatcher.options.thirdPersonView == 2) ? -1 : 1) * this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(aio));
        }
        this.itemRenderer.renderStatic(((ItemSupplier)aio).getItem(), ItemTransforms.TransformType.GROUND);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(aio, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Entity aio) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
