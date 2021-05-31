package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Vec3i;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

public class StructureBlockRenderer extends BlockEntityRenderer<StructureBlockEntity> {
    @Override
    public void render(final StructureBlockEntity buw, final double double2, final double double3, final double double4, final float float5, final int integer) {
        if (!Minecraft.getInstance().player.canUseGameMasterBlocks() && !Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        super.render(buw, double2, double3, double4, float5, integer);
        final BlockPos ew11 = buw.getStructurePos();
        final BlockPos ew12 = buw.getStructureSize();
        if (ew12.getX() < 1 || ew12.getY() < 1 || ew12.getZ() < 1) {
            return;
        }
        if (buw.getMode() != StructureMode.SAVE && buw.getMode() != StructureMode.LOAD) {
            return;
        }
        final double double5 = 0.01;
        final double double6 = ew11.getX();
        final double double7 = ew11.getZ();
        final double double8 = double3 + ew11.getY() - 0.01;
        final double double9 = double8 + ew12.getY() + 0.02;
        double double10 = 0.0;
        double double11 = 0.0;
        switch (buw.getMirror()) {
            case LEFT_RIGHT: {
                double10 = ew12.getX() + 0.02;
                double11 = -(ew12.getZ() + 0.02);
                break;
            }
            case FRONT_BACK: {
                double10 = -(ew12.getX() + 0.02);
                double11 = ew12.getZ() + 0.02;
                break;
            }
            default: {
                double10 = ew12.getX() + 0.02;
                double11 = ew12.getZ() + 0.02;
                break;
            }
        }
        double double12 = 0.0;
        double double13 = 0.0;
        double double14 = 0.0;
        double double15 = 0.0;
        switch (buw.getRotation()) {
            case CLOCKWISE_90: {
                double12 = double2 + ((double11 < 0.0) ? (double6 - 0.01) : (double6 + 1.0 + 0.01));
                double13 = double4 + ((double10 < 0.0) ? (double7 + 1.0 + 0.01) : (double7 - 0.01));
                double14 = double12 - double11;
                double15 = double13 + double10;
                break;
            }
            case CLOCKWISE_180: {
                double12 = double2 + ((double10 < 0.0) ? (double6 - 0.01) : (double6 + 1.0 + 0.01));
                double13 = double4 + ((double11 < 0.0) ? (double7 - 0.01) : (double7 + 1.0 + 0.01));
                double14 = double12 - double10;
                double15 = double13 - double11;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                double12 = double2 + ((double11 < 0.0) ? (double6 + 1.0 + 0.01) : (double6 - 0.01));
                double13 = double4 + ((double10 < 0.0) ? (double7 - 0.01) : (double7 + 1.0 + 0.01));
                double14 = double12 + double11;
                double15 = double13 - double10;
                break;
            }
            default: {
                double12 = double2 + ((double10 < 0.0) ? (double6 + 1.0 + 0.01) : (double6 - 0.01));
                double13 = double4 + ((double11 < 0.0) ? (double7 + 1.0 + 0.01) : (double7 - 0.01));
                double14 = double12 + double10;
                double15 = double13 + double11;
                break;
            }
        }
        final int integer2 = 255;
        final int integer3 = 223;
        final int integer4 = 127;
        final Tesselator cuz38 = Tesselator.getInstance();
        final BufferBuilder cuw39 = cuz38.getBuilder();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.setOverlayRenderState(true);
        if (buw.getMode() == StructureMode.SAVE || buw.getShowBoundingBox()) {
            this.renderBox(cuz38, cuw39, double12, double8, double13, double14, double9, double15, 255, 223, 127);
        }
        if (buw.getMode() == StructureMode.SAVE && buw.getShowAir()) {
            this.renderInvisibleBlocks(buw, double2, double3, double4, ew11, cuz38, cuw39, true);
            this.renderInvisibleBlocks(buw, double2, double3, double4, ew11, cuz38, cuw39, false);
        }
        this.setOverlayRenderState(false);
        GlStateManager.lineWidth(1.0f);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableDepthTest();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();
    }
    
    private void renderInvisibleBlocks(final StructureBlockEntity buw, final double double2, final double double3, final double double4, final BlockPos ew, final Tesselator cuz, final BufferBuilder cuw, final boolean boolean8) {
        GlStateManager.lineWidth(boolean8 ? 3.0f : 1.0f);
        cuw.begin(3, DefaultVertexFormat.POSITION_COLOR);
        final BlockGetter bhb13 = buw.getLevel();
        final BlockPos ew2 = buw.getBlockPos();
        final BlockPos ew3 = ew2.offset(ew);
        for (final BlockPos ew4 : BlockPos.betweenClosed(ew3, ew3.offset(buw.getStructureSize()).offset(-1, -1, -1))) {
            final BlockState bvt18 = bhb13.getBlockState(ew4);
            final boolean boolean9 = bvt18.isAir();
            final boolean boolean10 = bvt18.getBlock() == Blocks.STRUCTURE_VOID;
            if (boolean9 || boolean10) {
                final float float21 = boolean9 ? 0.05f : 0.0f;
                final double double5 = ew4.getX() - ew2.getX() + 0.45f + double2 - float21;
                final double double6 = ew4.getY() - ew2.getY() + 0.45f + double3 - float21;
                final double double7 = ew4.getZ() - ew2.getZ() + 0.45f + double4 - float21;
                final double double8 = ew4.getX() - ew2.getX() + 0.55f + double2 + float21;
                final double double9 = ew4.getY() - ew2.getY() + 0.55f + double3 + float21;
                final double double10 = ew4.getZ() - ew2.getZ() + 0.55f + double4 + float21;
                if (boolean8) {
                    LevelRenderer.addChainedLineBoxVertices(cuw, double5, double6, double7, double8, double9, double10, 0.0f, 0.0f, 0.0f, 1.0f);
                }
                else if (boolean9) {
                    LevelRenderer.addChainedLineBoxVertices(cuw, double5, double6, double7, double8, double9, double10, 0.5f, 0.5f, 1.0f, 1.0f);
                }
                else {
                    LevelRenderer.addChainedLineBoxVertices(cuw, double5, double6, double7, double8, double9, double10, 1.0f, 0.25f, 0.25f, 1.0f);
                }
            }
        }
        cuz.end();
    }
    
    private void renderBox(final Tesselator cuz, final BufferBuilder cuw, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8, final int integer9, final int integer10, final int integer11) {
        GlStateManager.lineWidth(2.0f);
        cuw.begin(3, DefaultVertexFormat.POSITION_COLOR);
        cuw.vertex(double3, double4, double5).color((float)integer10, (float)integer10, (float)integer10, 0.0f).endVertex();
        cuw.vertex(double3, double4, double5).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double4, double5).color(integer10, integer11, integer11, integer9).endVertex();
        cuw.vertex(double6, double4, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double3, double4, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double3, double4, double5).color(integer11, integer11, integer10, integer9).endVertex();
        cuw.vertex(double3, double7, double5).color(integer11, integer10, integer11, integer9).endVertex();
        cuw.vertex(double6, double7, double5).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double7, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double3, double7, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double3, double7, double5).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double3, double7, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double3, double4, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double4, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double7, double8).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double7, double5).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double4, double5).color(integer10, integer10, integer10, integer9).endVertex();
        cuw.vertex(double6, double4, double5).color((float)integer10, (float)integer10, (float)integer10, 0.0f).endVertex();
        cuz.end();
        GlStateManager.lineWidth(1.0f);
    }
    
    @Override
    public boolean shouldRenderOffScreen(final StructureBlockEntity buw) {
        return true;
    }
}
