package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.Entity;

public class DefaultRenderer extends EntityRenderer<Entity> {
    public DefaultRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    public void render(final Entity aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        EntityRenderer.render(aio.getBoundingBox(), double2 - aio.xOld, double3 - aio.yOld, double4 - aio.zOld);
        GlStateManager.popMatrix();
        super.render(aio, double2, double3, double4, float5, float6);
    }
    
    @Nullable
    @Override
    protected ResourceLocation getTextureLocation(final Entity aio) {
        return null;
    }
}
