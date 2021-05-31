package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.SpellcasterIllager;

public class EvokerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T> {
    private static final ResourceLocation EVOKER_ILLAGER;
    
    public EvokerRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new IllagerModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addLayer((RenderLayer<T, IllagerModel<T>>)new ItemInHandLayer<T, IllagerModel<T>>(this) {
            @Override
            public void render(final T avf, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
                if (avf.isCastingSpell()) {
                    super.render(avf, float2, float3, float4, float5, float6, float7, float8);
                }
            }
        });
    }
    
    protected ResourceLocation getTextureLocation(final T avf) {
        return EvokerRenderer.EVOKER_ILLAGER;
    }
    
    static {
        EVOKER_ILLAGER = new ResourceLocation("textures/entity/illager/evoker.png");
    }
}
