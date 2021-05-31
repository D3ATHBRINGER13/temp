package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.WolfModel;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer extends MobRenderer<Wolf, WolfModel<Wolf>> {
    private static final ResourceLocation WOLF_LOCATION;
    private static final ResourceLocation WOLF_TAME_LOCATION;
    private static final ResourceLocation WOLF_ANGRY_LOCATION;
    
    public WolfRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new WolfModel(), 0.5f);
        this.addLayer(new WolfCollarLayer(this));
    }
    
    @Override
    protected float getBob(final Wolf arz, final float float2) {
        return arz.getTailAngle();
    }
    
    @Override
    public void render(final Wolf arz, final double double2, final double double3, final double double4, final float float5, final float float6) {
        if (arz.isWet()) {
            final float float7 = arz.getBrightness() * arz.getWetShade(float6);
            GlStateManager.color3f(float7, float7, float7);
        }
        super.render(arz, double2, double3, double4, float5, float6);
    }
    
    protected ResourceLocation getTextureLocation(final Wolf arz) {
        if (arz.isTame()) {
            return WolfRenderer.WOLF_TAME_LOCATION;
        }
        if (arz.isAngry()) {
            return WolfRenderer.WOLF_ANGRY_LOCATION;
        }
        return WolfRenderer.WOLF_LOCATION;
    }
    
    static {
        WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");
        WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
        WOLF_ANGRY_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_angry.png");
    }
}
