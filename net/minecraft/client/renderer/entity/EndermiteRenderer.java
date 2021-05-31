package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.world.entity.monster.Endermite;

public class EndermiteRenderer extends MobRenderer<Endermite, EndermiteModel<Endermite>> {
    private static final ResourceLocation ENDERMITE_LOCATION;
    
    public EndermiteRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new EndermiteModel(), 0.3f);
    }
    
    @Override
    protected float getFlipDegrees(final Endermite auj) {
        return 180.0f;
    }
    
    protected ResourceLocation getTextureLocation(final Endermite auj) {
        return EndermiteRenderer.ENDERMITE_LOCATION;
    }
    
    static {
        ENDERMITE_LOCATION = new ResourceLocation("textures/entity/endermite.png");
    }
}
