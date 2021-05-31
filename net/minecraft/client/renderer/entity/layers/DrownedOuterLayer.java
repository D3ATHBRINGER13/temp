package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedOuterLayer<T extends Zombie> extends RenderLayer<T, DrownedModel<T>> {
    private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION;
    private final DrownedModel<T> model;
    
    public DrownedOuterLayer(final RenderLayerParent<T, DrownedModel<T>> dtr) {
        super(dtr);
        this.model = new DrownedModel<T>(0.25f, 0.0f, 64, 64);
    }
    
    @Override
    public void render(final T avm, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (avm.isInvisible()) {
            return;
        }
        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(avm, float2, float3, float4);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(DrownedOuterLayer.DROWNED_OUTER_LAYER_LOCATION);
        this.model.render(avm, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
    
    static {
        DROWNED_OUTER_LAYER_LOCATION = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
    }
}
