package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AreaEffectCloud;

public class AreaEffectCloudRenderer extends EntityRenderer<AreaEffectCloud> {
    public AreaEffectCloudRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Nullable
    @Override
    protected ResourceLocation getTextureLocation(final AreaEffectCloud ain) {
        return null;
    }
}
