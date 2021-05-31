package net.minecraft.client.gui.components;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public abstract class AbstractSliderButton extends AbstractWidget {
    protected final Options options;
    protected double value;
    
    protected AbstractSliderButton(final int integer1, final int integer2, final int integer3, final int integer4, final double double5) {
        this(Minecraft.getInstance().options, integer1, integer2, integer3, integer4, double5);
    }
    
    protected AbstractSliderButton(final Options cyg, final int integer2, final int integer3, final int integer4, final int integer5, final double double6) {
        super(integer2, integer3, integer4, integer5, "");
        this.options = cyg;
        this.value = double6;
    }
    
    @Override
    protected int getYImage(final boolean boolean1) {
        return 0;
    }
    
    @Override
    protected String getNarrationMessage() {
        return I18n.get("gui.narrate.slider", this.getMessage());
    }
    
    @Override
    protected void renderBg(final Minecraft cyc, final int integer2, final int integer3) {
        cyc.getTextureManager().bind(AbstractSliderButton.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer4 = (this.isHovered() ? 2 : 1) * 20;
        this.blit(this.x + (int)(this.value * (this.width - 8)), this.y, 0, 46 + integer4, 4, 20);
        this.blit(this.x + (int)(this.value * (this.width - 8)) + 4, this.y, 196, 46 + integer4, 4, 20);
    }
    
    @Override
    public void onClick(final double double1, final double double2) {
        this.setValueFromMouse(double1);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        final boolean boolean5 = integer1 == 263;
        if (boolean5 || integer1 == 262) {
            final float float6 = boolean5 ? -1.0f : 1.0f;
            this.setValue(this.value + float6 / (this.width - 8));
        }
        return false;
    }
    
    private void setValueFromMouse(final double double1) {
        this.setValue((double1 - (this.x + 4)) / (this.width - 8));
    }
    
    private void setValue(final double double1) {
        final double double2 = this.value;
        this.value = Mth.clamp(double1, 0.0, 1.0);
        if (double2 != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }
    
    @Override
    protected void onDrag(final double double1, final double double2, final double double3, final double double4) {
        this.setValueFromMouse(double1);
        super.onDrag(double1, double2, double3, double4);
    }
    
    @Override
    public void playDownSound(final SoundManager eap) {
    }
    
    @Override
    public void onRelease(final double double1, final double double2) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }
    
    protected abstract void updateMessage();
    
    protected abstract void applyValue();
}
