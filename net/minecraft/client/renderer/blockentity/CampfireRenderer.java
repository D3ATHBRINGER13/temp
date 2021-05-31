package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

public class CampfireRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
    @Override
    public void render(final CampfireBlockEntity btz, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final Direction fb11 = btz.getBlockState().<Direction>getValue((Property<Direction>)CampfireBlock.FACING);
        final NonNullList<ItemStack> fk12 = btz.getItems();
        for (int integer2 = 0; integer2 < fk12.size(); ++integer2) {
            final ItemStack bcj14 = fk12.get(integer2);
            if (bcj14 != ItemStack.EMPTY) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.44921875f, (float)double4 + 0.5f);
                final Direction fb12 = Direction.from2DDataValue((integer2 + fb11.get2DDataValue()) % 4);
                GlStateManager.rotatef(-fb12.toYRot(), 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(-0.3125f, -0.3125f, 0.0f);
                GlStateManager.scalef(0.375f, 0.375f, 0.375f);
                Minecraft.getInstance().getItemRenderer().renderStatic(bcj14, ItemTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
