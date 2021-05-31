package net.minecraft.client.gui.components;

import net.minecraft.client.Option;

public class OptionButton extends Button {
    private final Option option;
    
    public OptionButton(final int integer1, final int integer2, final int integer3, final int integer4, final Option cyf, final String string, final OnPress a) {
        super(integer1, integer2, integer3, integer4, string, a);
        this.option = cyf;
    }
}
