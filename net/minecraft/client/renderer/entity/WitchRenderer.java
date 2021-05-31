package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.WitchModel;
import net.minecraft.world.entity.monster.Witch;

public class WitchRenderer extends MobRenderer<Witch, WitchModel<Witch>> {
    private static final ResourceLocation WITCH_LOCATION;
    
    public WitchRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new WitchModel(0.0f), 0.5f);
        this.addLayer((RenderLayer<Witch, WitchModel<Witch>>)new WitchItemLayer((RenderLayerParent<LivingEntity, WitchModel<LivingEntity>>)this));
    }
    
    @Override
    public void render(final Witch avk, final double double2, final double double3, final double double4, final float float5, final float float6) {
        ((WitchModel)this.model).setHoldingItem(!avk.getMainHandItem().isEmpty());
        super.render(avk, double2, double3, double4, float5, float6);
    }
    
    protected ResourceLocation getTextureLocation(final Witch avk) {
        return WitchRenderer.WITCH_LOCATION;
    }
    
    @Override
    protected void scale(final Witch avk, final float float2) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
    
    static {
        WITCH_LOCATION = new ResourceLocation("textures/entity/witch.png");
    }
}
