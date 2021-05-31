package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer extends MobRenderer<Salmon, SalmonModel<Salmon>> {
    private static final ResourceLocation SALMON_LOCATION;
    
    public SalmonRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new SalmonModel(), 0.4f);
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Salmon arr) {
        return SalmonRenderer.SALMON_LOCATION;
    }
    
    @Override
    protected void setupRotations(final Salmon arr, final float float2, final float float3, final float float4) {
        super.setupRotations(arr, float2, float3, float4);
        float float5 = 1.0f;
        float float6 = 1.0f;
        if (!arr.isInWater()) {
            float5 = 1.3f;
            float6 = 1.7f;
        }
        final float float7 = float5 * 4.3f * Mth.sin(float6 * 0.6f * float2);
        GlStateManager.rotatef(float7, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, -0.4f);
        if (!arr.isInWater()) {
            GlStateManager.translatef(0.2f, 0.1f, 0.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
    
    static {
        SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");
    }
}
