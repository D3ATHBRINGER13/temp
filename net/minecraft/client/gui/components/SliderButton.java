package net.minecraft.client.gui.components;

import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;

public class SliderButton extends AbstractSliderButton {
    private final ProgressOption option;
    
    public SliderButton(final Options cyg, final int integer2, final int integer3, final int integer4, final int integer5, final ProgressOption cyi) {
        super(cyg, integer2, integer3, integer4, integer5, (float)cyi.toPct(cyi.get(cyg)));
        this.option = cyi;
        this.updateMessage();
    }
    
    @Override
    protected void applyValue() {
        this.option.set(this.options, this.option.toValue(this.value));
        this.options.save();
    }
    
    @Override
    protected void updateMessage() {
        this.setMessage(this.option.getMessage(this.options));
    }
}
