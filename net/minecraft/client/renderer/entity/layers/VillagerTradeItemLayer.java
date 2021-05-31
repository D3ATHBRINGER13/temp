package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.level.BlockLayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.world.entity.LivingEntity;

public class VillagerTradeItemLayer<T extends LivingEntity> extends RenderLayer<T, VillagerModel<T>> {
    private final ItemRenderer itemRenderer;
    
    public VillagerTradeItemLayer(final RenderLayerParent<T, VillagerModel<T>> dtr) {
        super(dtr);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = aix.getItemBySlot(EquipmentSlot.MAINHAND);
        if (bcj10.isEmpty()) {
            return;
        }
        final Item bce11 = bcj10.getItem();
        final Block bmv12 = Block.byItem(bce11);
        GlStateManager.pushMatrix();
        final boolean boolean13 = this.itemRenderer.isGui3d(bcj10) && bmv12.getRenderLayer() == BlockLayer.TRANSLUCENT;
        if (boolean13) {
            GlStateManager.depthMask(false);
        }
        GlStateManager.translatef(0.0f, 0.4f, -0.4f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        this.itemRenderer.renderWithMobState(bcj10, aix, ItemTransforms.TransformType.GROUND, false);
        if (boolean13) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
