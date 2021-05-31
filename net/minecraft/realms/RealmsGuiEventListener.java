package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class RealmsGuiEventListener {
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return false;
    }
    
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return false;
    }
    
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return false;
    }
    
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return false;
    }
    
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return false;
    }
    
    public boolean keyReleased(final int integer1, final int integer2, final int integer3) {
        return false;
    }
    
    public boolean charTyped(final char character, final int integer) {
        return false;
    }
    
    public abstract GuiEventListener getProxy();
}
