package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public class RealmsLabel extends RealmsGuiEventListener {
    private final RealmsLabelProxy proxy;
    private final String text;
    private final int x;
    private final int y;
    private final int color;
    
    public RealmsLabel(final String string, final int integer2, final int integer3, final int integer4) {
        this.proxy = new RealmsLabelProxy(this);
        this.text = string;
        this.x = integer2;
        this.y = integer3;
        this.color = integer4;
    }
    
    public void render(final RealmsScreen realmsScreen) {
        realmsScreen.drawCenteredString(this.text, this.x, this.y, this.color);
    }
    
    @Override
    public GuiEventListener getProxy() {
        return this.proxy;
    }
    
    public String getText() {
        return this.text;
    }
}
