package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockLayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.DolphinModel;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinCarryingItemLayer extends RenderLayer<Dolphin, DolphinModel<Dolphin>> {
    private final ItemRenderer itemRenderer;
    
    public DolphinCarryingItemLayer(final RenderLayerParent<Dolphin, DolphinModel<Dolphin>> dtr) {
        super(dtr);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }
    
    @Override
    public void render(final Dolphin arf, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final boolean boolean10 = arf.getMainArm() == HumanoidArm.RIGHT;
        final ItemStack bcj11 = boolean10 ? arf.getOffhandItem() : arf.getMainHandItem();
        final ItemStack bcj12 = boolean10 ? arf.getMainHandItem() : arf.getOffhandItem();
        if (bcj11.isEmpty() && bcj12.isEmpty()) {
            return;
        }
        this.renderItemOnNose(arf, bcj12);
    }
    
    private void renderItemOnNose(final LivingEntity aix, final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return;
        }
        final Item bce4 = bcj.getItem();
        final Block bmv5 = Block.byItem(bce4);
        GlStateManager.pushMatrix();
        final boolean boolean6 = this.itemRenderer.isGui3d(bcj) && bmv5.getRenderLayer() == BlockLayer.TRANSLUCENT;
        if (boolean6) {
            GlStateManager.depthMask(false);
        }
        final float float7 = 1.0f;
        final float float8 = -1.0f;
        final float float9 = Mth.abs(aix.xRot) / 60.0f;
        if (aix.xRot < 0.0f) {
            GlStateManager.translatef(0.0f, 1.0f - float9 * 0.5f, -1.0f + float9 * 0.5f);
        }
        else {
            GlStateManager.translatef(0.0f, 1.0f + float9 * 0.8f, -1.0f + float9 * 0.2f);
        }
        this.itemRenderer.renderWithMobState(bcj, aix, ItemTransforms.TransformType.GROUND, false);
        if (boolean6) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
