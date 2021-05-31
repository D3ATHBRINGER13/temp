package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.dragon.EndCrystalModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class EndCrystalRenderer extends EntityRenderer<EndCrystal> {
    private static final ResourceLocation END_CRYSTAL_LOCATION;
    private final EntityModel<EndCrystal> model;
    private final EntityModel<EndCrystal> modelWithoutBottom;
    
    public EndCrystalRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new EndCrystalModel<EndCrystal>(0.0f, true);
        this.modelWithoutBottom = new EndCrystalModel<EndCrystal>(0.0f, false);
        this.shadowRadius = 0.5f;
    }
    
    @Override
    public void render(final EndCrystal aso, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final float float7 = aso.time + float6;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        this.bindTexture(EndCrystalRenderer.END_CRYSTAL_LOCATION);
        float float8 = Mth.sin(float7 * 0.2f) / 2.0f + 0.5f;
        float8 += float8 * float8;
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(aso));
        }
        if (aso.showsBottom()) {
            this.model.render(aso, 0.0f, float7 * 3.0f, float8 * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        else {
            this.modelWithoutBottom.render(aso, 0.0f, float7 * 3.0f, float8 * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        final BlockPos ew13 = aso.getBeamTarget();
        if (ew13 != null) {
            this.bindTexture(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
            final float float9 = ew13.getX() + 0.5f;
            final float float10 = ew13.getY() + 0.5f;
            final float float11 = ew13.getZ() + 0.5f;
            final double double5 = float9 - aso.x;
            final double double6 = float10 - aso.y;
            final double double7 = float11 - aso.z;
            EnderDragonRenderer.renderCrystalBeams(double2 + double5, double3 - 0.3 + float8 * 0.4f + double6, double4 + double7, float6, float9, float10, float11, aso.time, aso.x, aso.y, aso.z);
        }
        super.render(aso, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final EndCrystal aso) {
        return EndCrystalRenderer.END_CRYSTAL_LOCATION;
    }
    
    @Override
    public boolean shouldRender(final EndCrystal aso, final Culler dqe, final double double3, final double double4, final double double5) {
        return super.shouldRender(aso, dqe, double3, double4, double5) || aso.getBeamTarget() != null;
    }
    
    static {
        END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
    }
}
