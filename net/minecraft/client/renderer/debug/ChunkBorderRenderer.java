package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    
    public ChunkBorderRenderer(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final Camera cxq4 = this.minecraft.gameRenderer.getMainCamera();
        final Tesselator cuz5 = Tesselator.getInstance();
        final BufferBuilder cuw6 = cuz5.getBuilder();
        final double double7 = cxq4.getPosition().x;
        final double double8 = cxq4.getPosition().y;
        final double double9 = cxq4.getPosition().z;
        final double double10 = 0.0 - double8;
        final double double11 = 256.0 - double8;
        GlStateManager.disableTexture();
        GlStateManager.disableBlend();
        final double double12 = (cxq4.getEntity().xChunk << 4) - double7;
        final double double13 = (cxq4.getEntity().zChunk << 4) - double9;
        GlStateManager.lineWidth(1.0f);
        cuw6.begin(3, DefaultVertexFormat.POSITION_COLOR);
        for (int integer21 = -16; integer21 <= 32; integer21 += 16) {
            for (int integer22 = -16; integer22 <= 32; integer22 += 16) {
                cuw6.vertex(double12 + integer21, double10, double13 + integer22).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
                cuw6.vertex(double12 + integer21, double10, double13 + integer22).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                cuw6.vertex(double12 + integer21, double11, double13 + integer22).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                cuw6.vertex(double12 + integer21, double11, double13 + integer22).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
            }
        }
        for (int integer21 = 2; integer21 < 16; integer21 += 2) {
            cuw6.vertex(double12 + integer21, double10, double13).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12 + integer21, double10, double13).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + integer21, double11, double13).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + integer21, double11, double13).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12 + integer21, double10, double13 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12 + integer21, double10, double13 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + integer21, double11, double13 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + integer21, double11, double13 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int integer21 = 2; integer21 < 16; integer21 += 2) {
            cuw6.vertex(double12, double10, double13 + integer21).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12, double10, double13 + integer21).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double11, double13 + integer21).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double11, double13 + integer21).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double10, double13 + integer21).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double10, double13 + integer21).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double11, double13 + integer21).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double11, double13 + integer21).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int integer21 = 0; integer21 <= 256; integer21 += 2) {
            final double double14 = integer21 - double8;
            cuw6.vertex(double12, double14, double13).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            cuw6.vertex(double12, double14, double13).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double14, double13 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double14, double13 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double14, double13).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double14, double13).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double14, double13).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        cuz5.end();
        GlStateManager.lineWidth(2.0f);
        cuw6.begin(3, DefaultVertexFormat.POSITION_COLOR);
        for (int integer21 = 0; integer21 <= 16; integer21 += 16) {
            for (int integer22 = 0; integer22 <= 16; integer22 += 16) {
                cuw6.vertex(double12 + integer21, double10, double13 + integer22).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
                cuw6.vertex(double12 + integer21, double10, double13 + integer22).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                cuw6.vertex(double12 + integer21, double11, double13 + integer22).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                cuw6.vertex(double12 + integer21, double11, double13 + integer22).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            }
        }
        for (int integer21 = 0; integer21 <= 256; integer21 += 16) {
            final double double14 = integer21 - double8;
            cuw6.vertex(double12, double14, double13).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            cuw6.vertex(double12, double14, double13).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double14, double13 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double14, double13 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            cuw6.vertex(double12 + 16.0, double14, double13).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double14, double13).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            cuw6.vertex(double12, double14, double13).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
        }
        cuz5.end();
        GlStateManager.lineWidth(1.0f);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
    }
}
