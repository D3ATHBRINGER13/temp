package net.minecraft.client.renderer.blockentity;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.BedBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BedBlockEntity;

public class BedRenderer extends BlockEntityRenderer<BedBlockEntity> {
    private static final ResourceLocation[] TEXTURES;
    private final BedModel bedModel;
    
    public BedRenderer() {
        this.bedModel = new BedModel();
    }
    
    @Override
    public void render(final BedBlockEntity btt, final double double2, final double double3, final double double4, final float float5, final int integer) {
        if (integer >= 0) {
            this.bindTexture(BedRenderer.BREAKING_LOCATIONS[integer]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            final ResourceLocation qv11 = BedRenderer.TEXTURES[btt.getColor().getId()];
            if (qv11 != null) {
                this.bindTexture(qv11);
            }
        }
        if (btt.hasLevel()) {
            final BlockState bvt11 = btt.getBlockState();
            this.renderPiece(bvt11.<BedPart>getValue(BedBlock.PART) == BedPart.HEAD, double2, double3, double4, bvt11.<Direction>getValue((Property<Direction>)BedBlock.FACING));
        }
        else {
            this.renderPiece(true, double2, double3, double4, Direction.SOUTH);
            this.renderPiece(false, double2, double3, double4 - 1.0, Direction.SOUTH);
        }
        if (integer >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void renderPiece(final boolean boolean1, final double double2, final double double3, final double double4, final Direction fb) {
        this.bedModel.preparePiece(boolean1);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double3 + 0.5625f, (float)double4);
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.5f, 0.5f, 0.5f);
        GlStateManager.rotatef(180.0f + fb.toYRot(), 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        GlStateManager.enableRescaleNormal();
        this.bedModel.render();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    static {
        TEXTURES = (ResourceLocation[])Arrays.stream((Object[])DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(bbg -> new ResourceLocation("textures/entity/bed/" + bbg.getName() + ".png")).toArray(ResourceLocation[]::new);
    }
}
