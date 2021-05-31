package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, M extends HorseModel<T>> extends MobRenderer<T, M> {
    private final float scale;
    
    public AbstractHorseRenderer(final EntityRenderDispatcher dsa, final M dhn, final float float3) {
        super(dsa, dhn, 0.75f);
        this.scale = float3;
    }
    
    @Override
    protected void scale(final T asb, final float float2) {
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        super.scale(asb, float2);
    }
}
