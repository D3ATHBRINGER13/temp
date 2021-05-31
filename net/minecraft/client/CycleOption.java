package net.minecraft.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.components.AbstractWidget;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;

public class CycleOption extends Option {
    private final BiConsumer<Options, Integer> setter;
    private final BiFunction<Options, CycleOption, String> toString;
    
    public CycleOption(final String string, final BiConsumer<Options, Integer> biConsumer, final BiFunction<Options, CycleOption, String> biFunction) {
        super(string);
        this.setter = biConsumer;
        this.toString = biFunction;
    }
    
    public void toggle(final Options cyg, final int integer) {
        this.setter.accept(cyg, integer);
        cyg.save();
    }
    
    @Override
    public AbstractWidget createButton(final Options cyg, final int integer2, final int integer3, final int integer4) {
        return new OptionButton(integer2, integer3, integer4, 20, this, this.getMessage(cyg), czi -> {
            this.toggle(cyg, 1);
            czi.setMessage(this.getMessage(cyg));
        });
    }
    
    public String getMessage(final Options cyg) {
        return (String)this.toString.apply(cyg, this);
    }
}
