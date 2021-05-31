package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.EntityModel;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SheepModel;
import net.minecraft.world.entity.animal.Sheep;

public class SheepFurLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
    private static final ResourceLocation SHEEP_FUR_LOCATION;
    private final SheepFurModel<Sheep> model;
    
    public SheepFurLayer(final RenderLayerParent<Sheep, SheepModel<Sheep>> dtr) {
        super(dtr);
        this.model = new SheepFurModel<Sheep>();
    }
    
    @Override
    public void render(final Sheep ars, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (ars.isSheared() || ars.isInvisible()) {
            return;
        }
        this.bindTexture(SheepFurLayer.SHEEP_FUR_LOCATION);
        if (ars.hasCustomName() && "jeb_".equals(ars.getName().getContents())) {
            final int integer10 = 25;
            final int integer11 = ars.tickCount / 25 + ars.getId();
            final int integer12 = DyeColor.values().length;
            final int integer13 = integer11 % integer12;
            final int integer14 = (integer11 + 1) % integer12;
            final float float9 = (ars.tickCount % 25 + float4) / 25.0f;
            final float[] arr16 = Sheep.getColorArray(DyeColor.byId(integer13));
            final float[] arr17 = Sheep.getColorArray(DyeColor.byId(integer14));
            GlStateManager.color3f(arr16[0] * (1.0f - float9) + arr17[0] * float9, arr16[1] * (1.0f - float9) + arr17[1] * float9, arr16[2] * (1.0f - float9) + arr17[2] * float9);
        }
        else {
            final float[] arr18 = Sheep.getColorArray(ars.getColor());
            GlStateManager.color3f(arr18[0], arr18[1], arr18[2]);
        }
        ((RenderLayer<T, SheepModel<Sheep>>)this).getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(ars, float2, float3, float4);
        this.model.render(ars, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
    
    static {
        SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    }
}
