package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SkullModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.WitherSkull;

public class WitherSkullRenderer extends EntityRenderer<WitherSkull> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION;
    private static final ResourceLocation WITHER_LOCATION;
    private final SkullModel model;
    
    public WitherSkullRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
        this.model = new SkullModel();
    }
    
    private float rotlerp(final float float1, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - float1; float4 < -180.0f; float4 += 360.0f) {}
        while (float4 >= 180.0f) {
            float4 -= 360.0f;
        }
        return float1 + float3 * float4;
    }
    
    @Override
    public void render(final WitherSkull axi, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        final float float7 = this.rotlerp(axi.yRotO, axi.yRot, float6);
        final float float8 = Mth.lerp(float6, axi.xRotO, axi.xRot);
        GlStateManager.translatef((float)double2, (float)double3, (float)double4);
        final float float9 = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.bindTexture(axi);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(axi));
        }
        this.model.render(0.0f, 0.0f, 0.0f, float7, float8, 0.0625f);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(axi, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final WitherSkull axi) {
        return axi.isDangerous() ? WitherSkullRenderer.WITHER_INVULNERABLE_LOCATION : WitherSkullRenderer.WITHER_LOCATION;
    }
    
    static {
        WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
        WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");
    }
}
