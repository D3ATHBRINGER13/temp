package net.minecraft.realms;

import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class RealmListEntry extends ObjectSelectionList.Entry<RealmListEntry> {
    @Override
    public abstract void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9);
    
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return false;
    }
}
