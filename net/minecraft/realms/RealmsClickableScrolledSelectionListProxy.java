package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrolledSelectionList;

public class RealmsClickableScrolledSelectionListProxy extends ScrolledSelectionList {
    private final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;
    
    public RealmsClickableScrolledSelectionListProxy(final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(Minecraft.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsClickableScrolledSelectionList = realmsClickableScrolledSelectionList;
    }
    
    public int getItemCount() {
        return this.realmsClickableScrolledSelectionList.getItemCount();
    }
    
    public boolean selectItem(final int integer1, final int integer2, final double double3, final double double4) {
        return this.realmsClickableScrolledSelectionList.selectItem(integer1, integer2, double3, double4);
    }
    
    public boolean isSelectedItem(final int integer) {
        return this.realmsClickableScrolledSelectionList.isSelectedItem(integer);
    }
    
    public void renderBackground() {
        this.realmsClickableScrolledSelectionList.renderBackground();
    }
    
    public void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.realmsClickableScrolledSelectionList.renderItem(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsClickableScrolledSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsClickableScrolledSelectionList.getScrollbarPosition();
    }
    
    public void itemClicked(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        this.realmsClickableScrolledSelectionList.itemClicked(integer1, integer2, integer3, integer4, integer5);
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return this.realmsClickableScrolledSelectionList.mouseScrolled(double1, double2, double3) || super.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.realmsClickableScrolledSelectionList.mouseClicked(double1, double2, integer) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return this.realmsClickableScrolledSelectionList.mouseReleased(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return this.realmsClickableScrolledSelectionList.mouseDragged(double1, double2, integer, double4, double5) || super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    public void renderSelected(final int integer1, final int integer2, final int integer3, final Tezzelator tezzelator) {
        this.realmsClickableScrolledSelectionList.renderSelected(integer1, integer2, integer3, tezzelator);
    }
    
    public void renderList(final int integer1, final int integer2, final int integer3, final int integer4, final float float5) {
        for (int integer5 = this.getItemCount(), integer6 = 0; integer6 < integer5; ++integer6) {
            final int integer7 = integer2 + integer6 * this.itemHeight + this.headerHeight;
            final int integer8 = this.itemHeight - 4;
            if (integer7 > this.y1 || integer7 + integer8 < this.y0) {
                this.updateItemPosition(integer6, integer1, integer7, float5);
            }
            if (this.renderSelection && this.isSelectedItem(integer6)) {
                this.renderSelected(this.width, integer7, integer8, Tezzelator.instance);
            }
            this.renderItem(integer6, integer1, integer7, integer8, integer3, integer4, float5);
        }
    }
    
    public int y0() {
        return this.y0;
    }
    
    public int y1() {
        return this.y1;
    }
    
    public int headerHeight() {
        return this.headerHeight;
    }
    
    public double yo() {
        return this.yo;
    }
    
    public int itemHeight() {
        return this.itemHeight;
    }
}
