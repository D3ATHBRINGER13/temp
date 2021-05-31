package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer extends RenderLayer<TropicalFish, EntityModel<TropicalFish>> {
    private final TropicalFishModelA<TropicalFish> modelA;
    private final TropicalFishModelB<TropicalFish> modelB;
    
    public TropicalFishPatternLayer(final RenderLayerParent<TropicalFish, EntityModel<TropicalFish>> dtr) {
        super(dtr);
        this.modelA = new TropicalFishModelA<TropicalFish>(0.008f);
        this.modelB = new TropicalFishModelB<TropicalFish>(0.008f);
    }
    
    @Override
    public void render(final TropicalFish arw, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (arw.isInvisible()) {
            return;
        }
        final EntityModel<TropicalFish> dhh10 = (EntityModel<TropicalFish>)((arw.getBaseVariant() == 0) ? this.modelA : this.modelB);
        this.bindTexture(arw.getPatternTextureLocation());
        final float[] arr11 = arw.getPatternColor();
        GlStateManager.color3f(arr11[0], arr11[1], arr11[2]);
        ((RenderLayer<T, EntityModel<TropicalFish>>)this).getParentModel().copyPropertiesTo(dhh10);
        dhh10.prepareMobModel(arw, float2, float3, float4);
        dhh10.render(arw, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
}
