package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.CatModel;
import net.minecraft.world.entity.animal.Cat;

public class CatCollarLayer extends RenderLayer<Cat, CatModel<Cat>> {
    private static final ResourceLocation CAT_COLLAR_LOCATION;
    private final CatModel<Cat> catModel;
    
    public CatCollarLayer(final RenderLayerParent<Cat, CatModel<Cat>> dtr) {
        super(dtr);
        this.catModel = new CatModel<Cat>(0.01f);
    }
    
    @Override
    public void render(final Cat arb, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!arb.isTame() || arb.isInvisible()) {
            return;
        }
        this.bindTexture(CatCollarLayer.CAT_COLLAR_LOCATION);
        final float[] arr10 = arb.getCollarColor().getTextureDiffuseColors();
        GlStateManager.color3f(arr10[0], arr10[1], arr10[2]);
        ((RenderLayer<T, CatModel<Cat>>)this).getParentModel().copyPropertiesTo(this.catModel);
        this.catModel.prepareMobModel(arb, float2, float3, float4);
        this.catModel.render(arb, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
    
    static {
        CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
    }
}
