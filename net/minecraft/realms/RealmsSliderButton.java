package net.minecraft.realms;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

public abstract class RealmsSliderButton extends AbstractRealmsButton<RealmsSliderButtonProxy> {
    protected static final ResourceLocation WIDGETS_LOCATION;
    private final int id;
    private final RealmsSliderButtonProxy proxy;
    private final double minValue;
    private final double maxValue;
    
    public RealmsSliderButton(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final double double6, final double double7) {
        this.id = integer1;
        this.minValue = double6;
        this.maxValue = double7;
        this.proxy = new RealmsSliderButtonProxy(this, integer2, integer3, integer4, 20, this.toPct(integer5));
        this.getProxy().setMessage(this.getMessage());
    }
    
    public String getMessage() {
        return "";
    }
    
    public double toPct(final double double1) {
        return Mth.clamp((this.clamp(double1) - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
    }
    
    public double toValue(final double double1) {
        return this.clamp(Mth.lerp(Mth.clamp(double1, 0.0, 1.0), this.minValue, this.maxValue));
    }
    
    public double clamp(final double double1) {
        return Mth.clamp(double1, this.minValue, this.maxValue);
    }
    
    public int getYImage(final boolean boolean1) {
        return 0;
    }
    
    public void onClick(final double double1, final double double2) {
    }
    
    public void onRelease(final double double1, final double double2) {
    }
    
    @Override
    public RealmsSliderButtonProxy getProxy() {
        return this.proxy;
    }
    
    public double getValue() {
        return this.proxy.getValue();
    }
    
    public void setValue(final double double1) {
        this.proxy.setValue(double1);
    }
    
    public int id() {
        return this.id;
    }
    
    public void setMessage(final String string) {
        this.proxy.setMessage(string);
    }
    
    public int getWidth() {
        return this.proxy.getWidth();
    }
    
    public int getHeight() {
        return this.proxy.getHeight();
    }
    
    public int y() {
        return this.proxy.y();
    }
    
    public abstract void applyValue();
    
    public void updateMessage() {
        this.proxy.setMessage(this.getMessage());
    }
    
    static {
        WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    }
}
