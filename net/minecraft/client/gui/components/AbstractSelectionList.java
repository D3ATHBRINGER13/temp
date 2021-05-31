package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.Mth;
import java.util.Objects;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

public abstract class AbstractSelectionList<E extends Entry<E>> extends AbstractContainerEventHandler implements Widget {
    protected static final int DRAG_OUTSIDE = -2;
    protected final Minecraft minecraft;
    protected final int itemHeight;
    private final List<E> children;
    protected int width;
    protected int height;
    protected int y0;
    protected int y1;
    protected int x1;
    protected int x0;
    protected boolean centerListVertically;
    protected int yDrag;
    private double scrollAmount;
    protected boolean renderSelection;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    private E selected;
    
    public AbstractSelectionList(final Minecraft cyc, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.children = (List<E>)new TrackedList();
        this.centerListVertically = true;
        this.yDrag = -2;
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
    
    public int getRowWidth() {
        return 220;
    }
    
    @Nullable
    public E getSelected() {
        return this.selected;
    }
    
    public void setSelected(@Nullable final E a) {
        this.selected = a;
    }
    
    @Nullable
    @Override
    public E getFocused() {
        return (E)super.getFocused();
    }
    
    @Override
    public final List<E> children() {
        return this.children;
    }
    
    protected final void clearEntries() {
        this.children.clear();
    }
    
    protected void replaceEntries(final Collection<E> collection) {
        this.children.clear();
        this.children.addAll((Collection)collection);
    }
    
    protected E getEntry(final int integer) {
        return (E)this.children().get(integer);
    }
    
    protected int addEntry(final E a) {
        this.children.add(a);
        return this.children.size() - 1;
    }
    
    protected int getItemCount() {
        return this.children().size();
    }
    
    protected boolean isSelectedItem(final int integer) {
        return Objects.equals(this.getSelected(), this.children().get(integer));
    }
    
    @Nullable
    protected final E getEntryAtPosition(final double double1, final double double2) {
        final int integer6 = this.getRowWidth() / 2;
        final int integer7 = this.x0 + this.width / 2;
        final int integer8 = integer7 - integer6;
        final int integer9 = integer7 + integer6;
        final int integer10 = Mth.floor(double2 - this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
        final int integer11 = integer10 / this.itemHeight;
        if (double1 < this.getScrollbarPosition() && double1 >= integer8 && double1 <= integer9 && integer11 >= 0 && integer10 >= 0 && integer11 < this.getItemCount()) {
            return (E)this.children().get(integer11);
        }
        return null;
    }
    
    public void updateSize(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.width = integer1;
        this.height = integer2;
        this.y0 = integer3;
        this.y1 = integer4;
        this.x0 = 0;
        this.x1 = integer1;
    }
    
    public void setLeftPos(final int integer) {
        this.x0 = integer;
        this.x1 = integer + this.width;
    }
    
    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }
    
    protected void clickedHeader(final int integer1, final int integer2) {
    }
    
    protected void renderHeader(final int integer1, final int integer2, final Tesselator cuz) {
    }
    
    protected void renderBackground() {
    }
    
    protected void renderDecorations(final int integer1, final int integer2) {
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final int integer3 = this.getScrollbarPosition();
        final int integer4 = integer3 + 6;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tesselator cuz7 = Tesselator.getInstance();
        final BufferBuilder cuw8 = cuz7.getBuilder();
        this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float4 = 32.0f;
        cuw8.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        cuw8.vertex(this.x0, this.y1, 0.0).uv(this.x0 / 32.0f, (this.y1 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuw8.vertex(this.x1, this.y1, 0.0).uv(this.x1 / 32.0f, (this.y1 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuw8.vertex(this.x1, this.y0, 0.0).uv(this.x1 / 32.0f, (this.y0 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuw8.vertex(this.x0, this.y0, 0.0).uv(this.x0 / 32.0f, (this.y0 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
        cuz7.end();
        final int integer5 = this.getRowLeft();
        final int integer6 = this.y0 + 4 - (int)this.getScrollAmount();
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
            int integer10 = (int)this.getScrollAmount() * (this.y1 - this.y0 - integer9) / integer8 + this.y0;
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
    
    protected void centerScrollOn(final E a) {
        this.setScrollAmount(this.children().indexOf(a) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
    }
    
    protected void ensureVisible(final E a) {
        final int integer3 = this.getRowTop(this.children().indexOf(a));
        final int integer4 = integer3 - this.y0 - 4 - this.itemHeight;
        if (integer4 < 0) {
            this.scroll(integer4);
        }
        final int integer5 = this.y1 - integer3 - this.itemHeight - this.itemHeight;
        if (integer5 < 0) {
            this.scroll(-integer5);
        }
    }
    
    private void scroll(final int integer) {
        this.setScrollAmount(this.getScrollAmount() + integer);
        this.yDrag = -2;
    }
    
    public double getScrollAmount() {
        return this.scrollAmount;
    }
    
    public void setScrollAmount(final double double1) {
        this.scrollAmount = Mth.clamp(double1, 0.0, this.getMaxScroll());
    }
    
    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }
    
    public int getScrollBottom() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }
    
    protected void updateScrollingState(final double double1, final double double2, final int integer) {
        this.scrolling = (integer == 0 && double1 >= this.getScrollbarPosition() && double1 < this.getScrollbarPosition() + 6);
    }
    
    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        this.updateScrollingState(double1, double2, integer);
        if (!this.isMouseOver(double1, double2)) {
            return false;
        }
        final E a7 = this.getEntryAtPosition(double1, double2);
        if (a7 != null) {
            if (a7.mouseClicked(double1, double2, integer)) {
                this.setFocused(a7);
                this.setDragging(true);
                return true;
            }
        }
        else if (integer == 0) {
            this.clickedHeader((int)(double1 - (this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(double2 - this.y0) + (int)this.getScrollAmount() - 4);
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
        if (integer != 0 || !this.scrolling) {
            return false;
        }
        if (double2 < this.y0) {
            this.setScrollAmount(0.0);
        }
        else if (double2 > this.y1) {
            this.setScrollAmount(this.getMaxScroll());
        }
        else {
            final double double6 = Math.max(1, this.getMaxScroll());
            final int integer2 = this.y1 - this.y0;
            final int integer3 = Mth.clamp((int)(integer2 * integer2 / (float)this.getMaxPosition()), 32, integer2 - 8);
            final double double7 = Math.max(1.0, double6 / (integer2 - integer3));
            this.setScrollAmount(this.getScrollAmount() + double5 * double7);
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        this.setScrollAmount(this.getScrollAmount() - double3 * this.itemHeight / 2.0);
        return true;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
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
        if (!this.children().isEmpty()) {
            final int integer2 = this.children().indexOf(this.getSelected());
            final int integer3 = Mth.clamp(integer2 + integer, 0, this.getItemCount() - 1);
            final E a5 = (E)this.children().get(integer3);
            this.setSelected(a5);
            this.ensureVisible(a5);
        }
    }
    
    public boolean isMouseOver(final double double1, final double double2) {
        return double2 >= this.y0 && double2 <= this.y1 && double1 >= this.x0 && double1 <= this.x1;
    }
    
    protected void renderList(final int integer1, final int integer2, final int integer3, final int integer4, final float float5) {
        final int integer5 = this.getItemCount();
        final Tesselator cuz8 = Tesselator.getInstance();
        final BufferBuilder cuw9 = cuz8.getBuilder();
        for (int integer6 = 0; integer6 < integer5; ++integer6) {
            final int integer7 = this.getRowTop(integer6);
            final int integer8 = this.getRowBottom(integer6);
            if (integer8 >= this.y0) {
                if (integer7 <= this.y1) {
                    final int integer9 = integer2 + integer6 * this.itemHeight + this.headerHeight;
                    final int integer10 = this.itemHeight - 4;
                    final E a15 = this.getEntry(integer6);
                    final int integer11 = this.getRowWidth();
                    if (this.renderSelection && this.isSelectedItem(integer6)) {
                        final int integer12 = this.x0 + this.width / 2 - integer11 / 2;
                        final int integer13 = this.x0 + this.width / 2 + integer11 / 2;
                        GlStateManager.disableTexture();
                        final float float6 = this.isFocused() ? 1.0f : 0.5f;
                        GlStateManager.color4f(float6, float6, float6, 1.0f);
                        cuw9.begin(7, DefaultVertexFormat.POSITION);
                        cuw9.vertex(integer12, integer9 + integer10 + 2, 0.0).endVertex();
                        cuw9.vertex(integer13, integer9 + integer10 + 2, 0.0).endVertex();
                        cuw9.vertex(integer13, integer9 - 2, 0.0).endVertex();
                        cuw9.vertex(integer12, integer9 - 2, 0.0).endVertex();
                        cuz8.end();
                        GlStateManager.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                        cuw9.begin(7, DefaultVertexFormat.POSITION);
                        cuw9.vertex(integer12 + 1, integer9 + integer10 + 1, 0.0).endVertex();
                        cuw9.vertex(integer13 - 1, integer9 + integer10 + 1, 0.0).endVertex();
                        cuw9.vertex(integer13 - 1, integer9 - 1, 0.0).endVertex();
                        cuw9.vertex(integer12 + 1, integer9 - 1, 0.0).endVertex();
                        cuz8.end();
                        GlStateManager.enableTexture();
                    }
                    final int integer12 = this.getRowLeft();
                    a15.render(integer6, integer7, integer12, integer11, integer10, integer3, integer4, this.isMouseOver(integer3, integer4) && Objects.equals(this.getEntryAtPosition(integer3, integer4), a15), float5);
                }
            }
        }
    }
    
    protected int getRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
    }
    
    protected int getRowTop(final int integer) {
        return this.y0 + 4 - (int)this.getScrollAmount() + integer * this.itemHeight + this.headerHeight;
    }
    
    private int getRowBottom(final int integer) {
        return this.getRowTop(integer) + this.itemHeight;
    }
    
    protected boolean isFocused() {
        return false;
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
    
    protected E remove(final int integer) {
        final E a3 = (E)this.children.get(integer);
        if (this.removeEntry((E)this.children.get(integer))) {
            return a3;
        }
        return null;
    }
    
    protected boolean removeEntry(final E a) {
        final boolean boolean3 = this.children.remove(a);
        if (boolean3 && a == this.getSelected()) {
            this.setSelected(null);
        }
        return boolean3;
    }
    
    public abstract static class Entry<E extends Entry<E>> implements GuiEventListener {
        @Deprecated
        AbstractSelectionList<E> list;
        
        public abstract void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9);
        
        public boolean isMouseOver(final double double1, final double double2) {
            return Objects.equals(this.list.getEntryAtPosition(double1, double2), this);
        }
    }
    
    class TrackedList extends AbstractList<E> {
        private final List<E> delegate;
        
        private TrackedList() {
            this.delegate = (List<E>)Lists.newArrayList();
        }
        
        public E get(final int integer) {
            return (E)this.delegate.get(integer);
        }
        
        public int size() {
            return this.delegate.size();
        }
        
        public E set(final int integer, final E a) {
            final E a2 = (E)this.delegate.set(integer, a);
            a.list = (AbstractSelectionList<E>)AbstractSelectionList.this;
            return a2;
        }
        
        public void add(final int integer, final E a) {
            this.delegate.add(integer, a);
            a.list = (AbstractSelectionList<E>)AbstractSelectionList.this;
        }
        
        public E remove(final int integer) {
            return (E)this.delegate.remove(integer);
        }
    }
}
