package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

public class RealmsButtonProxy extends Button implements RealmsAbstractButtonProxy<RealmsButton> {
    private final RealmsButton button;
    
    public RealmsButtonProxy(final RealmsButton realmsButton, final int integer2, final int integer3, final String string, final int integer5, final int integer6, final OnPress a) {
        super(integer2, integer3, integer5, integer6, string, a);
        this.button = realmsButton;
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
    
    public void setMessage(final String string) {
        super.setMessage(string);
    }
    
    public int getWidth() {
        return super.getWidth();
    }
    
    public int y() {
        return this.y;
    }
    
    @Override
    public void onClick(final double double1, final double double2) {
        this.button.onPress();
    }
    
    public void onRelease(final double double1, final double double2) {
        this.button.onRelease(double1, double2);
    }
    
    public void renderBg(final Minecraft cyc, final int integer2, final int integer3) {
        this.button.renderBg(integer2, integer3);
    }
    
    public void renderButton(final int integer1, final int integer2, final float float3) {
        this.button.renderButton(integer1, integer2, float3);
    }
    
    public void superRenderButton(final int integer1, final int integer2, final float float3) {
        super.renderButton(integer1, integer2, float3);
    }
    
    @Override
    public RealmsButton getButton() {
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
    
    public boolean isHovered() {
        return super.isHovered();
    }
}
