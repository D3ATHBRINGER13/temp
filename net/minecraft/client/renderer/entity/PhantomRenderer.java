package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomRenderer extends MobRenderer<Phantom, PhantomModel<Phantom>> {
    private static final ResourceLocation PHANTOM_LOCATION;
    
    public PhantomRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new PhantomModel(), 0.75f);
        this.addLayer((RenderLayer<Phantom, PhantomModel<Phantom>>)new PhantomEyesLayer((RenderLayerParent<Entity, PhantomModel<Entity>>)this));
    }
    
    protected ResourceLocation getTextureLocation(final Phantom auu) {
        return PhantomRenderer.PHANTOM_LOCATION;
    }
    
    @Override
    protected void scale(final Phantom auu, final float float2) {
        final int integer4 = auu.getPhantomSize();
        final float float3 = 1.0f + 0.15f * integer4;
        GlStateManager.scalef(float3, float3, float3);
        GlStateManager.translatef(0.0f, 1.3125f, 0.1875f);
    }
    
    @Override
    protected void setupRotations(final Phantom auu, final float float2, final float float3, final float float4) {
        super.setupRotations(auu, float2, float3, float4);
        GlStateManager.rotatef(auu.xRot, 1.0f, 0.0f, 0.0f);
    }
    
    static {
        PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");
    }
}
