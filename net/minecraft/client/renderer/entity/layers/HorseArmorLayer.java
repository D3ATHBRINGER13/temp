package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.Horse;

public class HorseArmorLayer extends RenderLayer<Horse, HorseModel<Horse>> {
    private final HorseModel<Horse> model;
    
    public HorseArmorLayer(final RenderLayerParent<Horse, HorseModel<Horse>> dtr) {
        super(dtr);
        this.model = new HorseModel<Horse>(0.1f);
    }
    
    @Override
    public void render(final Horse asd, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = asd.getArmor();
        if (bcj10.getItem() instanceof HorseArmorItem) {
            final HorseArmorItem bcd11 = (HorseArmorItem)bcj10.getItem();
            ((RenderLayer<T, HorseModel<Horse>>)this).getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(asd, float2, float3, float4);
            this.bindTexture(bcd11.getTexture());
            if (bcd11 instanceof DyeableHorseArmorItem) {
                final int integer12 = ((DyeableHorseArmorItem)bcd11).getColor(bcj10);
                final float float9 = (integer12 >> 16 & 0xFF) / 255.0f;
                final float float10 = (integer12 >> 8 & 0xFF) / 255.0f;
                final float float11 = (integer12 & 0xFF) / 255.0f;
                GlStateManager.color4f(float9, float10, float11, 1.0f);
                this.model.render(asd, float2, float3, float5, float6, float7, float8);
                return;
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.model.render(asd, float2, float3, float5, float6, float7, float8);
        }
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
