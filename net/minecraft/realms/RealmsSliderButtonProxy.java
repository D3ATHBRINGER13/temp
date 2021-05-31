package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;

public class RealmsSliderButtonProxy extends AbstractSliderButton implements RealmsAbstractButtonProxy<RealmsSliderButton> {
    private final RealmsSliderButton button;
    
    public RealmsSliderButtonProxy(final RealmsSliderButton realmsSliderButton, final int integer2, final int integer3, final int integer4, final int integer5, final double double6) {
        super(integer2, integer3, integer4, integer5, double6);
        this.button = realmsSliderButton;
    }
    
    @Override
    public boolean active() {
        return this.active;
    }
    
    @Override
    public void active(final boolean boolean1) {
        this.active = boolean1;
    }
    
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    @Override
    public void setVisible(final boolean boolean1) {
        this.visible = boolean1;
    }
    
    @Override
    public void setMessage(final String string) {
        super.setMessage(string);
    }
    
    @Override
    public int getWidth() {
        return super.getWidth();
    }
    
    public int y() {
        return this.y;
    }
    
    @Override
    public void onClick(final double double1, final double double2) {
        this.button.onClick(double1, double2);
    }
    
    @Override
    public void onRelease(final double double1, final double double2) {
        this.button.onRelease(double1, double2);
    }
    
    public void updateMessage() {
        this.button.updateMessage();
    }
    
    public void applyValue() {
        this.button.applyValue();
    }
    
    public double getValue() {
        return this.value;
    }
    
    public void setValue(final double double1) {
        this.value = double1;
    }
    
    public void renderBg(final Minecraft cyc, final int integer2, final int integer3) {
        super.renderBg(cyc, integer2, integer3);
    }
    
    @Override
    public RealmsSliderButton getButton() {
        return this.button;
    }
    
    public int getYImage(final boolean boolean1) {
        return this.button.getYImage(boolean1);
    }
    
    public int getSuperYImage(final boolean boolean1) {
        return super.getYImage(boolean1);
    }
    
    public int getHeight() {
        return this.height;
    }
}
