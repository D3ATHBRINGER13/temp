package net.minecraft.client.gui;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.resources.ResourceLocation;

public abstract class GuiComponent {
    public static final ResourceLocation BACKGROUND_LOCATION;
    public static final ResourceLocation STATS_ICON_LOCATION;
    public static final ResourceLocation GUI_ICONS_LOCATION;
    protected int blitOffset;
    
    protected void hLine(int integer1, int integer2, final int integer3, final int integer4) {
        if (integer2 < integer1) {
            final int integer5 = integer1;
            integer1 = integer2;
            integer2 = integer5;
        }
        fill(integer1, integer3, integer2 + 1, integer3 + 1, integer4);
    }
    
    protected void vLine(final int integer1, int integer2, int integer3, final int integer4) {
        if (integer3 < integer2) {
            final int integer5 = integer2;
            integer2 = integer3;
            integer3 = integer5;
        }
        fill(integer1, integer2 + 1, integer1 + 1, integer3, integer4);
    }
    
    public static void fill(int integer1, int integer2, int integer3, int integer4, final int integer5) {
        if (integer1 < integer3) {
            final int integer6 = integer1;
            integer1 = integer3;
            integer3 = integer6;
        }
        if (integer2 < integer4) {
            final int integer6 = integer2;
            integer2 = integer4;
            integer4 = integer6;
        }
        final float float6 = (integer5 >> 24 & 0xFF) / 255.0f;
        final float float7 = (integer5 >> 16 & 0xFF) / 255.0f;
        final float float8 = (integer5 >> 8 & 0xFF) / 255.0f;
        final float float9 = (integer5 & 0xFF) / 255.0f;
        final Tesselator cuz10 = Tesselator.getInstance();
        final BufferBuilder cuw11 = cuz10.getBuilder();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(float7, float8, float9, float6);
        cuw11.begin(7, DefaultVertexFormat.POSITION);
        cuw11.vertex(integer1, integer4, 0.0).endVertex();
        cuw11.vertex(integer3, integer4, 0.0).endVertex();
        cuw11.vertex(integer3, integer2, 0.0).endVertex();
        cuw11.vertex(integer1, integer2, 0.0).endVertex();
        cuz10.end();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
    
    protected void fillGradient(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        final float float8 = (integer5 >> 24 & 0xFF) / 255.0f;
        final float float9 = (integer5 >> 16 & 0xFF) / 255.0f;
        final float float10 = (integer5 >> 8 & 0xFF) / 255.0f;
        final float float11 = (integer5 & 0xFF) / 255.0f;
        final float float12 = (integer6 >> 24 & 0xFF) / 255.0f;
        final float float13 = (integer6 >> 16 & 0xFF) / 255.0f;
        final float float14 = (integer6 >> 8 & 0xFF) / 255.0f;
        final float float15 = (integer6 & 0xFF) / 255.0f;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tesselator cuz16 = Tesselator.getInstance();
        final BufferBuilder cuw17 = cuz16.getBuilder();
        cuw17.begin(7, DefaultVertexFormat.POSITION_COLOR);
        cuw17.vertex(integer3, integer2, this.blitOffset).color(float9, float10, float11, float8).endVertex();
        cuw17.vertex(integer1, integer2, this.blitOffset).color(float9, float10, float11, float8).endVertex();
        cuw17.vertex(integer1, integer4, this.blitOffset).color(float13, float14, float15, float12).endVertex();
        cuw17.vertex(integer3, integer4, this.blitOffset).color(float13, float14, float15, float12).endVertex();
        cuz16.end();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }
    
    public void drawCenteredString(final Font cyu, final String string, final int integer3, final int integer4, final int integer5) {
        cyu.drawShadow(string, (float)(integer3 - cyu.width(string) / 2), (float)integer4, integer5);
    }
    
    public void drawRightAlignedString(final Font cyu, final String string, final int integer3, final int integer4, final int integer5) {
        cyu.drawShadow(string, (float)(integer3 - cyu.width(string)), (float)integer4, integer5);
    }
    
    public void drawString(final Font cyu, final String string, final int integer3, final int integer4, final int integer5) {
        cyu.drawShadow(string, (float)integer3, (float)integer4, integer5);
    }
    
    public static void blit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final TextureAtlasSprite dxb) {
        innerBlit(integer1, integer1 + integer4, integer2, integer2 + integer5, integer3, dxb.getU0(), dxb.getU1(), dxb.getV0(), dxb.getV1());
    }
    
    public void blit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        blit(integer1, integer2, this.blitOffset, (float)integer3, (float)integer4, integer5, integer6, 256, 256);
    }
    
    public static void blit(final int integer1, final int integer2, final int integer3, final float float4, final float float5, final int integer6, final int integer7, final int integer8, final int integer9) {
        innerBlit(integer1, integer1 + integer6, integer2, integer2 + integer7, integer3, integer6, integer7, float4, float5, integer9, integer8);
    }
    
    public static void blit(final int integer1, final int integer2, final int integer3, final int integer4, final float float5, final float float6, final int integer7, final int integer8, final int integer9, final int integer10) {
        innerBlit(integer1, integer1 + integer3, integer2, integer2 + integer4, 0, integer7, integer8, float5, float6, integer9, integer10);
    }
    
    public static void blit(final int integer1, final int integer2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final int integer8) {
        blit(integer1, integer2, integer5, integer6, float3, float4, integer5, integer6, integer7, integer8);
    }
    
    private static void innerBlit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final float float8, final float float9, final int integer10, final int integer11) {
        innerBlit(integer1, integer2, integer3, integer4, integer5, (float8 + 0.0f) / integer10, (float8 + integer6) / integer10, (float9 + 0.0f) / integer11, (float9 + integer7) / integer11);
    }
    
    protected static void innerBlit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final float float6, final float float7, final float float8, final float float9) {
        final Tesselator cuz10 = Tesselator.getInstance();
        final BufferBuilder cuw11 = cuz10.getBuilder();
        cuw11.begin(7, DefaultVertexFormat.POSITION_TEX);
        cuw11.vertex(integer1, integer4, integer5).uv(float6, float9).endVertex();
        cuw11.vertex(integer2, integer4, integer5).uv(float7, float9).endVertex();
        cuw11.vertex(integer2, integer3, integer5).uv(float7, float8).endVertex();
        cuw11.vertex(integer1, integer3, integer5).uv(float6, float8).endVertex();
        cuz10.end();
    }
    
    static {
        BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
        STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
        GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    }
}
