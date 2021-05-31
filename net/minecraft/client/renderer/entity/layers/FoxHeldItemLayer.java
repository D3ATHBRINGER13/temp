package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.FoxModel;
import net.minecraft.world.entity.animal.Fox;

public class FoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
    public FoxHeldItemLayer(final RenderLayerParent<Fox, FoxModel<Fox>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final Fox arh, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = arh.getItemBySlot(EquipmentSlot.MAINHAND);
        if (bcj10.isEmpty()) {
            return;
        }
        final boolean boolean11 = arh.isSleeping();
        final boolean boolean12 = arh.isBaby();
        GlStateManager.pushMatrix();
        if (boolean12) {
            final float float9 = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 8.0f * float8, 3.35f * float8);
        }
        GlStateManager.translatef(((RenderLayer<T, FoxModel>)this).getParentModel().head.x / 16.0f, ((RenderLayer<T, FoxModel>)this).getParentModel().head.y / 16.0f, ((RenderLayer<T, FoxModel>)this).getParentModel().head.z / 16.0f);
        final float float9 = arh.getHeadRollAngle(float4) * 57.295776f;
        GlStateManager.rotatef(float9, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(float6, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float7, 1.0f, 0.0f, 0.0f);
        if (arh.isBaby()) {
            if (boolean11) {
                GlStateManager.translatef(0.4f, 0.26f, 0.15f);
            }
            else {
                GlStateManager.translatef(0.06f, 0.26f, -0.5f);
            }
        }
        else if (boolean11) {
            GlStateManager.translatef(0.46f, 0.26f, 0.22f);
        }
        else {
            GlStateManager.translatef(0.06f, 0.27f, -0.5f);
        }
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        if (boolean11) {
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
        Minecraft.getInstance().getItemRenderer().renderWithMobState(bcj10, arh, ItemTransforms.TransformType.GROUND, false);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
