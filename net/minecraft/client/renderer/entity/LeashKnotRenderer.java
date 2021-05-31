package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

public class LeashKnotRenderer extends EntityRenderer<LeashFenceKnotEntity> {
    private static final ResourceLocation KNOT_LOCATION;
    private final LeashKnotModel<LeashFenceKnotEntity> model;
    
    public LeashKnotRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new LeashKnotModel<LeashFenceKnotEntity>();
    }
    
    @Override
    public void render(final LeashFenceKnotEntity ato, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        final float float7 = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.bindTexture(ato);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(ato));
        }
        this.model.render(ato, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(ato, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final LeashFenceKnotEntity ato) {
        return LeashKnotRenderer.KNOT_LOCATION;
    }
    
    static {
        KNOT_LOCATION = new ResourceLocation("textures/entity/lead_knot.png");
    }
}
