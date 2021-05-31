package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.VexModel;
import net.minecraft.world.entity.monster.Vex;

public class VexRenderer extends HumanoidMobRenderer<Vex, VexModel> {
    private static final ResourceLocation VEX_LOCATION;
    private static final ResourceLocation VEX_CHARGING_LOCATION;
    
    public VexRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new VexModel(), 0.3f);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Vex avi) {
        if (avi.isCharging()) {
            return VexRenderer.VEX_CHARGING_LOCATION;
        }
        return VexRenderer.VEX_LOCATION;
    }
    
    @Override
    protected void scale(final Vex avi, final float float2) {
        GlStateManager.scalef(0.4f, 0.4f, 0.4f);
    }
    
    static {
        VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
        VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");
    }
}
