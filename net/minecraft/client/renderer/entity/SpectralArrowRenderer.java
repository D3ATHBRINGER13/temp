package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.SpectralArrow;

public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrow> {
    public static final ResourceLocation SPECTRAL_ARROW_LOCATION;
    
    public SpectralArrowRenderer(final EntityRenderDispatcher dsa) {
        super(dsa);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final SpectralArrow axa) {
        return SpectralArrowRenderer.SPECTRAL_ARROW_LOCATION;
    }
    
    static {
        SPECTRAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");
    }
}
