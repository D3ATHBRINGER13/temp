package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;

public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    
    public SolidFaceRenderer(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final double double5 = cxq4.getPosition().x;
        final double double6 = cxq4.getPosition().y;
        final double double7 = cxq4.getPosition().z;
        final BlockGetter bhb11 = this.minecraft.player.level;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        final BlockPos ew12 = new BlockPos(cxq4.getPosition());
        for (final BlockPos ew13 : BlockPos.betweenClosed(ew12.offset(-6, -6, -6), ew12.offset(6, 6, 6))) {
            final BlockState bvt15 = bhb11.getBlockState(ew13);
            if (bvt15.getBlock() == Blocks.AIR) {
                continue;
            }
            final VoxelShape ctc16 = bvt15.getShape(bhb11, ew13);
            for (final AABB csc18 : ctc16.toAabbs()) {
                final AABB csc19 = csc18.move(ew13).inflate(0.002).move(-double5, -double6, -double7);
                final double double8 = csc19.minX;
                final double double9 = csc19.minY;
                final double double10 = csc19.minZ;
                final double double11 = csc19.maxX;
                final double double12 = csc19.maxY;
                final double double13 = csc19.maxZ;
                final float float32 = 1.0f;
                final float float33 = 0.0f;
                final float float34 = 0.0f;
                final float float35 = 0.5f;
                if (bvt15.isFaceSturdy(bhb11, ew13, Direction.WEST)) {
                    final Tesselator cuz36 = Tesselator.getInstance();
                    final BufferBuilder cuw37 = cuz36.getBuilder();
                    cuw37.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    cuw37.vertex(double8, double9, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double9, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double12, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double12, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuz36.end();
                }
                if (bvt15.isFaceSturdy(bhb11, ew13, Direction.SOUTH)) {
                    final Tesselator cuz36 = Tesselator.getInstance();
                    final BufferBuilder cuw37 = cuz36.getBuilder();
                    cuw37.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    cuw37.vertex(double8, double12, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double9, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double12, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double9, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuz36.end();
                }
                if (bvt15.isFaceSturdy(bhb11, ew13, Direction.EAST)) {
                    final Tesselator cuz36 = Tesselator.getInstance();
                    final BufferBuilder cuw37 = cuz36.getBuilder();
                    cuw37.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    cuw37.vertex(double11, double9, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double9, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double12, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double12, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuz36.end();
                }
                if (bvt15.isFaceSturdy(bhb11, ew13, Direction.NORTH)) {
                    final Tesselator cuz36 = Tesselator.getInstance();
                    final BufferBuilder cuw37 = cuz36.getBuilder();
                    cuw37.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    cuw37.vertex(double11, double12, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double9, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double12, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double9, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuz36.end();
                }
                if (bvt15.isFaceSturdy(bhb11, ew13, Direction.DOWN)) {
                    final Tesselator cuz36 = Tesselator.getInstance();
                    final BufferBuilder cuw37 = cuz36.getBuilder();
                    cuw37.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    cuw37.vertex(double8, double9, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double9, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double9, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double9, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuz36.end();
                }
                if (bvt15.isFaceSturdy(bhb11, ew13, Direction.UP)) {
                    final Tesselator cuz36 = Tesselator.getInstance();
                    final BufferBuilder cuw37 = cuz36.getBuilder();
                    cuw37.begin(5, DefaultVertexFormat.POSITION_COLOR);
                    cuw37.vertex(double8, double12, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double8, double12, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double12, double10).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuw37.vertex(double11, double12, double13).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                    cuz36.end();
                }
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
