package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EntityModel;

public class StrayClothingLayer<T extends Mob, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation STRAY_CLOTHES_LOCATION;
    private final SkeletonModel<T> layerModel;
    
    public StrayClothingLayer(final RenderLayerParent<T, M> dtr) {
        super(dtr);
        this.layerModel = new SkeletonModel<T>(0.25f, true);
    }
    
    @Override
    public void render(final T aiy, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.getParentModel().copyPropertiesTo(this.layerModel);
        this.layerModel.prepareMobModel(aiy, float2, float3, float4);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(StrayClothingLayer.STRAY_CLOTHES_LOCATION);
        this.layerModel.render(aiy, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
    
    static {
        STRAY_CLOTHES_LOCATION = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
    }
}
