package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.VillagerTradeItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer extends MobRenderer<Villager, VillagerModel<Villager>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN;
    
    public VillagerRenderer(final EntityRenderDispatcher dsa, final ReloadableResourceManager xg) {
        super(dsa, new VillagerModel(0.0f), 0.5f);
        this.addLayer(new CustomHeadLayer<Villager, VillagerModel<Villager>>(this));
        this.addLayer(new VillagerProfessionLayer<Villager, VillagerModel<Villager>>((RenderLayerParent<Villager, VillagerModel<Villager>>)this, xg, "villager"));
        this.addLayer((RenderLayer<Villager, VillagerModel<Villager>>)new VillagerTradeItemLayer((RenderLayerParent<LivingEntity, VillagerModel<LivingEntity>>)this));
    }
    
    protected ResourceLocation getTextureLocation(final Villager avt) {
        return VillagerRenderer.VILLAGER_BASE_SKIN;
    }
    
    @Override
    protected void scale(final Villager avt, final float float2) {
        float float3 = 0.9375f;
        if (avt.isBaby()) {
            float3 *= 0.5;
            this.shadowRadius = 0.25f;
        }
        else {
            this.shadowRadius = 0.5f;
        }
        GlStateManager.scalef(float3, float3, float3);
    }
    
    static {
        VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");
    }
}
