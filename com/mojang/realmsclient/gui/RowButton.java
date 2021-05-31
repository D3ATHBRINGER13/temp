package com.mojang.realmsclient.gui;

import net.minecraft.realms.RealmListEntry;
import java.util.Iterator;
import net.minecraft.realms.RealmsObjectSelectionList;
import java.util.List;

public abstract class RowButton {
    public final int width;
    public final int height;
    public final int xOffset;
    public final int yOffset;
    
    public RowButton(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.width = integer1;
        this.height = integer2;
        this.xOffset = integer3;
        this.yOffset = integer4;
    }
    
    public void drawForRowAt(final int integer1, final int integer2, final int integer3, final int integer4) {
        final int integer5 = integer1 + this.xOffset;
        final int integer6 = integer2 + this.yOffset;
        boolean boolean8 = false;
        if (integer3 >= integer5 && integer3 <= integer5 + this.width && integer4 >= integer6 && integer4 <= integer6 + this.height) {
            boolean8 = true;
        }
        this.draw(integer5, integer6, boolean8);
    }
    
    protected abstract void draw(final int integer1, final int integer2, final boolean boolean3);
    
    public int getRight() {
        return this.xOffset + this.width;
    }
    
    public int getBottom() {
        return this.yOffset + this.height;
    }
    
    public abstract void onClick(final int integer);
    
    public static void drawButtonsInRow(final List<RowButton> list, final RealmsObjectSelectionList realmsObjectSelectionList, final int integer3, final int integer4, final int integer5, final int integer6) {
        for (final RowButton cwb8 : list) {
            if (realmsObjectSelectionList.getRowWidth() > cwb8.getRight()) {
                cwb8.drawForRowAt(integer3, integer4, integer5, integer6);
            }
        }
    }
    
    public static void rowButtonMouseClicked(final RealmsObjectSelectionList realmsObjectSelectionList, final RealmListEntry realmListEntry, final List<RowButton> list, final int integer, final double double5, final double double6) {
        if (integer == 0) {
            final int integer2 = realmsObjectSelectionList.children().indexOf(realmListEntry);
            if (integer2 > -1) {
                realmsObjectSelectionList.selectItem(integer2);
                final int integer3 = realmsObjectSelectionList.getRowLeft();
                final int integer4 = realmsObjectSelectionList.getRowTop(integer2);
                final int integer5 = (int)(double5 - integer3);
                final int integer6 = (int)(double6 - integer4);
                for (final RowButton cwb15 : list) {
                    if (integer5 >= cwb15.xOffset && integer5 <= cwb15.getRight() && integer6 >= cwb15.yOffset && integer6 <= cwb15.getBottom()) {
                        cwb15.onClick(integer2);
                    }
                }
            }
        }
    }
}
