package net.minecraft.client.gui.spectator;

import net.minecraft.network.chat.Component;

public interface SpectatorMenuItem {
    void selectItem(final SpectatorMenu dfy);
    
    Component getName();
    
    void renderIcon(final float float1, final int integer);
    
    boolean isEnabled();
}
