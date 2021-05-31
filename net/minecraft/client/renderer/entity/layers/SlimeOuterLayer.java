package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.world.entity.Entity;

public class SlimeOuterLayer<T extends Entity> extends RenderLayer<T, SlimeModel<T>> {
    private final EntityModel<T> model;
    
    public SlimeOuterLayer(final RenderLayerParent<T, SlimeModel<T>> dtr) {
        super(dtr);
        this.model = new SlimeModel<T>(0);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (aio.isInvisible()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.render(aio, float2, float3, float5, float6, float7, float8);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
}
