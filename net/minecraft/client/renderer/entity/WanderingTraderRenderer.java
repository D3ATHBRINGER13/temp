package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.VillagerTradeItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WanderingTraderRenderer extends MobRenderer<WanderingTrader, VillagerModel<WanderingTrader>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN;
    
    public WanderingTraderRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new VillagerModel(0.0f), 0.5f);
        this.addLayer(new CustomHeadLayer<WanderingTrader, VillagerModel<WanderingTrader>>(this));
        this.addLayer((RenderLayer<WanderingTrader, VillagerModel<WanderingTrader>>)new VillagerTradeItemLayer((RenderLayerParent<LivingEntity, VillagerModel<LivingEntity>>)this));
    }
    
    protected ResourceLocation getTextureLocation(final WanderingTrader avz) {
        return WanderingTraderRenderer.VILLAGER_BASE_SKIN;
    }
    
    @Override
    protected void scale(final WanderingTrader avz, final float float2) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
    
    static {
        VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/wandering_trader.png");
    }
}
