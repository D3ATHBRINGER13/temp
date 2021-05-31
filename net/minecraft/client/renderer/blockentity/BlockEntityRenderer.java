package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.Nameable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityRenderer<T extends BlockEntity> {
    public static final ResourceLocation[] BREAKING_LOCATIONS;
    protected BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    
    public void render(final T btw, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final HitResult csf11 = this.blockEntityRenderDispatcher.cameraHitResult;
        if (btw instanceof Nameable && csf11 != null && csf11.getType() == HitResult.Type.BLOCK && btw.getBlockPos().equals(((BlockHitResult)csf11).getBlockPos())) {
            this.setOverlayRenderState(true);
            this.renderNameTag(btw, ((Nameable)btw).getDisplayName().getColoredString(), double2, double3, double4, 12);
            this.setOverlayRenderState(false);
        }
    }
    
    protected void setOverlayRenderState(final boolean boolean1) {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        if (boolean1) {
            GlStateManager.disableTexture();
        }
        else {
            GlStateManager.enableTexture();
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    protected void bindTexture(final ResourceLocation qv) {
        final TextureManager dxc3 = this.blockEntityRenderDispatcher.textureManager;
        if (dxc3 != null) {
            dxc3.bind(qv);
        }
    }
    
    protected Level getLevel() {
        return this.blockEntityRenderDispatcher.level;
    }
    
    public void init(final BlockEntityRenderDispatcher dpd) {
        this.blockEntityRenderDispatcher = dpd;
    }
    
    public Font getFont() {
        return this.blockEntityRenderDispatcher.getFont();
    }
    
    public boolean shouldRenderOffScreen(final T btw) {
        return false;
    }
    
    protected void renderNameTag(final T btw, final String string, final double double3, final double double4, final double double5, final int integer) {
        final Camera cxq11 = this.blockEntityRenderDispatcher.camera;
        final double double6 = btw.distanceToSqr(cxq11.getPosition().x, cxq11.getPosition().y, cxq11.getPosition().z);
        if (double6 > integer * integer) {
            return;
        }
        final float float14 = cxq11.getYRot();
        final float float15 = cxq11.getXRot();
        GameRenderer.renderNameTagInWorld(this.getFont(), string, (float)double3 + 0.5f, (float)double4 + 1.5f, (float)double5 + 0.5f, 0, float14, float15, false);
    }
    
    static {
        BREAKING_LOCATIONS = new ResourceLocation[] { new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_0.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_1.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_2.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_3.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_4.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_5.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_6.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_7.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_8.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_9.getPath() + ".png") };
    }
}
