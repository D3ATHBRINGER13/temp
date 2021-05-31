package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BoatModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatRenderer extends EntityRenderer<Boat> {
    private static final ResourceLocation[] BOAT_TEXTURE_LOCATIONS;
    protected final BoatModel model;
    
    public BoatRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new BoatModel();
        this.shadowRadius = 0.8f;
    }
    
    @Override
    public void render(final Boat axw, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        this.setupTranslation(double2, double3, double4);
        this.setupRotation(axw, float5, float6);
        this.bindTexture(axw);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(axw));
        }
        this.model.render(axw, float6, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(axw, double2, double3, double4, float5, float6);
    }
    
    public void setupRotation(final Boat axw, final float float2, final float float3) {
        GlStateManager.rotatef(180.0f - float2, 0.0f, 1.0f, 0.0f);
        final float float4 = axw.getHurtTime() - float3;
        float float5 = axw.getDamage() - float3;
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        if (float4 > 0.0f) {
            GlStateManager.rotatef(Mth.sin(float4) * float4 * float5 / 10.0f * axw.getHurtDir(), 1.0f, 0.0f, 0.0f);
        }
        final float float6 = axw.getBubbleAngle(float3);
        if (!Mth.equal(float6, 0.0f)) {
            GlStateManager.rotatef(axw.getBubbleAngle(float3), 1.0f, 0.0f, 1.0f);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
    }
    
    public void setupTranslation(final double double1, final double double2, final double double3) {
        GlStateManager.translatef((float)double1, (float)double2 + 0.375f, (float)double3);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Boat axw) {
        return BoatRenderer.BOAT_TEXTURE_LOCATIONS[axw.getBoatType().ordinal()];
    }
    
    @Override
    public boolean hasSecondPass() {
        return true;
    }
    
    @Override
    public void renderSecondPass(final Boat axw, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        this.setupTranslation(double2, double3, double4);
        this.setupRotation(axw, float5, float6);
        this.bindTexture(axw);
        this.model.renderSecondPass(axw, float6, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        BOAT_TEXTURE_LOCATIONS = new ResourceLocation[] { new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png") };
    }
}
