package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class LlamaSpitRenderer extends EntityRenderer<LlamaSpit> {
    private static final ResourceLocation LLAMA_SPIT_LOCATION;
    private final LlamaSpitModel<LlamaSpit> model;
    
    public LlamaSpitRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new LlamaSpitModel<LlamaSpit>();
    }
    
    @Override
    public void render(final LlamaSpit awu, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3 + 0.15f, (float)double4);
        GlStateManager.rotatef(Mth.lerp(float6, awu.yRotO, awu.yRot) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(Mth.lerp(float6, awu.xRotO, awu.xRot), 0.0f, 0.0f, 1.0f);
        this.bindTexture(awu);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(awu));
        }
        this.model.render(awu, float6, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(awu, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final LlamaSpit awu) {
        return LlamaSpitRenderer.LLAMA_SPIT_LOCATION;
    }
    
    static {
        LLAMA_SPIT_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
    }
}
