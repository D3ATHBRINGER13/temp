package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {
    private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION;
    
    public ZombieVillagerRenderer(final EntityRenderDispatcher dsa, final ReloadableResourceManager xg) {
        super(dsa, new ZombieVillagerModel(), 0.5f);
        this.addLayer((RenderLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>)new HumanoidArmorLayer((RenderLayerParent<LivingEntity, HumanoidModel>)this, new ZombieVillagerModel(0.5f, true), new ZombieVillagerModel(1.0f, true)));
        this.addLayer(new VillagerProfessionLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>((RenderLayerParent<ZombieVillager, ZombieVillagerModel<ZombieVillager>>)this, xg, "zombie_villager"));
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final ZombieVillager avn) {
        return ZombieVillagerRenderer.ZOMBIE_VILLAGER_LOCATION;
    }
    
    @Override
    protected void setupRotations(final ZombieVillager avn, final float float2, float float3, final float float4) {
        if (avn.isConverting()) {
            float3 += (float)(Math.cos(avn.tickCount * 3.25) * 3.141592653589793 * 0.25);
        }
        super.setupRotations(avn, float2, float3, float4);
    }
    
    static {
        ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");
    }
}
