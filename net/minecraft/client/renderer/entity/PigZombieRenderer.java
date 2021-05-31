package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.PigZombie;

public class PigZombieRenderer extends HumanoidMobRenderer<PigZombie, ZombieModel<PigZombie>> {
    private static final ResourceLocation ZOMBIE_PIGMAN_LOCATION;
    
    public PigZombieRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new ZombieModel(), 0.5f);
        this.addLayer((RenderLayer<PigZombie, ZombieModel<PigZombie>>)new HumanoidArmorLayer((RenderLayerParent<LivingEntity, HumanoidModel>)this, new ZombieModel(0.5f, true), new ZombieModel(1.0f, true)));
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final PigZombie auv) {
        return PigZombieRenderer.ZOMBIE_PIGMAN_LOCATION;
    }
    
    static {
        ZOMBIE_PIGMAN_LOCATION = new ResourceLocation("textures/entity/zombie_pigman.png");
    }
}
