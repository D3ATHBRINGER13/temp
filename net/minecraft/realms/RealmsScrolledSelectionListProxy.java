package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrolledSelectionList;

public class RealmsScrolledSelectionListProxy extends ScrolledSelectionList {
    private final RealmsScrolledSelectionList realmsScrolledSelectionList;
    
    public RealmsScrolledSelectionListProxy(final RealmsScrolledSelectionList realmsScrolledSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(Minecraft.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsScrolledSelectionList = realmsScrolledSelectionList;
    }
    
    public int getItemCount() {
        return this.realmsScrolledSelectionList.getItemCount();
    }
    
    public boolean selectItem(final int integer1, final int integer2, final double double3, final double double4) {
        return this.realmsScrolledSelectionList.selectItem(integer1, integer2, double3, double4);
    }
    
    public boolean isSelectedItem(final int integer) {
        return this.realmsScrolledSelectionList.isSelectedItem(integer);
    }
    
    public void renderBackground() {
        this.realmsScrolledSelectionList.renderBackground();
    }
    
    public void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.realmsScrolledSelectionList.renderItem(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsScrolledSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsScrolledSelectionList.getScrollbarPosition();
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return this.realmsScrolledSelectionList.mouseScrolled(double1, double2, double3) || super.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.realmsScrolledSelectionList.mouseClicked(double1, double2, integer) || super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return this.realmsScrolledSelectionList.mouseReleased(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return this.realmsScrolledSelectionList.mouseDragged(double1, double2, integer, double4, double5);
    }
}
