package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.world.entity.animal.SnowGolem;

public class SnowGolemRenderer extends MobRenderer<SnowGolem, SnowGolemModel<SnowGolem>> {
    private static final ResourceLocation SNOW_GOLEM_LOCATION;
    
    public SnowGolemRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new SnowGolemModel(), 0.5f);
        this.addLayer(new SnowGolemHeadLayer(this));
    }
    
    protected ResourceLocation getTextureLocation(final SnowGolem aru) {
        return SnowGolemRenderer.SNOW_GOLEM_LOCATION;
    }
    
    static {
        SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");
    }
}
