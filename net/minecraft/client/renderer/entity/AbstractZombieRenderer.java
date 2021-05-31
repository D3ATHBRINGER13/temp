package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>> extends HumanoidMobRenderer<T, M> {
    private static final ResourceLocation ZOMBIE_LOCATION;
    
    protected AbstractZombieRenderer(final EntityRenderDispatcher dsa, final M djn2, final M djn3, final M djn4) {
        super(dsa, djn2, 0.5f);
        this.addLayer((RenderLayer<T, M>)new HumanoidArmorLayer((RenderLayerParent<LivingEntity, HumanoidModel>)this, djn3, djn4));
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final Zombie avm) {
        return AbstractZombieRenderer.ZOMBIE_LOCATION;
    }
    
    @Override
    protected void setupRotations(final T avm, final float float2, float float3, final float float4) {
        if (avm.isUnderWaterConverting()) {
            float3 += (float)(Math.cos(avm.tickCount * 3.25) * 3.141592653589793 * 0.25);
        }
        super.setupRotations(avm, float2, float3, float4);
    }
    
    static {
        ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");
    }
}
