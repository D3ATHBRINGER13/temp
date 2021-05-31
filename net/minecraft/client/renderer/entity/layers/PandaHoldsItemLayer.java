package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.PandaModel;
import net.minecraft.world.entity.animal.Panda;

public class PandaHoldsItemLayer extends RenderLayer<Panda, PandaModel<Panda>> {
    public PandaHoldsItemLayer(final RenderLayerParent<Panda, PandaModel<Panda>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final Panda arl, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = arl.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!arl.isSitting() || bcj10.isEmpty() || arl.isScared()) {
            return;
        }
        float float9 = -0.6f;
        float float10 = 1.4f;
        if (arl.isEating()) {
            float9 -= 0.2f * Mth.sin(float5 * 0.6f) + 0.2f;
            float10 -= 0.09f * Mth.sin(float5 * 0.6f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.1f, float10, float9);
        Minecraft.getInstance().getItemRenderer().renderWithMobState(bcj10, arl, ItemTransforms.TransformType.GROUND, false);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
