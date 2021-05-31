package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.core.Direction;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public class ShulkerBoxRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerModel<?> model;
    
    public ShulkerBoxRenderer(final ShulkerModel<?> dit) {
        this.model = dit;
    }
    
    @Override
    public void render(final ShulkerBoxBlockEntity bur, final double double2, final double double3, final double double4, final float float5, final int integer) {
        Direction fb11 = Direction.UP;
        if (bur.hasLevel()) {
            final BlockState bvt12 = this.getLevel().getBlockState(bur.getBlockPos());
            if (bvt12.getBlock() instanceof ShulkerBoxBlock) {
                fb11 = bvt12.<Direction>getValue(ShulkerBoxBlock.FACING);
            }
        }
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        if (integer >= 0) {
            this.bindTexture(ShulkerBoxRenderer.BREAKING_LOCATIONS[integer]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            final DyeColor bbg12 = bur.getColor();
            if (bbg12 == null) {
                this.bindTexture(ShulkerRenderer.DEFAULT_TEXTURE_LOCATION);
            }
            else {
                this.bindTexture(ShulkerRenderer.TEXTURE_LOCATION[bbg12.getId()]);
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        if (integer < 0) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 1.5f, (float)double4 + 0.5f);
        GlStateManager.scalef(1.0f, -1.0f, -1.0f);
        GlStateManager.translatef(0.0f, 1.0f, 0.0f);
        final float float6 = 0.9995f;
        GlStateManager.scalef(0.9995f, 0.9995f, 0.9995f);
        GlStateManager.translatef(0.0f, -1.0f, 0.0f);
        switch (fb11) {
            case DOWN: {
                GlStateManager.translatef(0.0f, 2.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
            case NORTH: {
                GlStateManager.translatef(0.0f, 1.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.translatef(0.0f, 1.0f, -1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case WEST: {
                GlStateManager.translatef(-1.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case EAST: {
                GlStateManager.translatef(1.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
        }
        this.model.getBase().render(0.0625f);
        GlStateManager.translatef(0.0f, -bur.getProgress(float5) * 0.5f, 0.0f);
        GlStateManager.rotatef(270.0f * bur.getProgress(float5), 0.0f, 1.0f, 0.0f);
        this.model.getLid().render(0.0625f);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
