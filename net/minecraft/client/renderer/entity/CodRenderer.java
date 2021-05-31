package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.CodModel;
import net.minecraft.world.entity.animal.Cod;

public class CodRenderer extends MobRenderer<Cod, CodModel<Cod>> {
    private static final ResourceLocation COD_LOCATION;
    
    public CodRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new CodModel(), 0.3f);
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Cod ard) {
        return CodRenderer.COD_LOCATION;
    }
    
    @Override
    protected void setupRotations(final Cod ard, final float float2, final float float3, final float float4) {
        super.setupRotations(ard, float2, float3, float4);
        final float float5 = 4.3f * Mth.sin(0.6f * float2);
        GlStateManager.rotatef(float5, 0.0f, 1.0f, 0.0f);
        if (!ard.isInWater()) {
            GlStateManager.translatef(0.1f, 0.1f, -0.1f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
    
    static {
        COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");
    }
}
