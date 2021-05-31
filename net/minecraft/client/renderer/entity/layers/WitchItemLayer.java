package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.WitchModel;
import net.minecraft.world.entity.LivingEntity;

public class WitchItemLayer<T extends LivingEntity> extends RenderLayer<T, WitchModel<T>> {
    public WitchItemLayer(final RenderLayerParent<T, WitchModel<T>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = aix.getMainHandItem();
        if (bcj10.isEmpty()) {
            return;
        }
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        if (this.getParentModel().young) {
            GlStateManager.translatef(0.0f, 0.625f, 0.0f);
            GlStateManager.rotatef(-20.0f, -1.0f, 0.0f, 0.0f);
            final float float9 = 0.5f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        }
        this.getParentModel().getNose().translateTo(0.0625f);
        GlStateManager.translatef(-0.0625f, 0.53125f, 0.21875f);
        final Item bce11 = bcj10.getItem();
        if (Block.byItem(bce11).defaultBlockState().getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED) {
            GlStateManager.translatef(0.0f, 0.0625f, -0.25f);
            GlStateManager.rotatef(30.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-5.0f, 0.0f, 1.0f, 0.0f);
            final float float10 = 0.375f;
            GlStateManager.scalef(0.375f, -0.375f, 0.375f);
        }
        else if (bce11 == Items.BOW) {
            GlStateManager.translatef(0.0f, 0.125f, -0.125f);
            GlStateManager.rotatef(-45.0f, 0.0f, 1.0f, 0.0f);
            final float float10 = 0.625f;
            GlStateManager.scalef(0.625f, -0.625f, 0.625f);
            GlStateManager.rotatef(-100.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-20.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            GlStateManager.translatef(0.1875f, 0.1875f, 0.0f);
            final float float10 = 0.875f;
            GlStateManager.scalef(0.875f, 0.875f, 0.875f);
            GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(-60.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-30.0f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.rotatef(-15.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(40.0f, 0.0f, 0.0f, 1.0f);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(aix, bcj10, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
}
