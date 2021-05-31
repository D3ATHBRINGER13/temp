package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearRenderer extends MobRenderer<PolarBear, PolarBearModel<PolarBear>> {
    private static final ResourceLocation BEAR_LOCATION;
    
    public PolarBearRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new PolarBearModel(), 0.9f);
    }
    
    protected ResourceLocation getTextureLocation(final PolarBear aro) {
        return PolarBearRenderer.BEAR_LOCATION;
    }
    
    @Override
    protected void scale(final PolarBear aro, final float float2) {
        GlStateManager.scalef(1.2f, 1.2f, 1.2f);
        super.scale(aro, float2);
    }
    
    static {
        BEAR_LOCATION = new ResourceLocation("textures/entity/bear/polarbear.png");
    }
}
