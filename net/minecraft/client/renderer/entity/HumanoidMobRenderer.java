package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Mob;

public class HumanoidMobRenderer<T extends Mob, M extends HumanoidModel<T>> extends MobRenderer<T, M> {
    private static final ResourceLocation DEFAULT_LOCATION;
    
    public HumanoidMobRenderer(final EntityRenderDispatcher dsa, final M dhp, final float float3) {
        super(dsa, dhp, float3);
        this.addLayer(new CustomHeadLayer<T, M>(this));
        this.addLayer(new ElytraLayer<T, M>(this));
        this.addLayer(new ItemInHandLayer<T, M>(this));
    }
    
    protected ResourceLocation getTextureLocation(final T aiy) {
        return HumanoidMobRenderer.DEFAULT_LOCATION;
    }
    
    static {
        DEFAULT_LOCATION = new ResourceLocation("textures/entity/steve.png");
    }
}
