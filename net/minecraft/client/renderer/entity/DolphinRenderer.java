package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinRenderer extends MobRenderer<Dolphin, DolphinModel<Dolphin>> {
    private static final ResourceLocation DOLPHIN_LOCATION;
    
    public DolphinRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new DolphinModel(), 0.7f);
        this.addLayer(new DolphinCarryingItemLayer(this));
    }
    
    protected ResourceLocation getTextureLocation(final Dolphin arf) {
        return DolphinRenderer.DOLPHIN_LOCATION;
    }
    
    @Override
    protected void scale(final Dolphin arf, final float float2) {
        final float float3 = 1.0f;
        GlStateManager.scalef(1.0f, 1.0f, 1.0f);
    }
    
    @Override
    protected void setupRotations(final Dolphin arf, final float float2, final float float3, final float float4) {
        super.setupRotations(arf, float2, float3, float4);
    }
    
    static {
        DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");
    }
}
