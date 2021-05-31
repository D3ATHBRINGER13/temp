package net.minecraft.client.gui.components;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class VolumeSlider extends AbstractSliderButton {
    private final SoundSource source;
    
    public VolumeSlider(final Minecraft cyc, final int integer2, final int integer3, final SoundSource yq, final int integer5) {
        super(cyc.options, integer2, integer3, integer5, 20, cyc.options.getSoundSourceVolume(yq));
        this.source = yq;
        this.updateMessage();
    }
    
    @Override
    protected void updateMessage() {
        final String string2 = ((float)this.value == this.getYImage(false)) ? I18n.get("options.off") : new StringBuilder().append((int)((float)this.value * 100.0f)).append("%").toString();
        this.setMessage(I18n.get("soundCategory." + this.source.getName()) + ": " + string2);
    }
    
    @Override
    protected void applyValue() {
        this.options.setSoundCategoryVolume(this.source, (float)this.value);
        this.options.save();
    }
}
