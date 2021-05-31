package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.world.entity.monster.AbstractIllager;

public abstract class IllagerRenderer<T extends AbstractIllager> extends MobRenderer<T, IllagerModel<T>> {
    protected IllagerRenderer(final EntityRenderDispatcher dsa, final IllagerModel<T> dhq, final float float3) {
        super(dsa, dhq, float3);
        this.addLayer(new CustomHeadLayer<T, IllagerModel<T>>(this));
    }
    
    public IllagerRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new IllagerModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addLayer(new CustomHeadLayer<T, IllagerModel<T>>(this));
    }
    
    @Override
    protected void scale(final T aua, final float float2) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
}
