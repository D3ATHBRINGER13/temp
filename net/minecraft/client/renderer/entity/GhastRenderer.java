package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.GhastModel;
import net.minecraft.world.entity.monster.Ghast;

public class GhastRenderer extends MobRenderer<Ghast, GhastModel<Ghast>> {
    private static final ResourceLocation GHAST_LOCATION;
    private static final ResourceLocation GHAST_SHOOTING_LOCATION;
    
    public GhastRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new GhastModel(), 1.5f);
    }
    
    protected ResourceLocation getTextureLocation(final Ghast aum) {
        if (aum.isCharging()) {
            return GhastRenderer.GHAST_SHOOTING_LOCATION;
        }
        return GhastRenderer.GHAST_LOCATION;
    }
    
    @Override
    protected void scale(final Ghast aum, final float float2) {
        final float float3 = 1.0f;
        final float float4 = 4.5f;
        final float float5 = 4.5f;
        GlStateManager.scalef(4.5f, 4.5f, 4.5f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    static {
        GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
        GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");
    }
}
