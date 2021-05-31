package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;

public class VindicatorRenderer extends IllagerRenderer<Vindicator> {
    private static final ResourceLocation VINDICATOR;
    
    public VindicatorRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new IllagerModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addLayer((RenderLayer<T, IllagerModel<T>>)new ItemInHandLayer<Vindicator, IllagerModel<Vindicator>>(this) {
            @Override
            public void render(final Vindicator avj, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
                if (avj.isAggressive()) {
                    super.render(avj, float2, float3, float4, float5, float6, float7, float8);
                }
            }
        });
    }
    
    protected ResourceLocation getTextureLocation(final Vindicator avj) {
        return VindicatorRenderer.VINDICATOR;
    }
    
    static {
        VINDICATOR = new ResourceLocation("textures/entity/illager/vindicator.png");
    }
}
