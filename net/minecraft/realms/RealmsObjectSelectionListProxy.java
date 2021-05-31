package net.minecraft.realms;

import net.minecraft.client.gui.components.AbstractSelectionList;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class RealmsObjectSelectionListProxy<E extends Entry<E>> extends ObjectSelectionList<E> {
    private final RealmsObjectSelectionList realmsObjectSelectionList;
    
    public RealmsObjectSelectionListProxy(final RealmsObjectSelectionList realmsObjectSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(Minecraft.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsObjectSelectionList = realmsObjectSelectionList;
    }
    
    public int getItemCount() {
        return super.getItemCount();
    }
    
    public void clear() {
        super.clearEntries();
    }
    
    public boolean isFocused() {
        return this.realmsObjectSelectionList.isFocused();
    }
    
    protected void setSelectedItem(final int integer) {
        if (integer == -1) {
            super.setSelected(null);
        }
        else if (super.getItemCount() != 0) {
            final E a3 = super.getEntry(integer);
            super.setSelected(a3);
        }
    }
    
    @Override
    public void setSelected(@Nullable final E a) {
        super.setSelected(a);
        this.realmsObjectSelectionList.selectItem(super.children().indexOf(a));
    }
    
    public void renderBackground() {
        this.realmsObjectSelectionList.renderBackground();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsObjectSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsObjectSelectionList.getScrollbarPosition();
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return this.realmsObjectSelectionList.mouseScrolled(double1, double2, double3) || super.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public int getRowWidth() {
        return this.realmsObjectSelectionList.getRowWidth();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.realmsObjectSelectionList.mouseClicked(double1, double2, integer) || AbstractSelectionList.this.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return this.realmsObjectSelectionList.mouseReleased(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return this.realmsObjectSelectionList.mouseDragged(double1, double2, integer, double4, double5) || super.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    @Override
    protected final int addEntry(final E a) {
        return super.addEntry(a);
    }
    
    public E remove(final int integer) {
        return super.remove(integer);
    }
    
    public boolean removeEntry(final E a) {
        return super.removeEntry(a);
    }
    
    @Override
    public void setScrollAmount(final double double1) {
        super.setScrollAmount(double1);
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
    
    public int itemHeight() {
        return this.itemHeight;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return super.keyPressed(integer1, integer2, integer3) || this.realmsObjectSelectionList.keyPressed(integer1, integer2, integer3);
    }
    
    public void replaceEntries(final Collection<E> collection) {
        super.replaceEntries(collection);
    }
    
    public int getRowTop(final int integer) {
        return super.getRowTop(integer);
    }
    
    public int getRowLeft() {
        return super.getRowLeft();
    }
}
