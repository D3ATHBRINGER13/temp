package net.minecraft.realms;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrolledSelectionList;

public class RealmsSimpleScrolledSelectionListProxy extends ScrolledSelectionList {
    private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;
    
    public RealmsSimpleScrolledSelectionListProxy(final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(Minecraft.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsSimpleScrolledSelectionList = realmsSimpleScrolledSelectionList;
    }
    
    public int getItemCount() {
        return this.realmsSimpleScrolledSelectionList.getItemCount();
    }
    
    public boolean selectItem(final int integer1, final int integer2, final double double3, final double double4) {
        return this.realmsSimpleScrolledSelectionList.selectItem(integer1, integer2, double3, double4);
    }
    
    public boolean isSelectedItem(final int integer) {
        return this.realmsSimpleScrolledSelectionList.isSelectedItem(integer);
    }
    
    public void renderBackground() {
        this.realmsSimpleScrolledSelectionList.renderBackground();
    }
    
    public void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.realmsSimpleScrolledSelectionList.renderItem(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsSimpleScrolledSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsSimpleScrolledSelectionList.getScrollbarPosition();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (!this.visible) {
            return;
        }
        this.renderBackground();
        final int integer3 = this.getScrollbarPosition();
        final int integer4 = integer3 + 6;
        this.capYPosition();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tesselator cuz7 = Tesselator.getInstance();
        final BufferBuilder cuw8 = cuz7.getBuilder();
        final int integer5 = this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
        final int integer6 = this.y0 + 4 - (int)this.yo;
        if (this.renderHeader) {
            this.renderHeader(integer5, integer6, cuz7);
        }
        this.renderList(integer5, integer6, integer1, integer2, float3);
        GlStateManager.disableDepthTest();
        this.renderHoleBackground(0, this.y0, 255, 255);
        this.renderHoleBackground(this.y1, this.height, 255, 255);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
        final int integer7 = this.getMaxScroll();
        if (integer7 > 0) {
            int integer8 = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
            integer8 = Mth.clamp(integer8, 32, this.y1 - this.y0 - 8);
            int integer9 = (int)this.yo * (this.y1 - this.y0 - integer8) / integer7 + this.y0;
            if (integer9 < this.y0) {
                integer9 = this.y0;
            }
            cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw8.vertex(integer3, this.y1, 0.0).uv(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            cuw8.vertex(integer4, this.y1, 0.0).uv(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            cuw8.vertex(integer4, this.y0, 0.0).uv(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuw8.vertex(integer3, this.y0, 0.0).uv(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuz7.end();
            cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw8.vertex(integer3, integer9 + integer8, 0.0).uv(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            cuw8.vertex(integer4, integer9 + integer8, 0.0).uv(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            cuw8.vertex(integer4, integer9, 0.0).uv(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            cuw8.vertex(integer3, integer9, 0.0).uv(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            cuz7.end();
            cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw8.vertex(integer3, integer9 + integer8 - 1, 0.0).uv(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            cuw8.vertex(integer4 - 1, integer9 + integer8 - 1, 0.0).uv(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            cuw8.vertex(integer4 - 1, integer9, 0.0).uv(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            cuw8.vertex(integer3, integer9, 0.0).uv(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            cuz7.end();
        }
        this.renderDecorations(integer1, integer2);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return this.realmsSimpleScrolledSelectionList.mouseScrolled(double1, double2, double3) || super.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.realmsSimpleScrolledSelectionList.mouseClicked(double1, double2, integer) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return this.realmsSimpleScrolledSelectionList.mouseReleased(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return this.realmsSimpleScrolledSelectionList.mouseDragged(double1, double2, integer, double4, double5);
    }
}
