package net.minecraft.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.components.AbstractWidget;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BooleanOption extends Option {
    private final Predicate<Options> getter;
    private final BiConsumer<Options, Boolean> setter;
    
    public BooleanOption(final String string, final Predicate<Options> predicate, final BiConsumer<Options, Boolean> biConsumer) {
        super(string);
        this.getter = predicate;
        this.setter = biConsumer;
    }
    
    public void set(final Options cyg, final String string) {
        this.set(cyg, "true".equals(string));
    }
    
    public void toggle(final Options cyg) {
        this.set(cyg, !this.get(cyg));
        cyg.save();
    }
    
    private void set(final Options cyg, final boolean boolean2) {
        this.setter.accept(cyg, boolean2);
    }
    
    public boolean get(final Options cyg) {
        return this.getter.test(cyg);
    }
    
    @Override
    public AbstractWidget createButton(final Options cyg, final int integer2, final int integer3, final int integer4) {
        return new OptionButton(integer2, integer3, integer4, 20, this, this.getMessage(cyg), czi -> {
            this.toggle(cyg);
            czi.setMessage(this.getMessage(cyg));
        });
    }
    
    public String getMessage(final Options cyg) {
        return this.getCaption() + I18n.get(this.get(cyg) ? "options.on" : "options.off");
    }
}
