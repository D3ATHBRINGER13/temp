package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.BatModel;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer extends MobRenderer<Bat, BatModel> {
    private static final ResourceLocation BAT_LOCATION;
    
    public BatRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new BatModel(), 0.25f);
    }
    
    protected ResourceLocation getTextureLocation(final Bat aqv) {
        return BatRenderer.BAT_LOCATION;
    }
    
    @Override
    protected void scale(final Bat aqv, final float float2) {
        GlStateManager.scalef(0.35f, 0.35f, 0.35f);
    }
    
    @Override
    protected void setupRotations(final Bat aqv, final float float2, final float float3, final float float4) {
        if (aqv.isResting()) {
            GlStateManager.translatef(0.0f, -0.1f, 0.0f);
        }
        else {
            GlStateManager.translatef(0.0f, Mth.cos(float2 * 0.3f) * 0.1f, 0.0f);
        }
        super.setupRotations(aqv, float2, float3, float4);
    }
    
    static {
        BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");
    }
}
