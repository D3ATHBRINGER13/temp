package net.minecraft.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public interface RenderLayerParent<T extends Entity, M extends EntityModel<T>> {
    M getModel();
    
    void bindTexture(final ResourceLocation qv);
    
    void setLightColor(final T aio);
}
