package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public class RealmsLabelProxy implements GuiEventListener {
    private final RealmsLabel label;
    
    public RealmsLabelProxy(final RealmsLabel realmsLabel) {
        this.label = realmsLabel;
    }
    
    public RealmsLabel getLabel() {
        return this.label;
    }
}
