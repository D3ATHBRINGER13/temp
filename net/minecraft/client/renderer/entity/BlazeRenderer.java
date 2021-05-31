package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer extends MobRenderer<Blaze, BlazeModel<Blaze>> {
    private static final ResourceLocation BLAZE_LOCATION;
    
    public BlazeRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new BlazeModel(), 0.5f);
    }
    
    protected ResourceLocation getTextureLocation(final Blaze auc) {
        return BlazeRenderer.BLAZE_LOCATION;
    }
    
    static {
        BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");
    }
}
