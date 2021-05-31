package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
    private static final ResourceLocation GOLEM_LOCATION;
    
    public IronGolemRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new IronGolemModel(), 0.7f);
        this.addLayer(new IronGolemFlowerLayer(this));
    }
    
    protected ResourceLocation getTextureLocation(final IronGolem ari) {
        return IronGolemRenderer.GOLEM_LOCATION;
    }
    
    @Override
    protected void setupRotations(final IronGolem ari, final float float2, final float float3, final float float4) {
        super.setupRotations(ari, float2, float3, float4);
        if (ari.animationSpeed < 0.01) {
            return;
        }
        final float float5 = 13.0f;
        final float float6 = ari.animationPosition - ari.animationSpeed * (1.0f - float4) + 6.0f;
        final float float7 = (Math.abs(float6 % 13.0f - 6.5f) - 3.25f) / 3.25f;
        GlStateManager.rotatef(6.5f * float7, 0.0f, 0.0f, 1.0f);
    }
    
    static {
        GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem.png");
    }
}
