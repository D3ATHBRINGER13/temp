package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Collections;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

public abstract class ScrolledSelectionList extends AbstractContainerEventHandler implements Widget {
    protected static final int NO_DRAG = -1;
    protected static final int DRAG_OUTSIDE = -2;
    protected final Minecraft minecraft;
    protected int width;
    protected int height;
    protected int y0;
    protected int y1;
    protected int x1;
    protected int x0;
    protected final int itemHeight;
    protected boolean centerListVertically;
    protected int yDrag;
    protected double yo;
    protected boolean visible;
    protected boolean renderSelection;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    
    public ScrolledSelectionList(final Minecraft cyc, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.centerListVertically = true;
        this.yDrag = -2;
        this.visible = true;
        this.renderSelection = true;
        this.minecraft = cyc;
        this.width = integer2;
        this.height = integer3;
        this.y0 = integer4;
        this.y1 = integer5;
        this.itemHeight = integer6;
        this.x0 = 0;
        this.x1 = integer2;
    }
    
    public void updateSize(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.width = integer1;
        this.height = integer2;
        this.y0 = integer3;
        this.y1 = integer4;
        this.x0 = 0;
        this.x1 = integer1;
    }
    
    public void setRenderSelection(final boolean boolean1) {
        this.renderSelection = boolean1;
    }
    
    protected void setRenderHeader(final boolean boolean1, final int integer) {
        this.renderHeader = boolean1;
        this.headerHeight = integer;
        if (!boolean1) {
            this.headerHeight = 0;
        }
    }
    
    public void setVisible(final boolean boolean1) {
        this.visible = boolean1;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    protected abstract int getItemCount();
    
    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
    
    protected boolean selectItem(final int integer1, final int integer2, final double double3, final double double4) {
        return true;
    }
    
    protected abstract boolean isSelectedItem(final int integer);
    
    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }
    
    protected abstract void renderBackground();
    
    protected void updateItemPosition(final int integer1, final int integer2, final int integer3, final float float4) {
    }
    
    protected abstract void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final float float7);
    
    protected void renderHeader(final int integer1, final int integer2, final Tesselator cuz) {
    }
    
    protected void clickedHeader(final int integer1, final int integer2) {
    }
    
    protected void renderDecorations(final int integer1, final int integer2) {
    }
    
    public int getItemAtPosition(final double double1, final double double2) {
        final int integer6 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
        final int integer7 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
        final int integer8 = Mth.floor(double2 - this.y0) - this.headerHeight + (int)this.yo - 4;
        final int integer9 = integer8 / this.itemHeight;
        if (double1 < this.getScrollbarPosition() && double1 >= integer6 && double1 <= integer7 && integer9 >= 0 && integer8 >= 0 && integer9 < this.getItemCount()) {
            return integer9;
        }
        return -1;
    }
    
    protected void capYPosition() {
        this.yo = Mth.clamp(this.yo, 0.0, this.getMaxScroll());
    }
    
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }
    
    public void centerScrollOn(final int integer) {
        this.yo = integer * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2;
        this.capYPosition();
    }
    
    public int getScroll() {
        return (int)this.yo;
    }
    
    public boolean isMouseInList(final double double1, final double double2) {
        return double2 >= this.y0 && double2 <= this.y1 && double1 >= this.x0 && double1 <= this.x1;
    }
    
    public int getScrollBottom() {
        return (int)this.yo - this.height - this.headerHeight;
    }
    
    public void scroll(final int integer) {
        this.yo += integer;
        this.capYPosition();
        this.yDrag = -2;
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
        this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float4 = 32.0f;
        cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw8.vertex(this.x0, this.y1, 0.0).uv(this.x0 / 32.0f, (this.y1 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuw8.vertex(this.x1, this.y1, 0.0).uv(this.x1 / 32.0f, (this.y1 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuw8.vertex(this.x1, this.y0, 0.0).uv(this.x1 / 32.0f, (this.y0 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuw8.vertex(this.x0, this.y0, 0.0).uv(this.x0 / 32.0f, (this.y0 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuz7.end();
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
        final int integer7 = 4;
        cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw8.vertex(this.x0, this.y0 + 4, 0.0).uv(0.0, 1.0).color(0, 0, 0, 0).endVertex();
        cuw8.vertex(this.x1, this.y0 + 4, 0.0).uv(1.0, 1.0).color(0, 0, 0, 0).endVertex();
        cuw8.vertex(this.x1, this.y0, 0.0).uv(1.0, 0.0).color(0, 0, 0, 255).endVertex();
        cuw8.vertex(this.x0, this.y0, 0.0).uv(0.0, 0.0).color(0, 0, 0, 255).endVertex();
        cuz7.end();
        cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw8.vertex(this.x0, this.y1, 0.0).uv(0.0, 1.0).color(0, 0, 0, 255).endVertex();
        cuw8.vertex(this.x1, this.y1, 0.0).uv(1.0, 1.0).color(0, 0, 0, 255).endVertex();
        cuw8.vertex(this.x1, this.y1 - 4, 0.0).uv(1.0, 0.0).color(0, 0, 0, 0).endVertex();
        cuw8.vertex(this.x0, this.y1 - 4, 0.0).uv(0.0, 0.0).color(0, 0, 0, 0).endVertex();
        cuz7.end();
        final int integer8 = this.getMaxScroll();
        if (integer8 > 0) {
            int integer9 = (int)((this.y1 - this.y0) * (this.y1 - this.y0) / (float)this.getMaxPosition());
            integer9 = Mth.clamp(integer9, 32, this.y1 - this.y0 - 8);
            int integer10 = (int)this.yo * (this.y1 - this.y0 - integer9) / integer8 + this.y0;
            if (integer10 < this.y0) {
                integer10 = this.y0;
            }
            cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw8.vertex(integer3, this.y1, 0.0).uv(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            cuw8.vertex(integer4, this.y1, 0.0).uv(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            cuw8.vertex(integer4, this.y0, 0.0).uv(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuw8.vertex(integer3, this.y0, 0.0).uv(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            cuz7.end();
            cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw8.vertex(integer3, integer10 + integer9, 0.0).uv(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            cuw8.vertex(integer4, integer10 + integer9, 0.0).uv(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            cuw8.vertex(integer4, integer10, 0.0).uv(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            cuw8.vertex(integer3, integer10, 0.0).uv(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            cuz7.end();
            cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            cuw8.vertex(integer3, integer10 + integer9 - 1, 0.0).uv(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            cuw8.vertex(integer4 - 1, integer10 + integer9 - 1, 0.0).uv(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            cuw8.vertex(integer4 - 1, integer10, 0.0).uv(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            cuw8.vertex(integer3, integer10, 0.0).uv(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            cuz7.end();
        }
        this.renderDecorations(integer1, integer2);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }
    
    protected void updateScrollingState(final double double1, final double double2, final int integer) {
        this.scrolling = (integer == 0 && double1 >= this.getScrollbarPosition() && double1 < this.getScrollbarPosition() + 6);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        this.updateScrollingState(double1, double2, integer);
        if (!this.isVisible() || !this.isMouseInList(double1, double2)) {
            return false;
        }
        final int integer2 = this.getItemAtPosition(double1, double2);
        if (integer2 == -1 && integer == 0) {
            this.clickedHeader((int)(double1 - (this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(double2 - this.y0) + (int)this.yo - 4);
            return true;
        }
        if (integer2 != -1 && this.selectItem(integer2, integer, double1, double2)) {
            if (this.children().size() > integer2) {
                this.setFocused((GuiEventListener)this.children().get(integer2));
            }
            this.setDragging(true);
            return true;
        }
        return this.scrolling;
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(double1, double2, integer);
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        if (super.mouseDragged(double1, double2, integer, double4, double5)) {
            return true;
        }
        if (!this.isVisible() || integer != 0 || !this.scrolling) {
            return false;
        }
        if (double2 < this.y0) {
            this.yo = 0.0;
        }
        else if (double2 > this.y1) {
            this.yo = this.getMaxScroll();
        }
        else {
            double double6 = this.getMaxScroll();
            if (double6 < 1.0) {
                double6 = 1.0;
            }
            int integer2 = (int)((this.y1 - this.y0) * (this.y1 - this.y0) / (float)this.getMaxPosition());
            integer2 = Mth.clamp(integer2, 32, this.y1 - this.y0 - 8);
            double double7 = double6 / (this.y1 - this.y0 - integer2);
            if (double7 < 1.0) {
                double7 = 1.0;
            }
            this.yo += double5 * double7;
            this.capYPosition();
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        if (!this.isVisible()) {
            return false;
        }
        this.yo -= double3 * this.itemHeight / 2.0;
        return true;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (!this.isVisible()) {
            return false;
        }
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 264) {
            this.moveSelection(1);
            return true;
        }
        if (integer1 == 265) {
            this.moveSelection(-1);
            return true;
        }
        return false;
    }
    
    protected void moveSelection(final int integer) {
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        return this.isVisible() && super.charTyped(character, integer);
    }
    
    public boolean isMouseOver(final double double1, final double double2) {
        return this.isMouseInList(double1, double2);
    }
    
    public int getRowWidth() {
        return 220;
    }
    
    protected void renderList(final int integer1, final int integer2, final int integer3, final int integer4, final float float5) {
        final int integer5 = this.getItemCount();
        final Tesselator cuz8 = Tesselator.getInstance();
        final BufferBuilder cuw9 = cuz8.getBuilder();
        for (int integer6 = 0; integer6 < integer5; ++integer6) {
            final int integer7 = integer2 + integer6 * this.itemHeight + this.headerHeight;
            final int integer8 = this.itemHeight - 4;
            if (integer7 > this.y1 || integer7 + integer8 < this.y0) {
                this.updateItemPosition(integer6, integer1, integer7, float5);
            }
            if (this.renderSelection && this.isSelectedItem(integer6)) {
                final int integer9 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
                final int integer10 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
                GlStateManager.disableTexture();
                final float float6 = this.isFocused() ? 1.0f : 0.5f;
                GlStateManager.color4f(float6, float6, float6, 1.0f);
                cuw9.begin(7, DefaultVertexFormat.POSITION);
                cuw9.vertex(integer9, integer7 + integer8 + 2, 0.0).endVertex();
                cuw9.vertex(integer10, integer7 + integer8 + 2, 0.0).endVertex();
                cuw9.vertex(integer10, integer7 - 2, 0.0).endVertex();
                cuw9.vertex(integer9, integer7 - 2, 0.0).endVertex();
                cuz8.end();
                GlStateManager.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                cuw9.begin(7, DefaultVertexFormat.POSITION);
                cuw9.vertex(integer9 + 1, integer7 + integer8 + 1, 0.0).endVertex();
                cuw9.vertex(integer10 - 1, integer7 + integer8 + 1, 0.0).endVertex();
                cuw9.vertex(integer10 - 1, integer7 - 1, 0.0).endVertex();
                cuw9.vertex(integer9 + 1, integer7 - 1, 0.0).endVertex();
                cuz8.end();
                GlStateManager.enableTexture();
            }
            this.renderItem(integer6, integer1, integer7, integer8, integer3, integer4, float5);
        }
    }
    
    protected boolean isFocused() {
        return false;
    }
    
    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }
    
    protected void renderHoleBackground(final int integer1, final int integer2, final int integer3, final int integer4) {
        final Tesselator cuz6 = Tesselator.getInstance();
        final BufferBuilder cuw7 = cuz6.getBuilder();
        this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float8 = 32.0f;
        cuw7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw7.vertex(this.x0, integer2, 0.0).uv(0.0, integer2 / 32.0f).color(64, 64, 64, integer4).endVertex();
        cuw7.vertex(this.x0 + this.width, integer2, 0.0).uv(this.width / 32.0f, integer2 / 32.0f).color(64, 64, 64, integer4).endVertex();
        cuw7.vertex(this.x0 + this.width, integer1, 0.0).uv(this.width / 32.0f, integer1 / 32.0f).color(64, 64, 64, integer3).endVertex();
        cuw7.vertex(this.x0, integer1, 0.0).uv(0.0, integer1 / 32.0f).color(64, 64, 64, integer3).endVertex();
        cuz6.end();
    }
    
    public void setLeftPos(final int integer) {
        this.x0 = integer;
        this.x1 = integer + this.width;
    }
    
    public int getItemHeight() {
        return this.itemHeight;
    }
}
