package net.minecraft.client.renderer.block;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.EntityBlockRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.Block;

public class AnimatedEntityBlockRenderer {
    public void renderSingleBlock(final Block bmv, final float float2) {
        GlStateManager.color4f(float2, float2, float2, 1.0f);
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        EntityBlockRenderer.instance.renderByItem(new ItemStack(bmv));
    }
}
