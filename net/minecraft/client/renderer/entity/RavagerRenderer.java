package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerRenderer extends MobRenderer<Ravager, RavagerModel> {
    private static final ResourceLocation TEXTURE_LOCATION;
    
    public RavagerRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new RavagerModel(), 1.1f);
    }
    
    protected ResourceLocation getTextureLocation(final Ravager auz) {
        return RavagerRenderer.TEXTURE_LOCATION;
    }
    
    static {
        TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");
    }
}
