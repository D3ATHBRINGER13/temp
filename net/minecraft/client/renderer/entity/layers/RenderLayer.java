package net.minecraft.client.renderer.entity.layers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public abstract class RenderLayer<T extends Entity, M extends EntityModel<T>> {
    private final RenderLayerParent<T, M> renderer;
    
    public RenderLayer(final RenderLayerParent<T, M> dtr) {
        this.renderer = dtr;
    }
    
    public M getParentModel() {
        return this.renderer.getModel();
    }
    
    public void bindTexture(final ResourceLocation qv) {
        this.renderer.bindTexture(qv);
    }
    
    public void setLightColor(final T aio) {
        this.renderer.setLightColor(aio);
    }
    
    public abstract void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8);
    
    public abstract boolean colorsOnDamage();
}
