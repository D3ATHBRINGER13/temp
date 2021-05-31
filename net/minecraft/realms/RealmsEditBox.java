package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;

public class RealmsEditBox extends RealmsGuiEventListener {
    private final EditBox editBox;
    
    public RealmsEditBox(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final String string) {
        this.editBox = new EditBox(Minecraft.getInstance().font, integer2, integer3, integer4, integer5, null, string);
    }
    
    public String getValue() {
        return this.editBox.getValue();
    }
    
    public void tick() {
        this.editBox.tick();
    }
    
    public void setValue(final String string) {
        this.editBox.setValue(string);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        return this.editBox.charTyped(character, integer);
    }
    
    @Override
    public GuiEventListener getProxy() {
        return this.editBox;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return this.editBox.keyPressed(integer1, integer2, integer3);
    }
    
    public boolean isFocused() {
        return this.editBox.isFocused();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return this.editBox.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double2, final int integer) {
        return this.editBox.mouseReleased(double1, double2, integer);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double2, final int integer, final double double4, final double double5) {
        return this.editBox.mouseDragged(double1, double2, integer, double4, double5);
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return this.editBox.mouseScrolled(double1, double2, double3);
    }
    
    public void render(final int integer1, final int integer2, final float float3) {
        this.editBox.render(integer1, integer2, float3);
    }
    
    public void setMaxLength(final int integer) {
        this.editBox.setMaxLength(integer);
    }
    
    public void setIsEditable(final boolean boolean1) {
        this.editBox.setEditable(boolean1);
    }
}
