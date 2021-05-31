package net.minecraft.network.chat;

import java.util.function.Supplier;
import java.util.function.Function;

public class KeybindComponent extends BaseComponent {
    public static Function<String, Supplier<String>> keyResolver;
    private final String name;
    private Supplier<String> nameResolver;
    
    public KeybindComponent(final String string) {
        this.name = string;
    }
    
    public String getContents() {
        if (this.nameResolver == null) {
            this.nameResolver = (Supplier<String>)KeybindComponent.keyResolver.apply(this.name);
        }
        return (String)this.nameResolver.get();
    }
    
    public KeybindComponent copy() {
        return new KeybindComponent(this.name);
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof KeybindComponent) {
            final KeybindComponent js3 = (KeybindComponent)object;
            return this.name.equals(js3.name) && super.equals(object);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.name + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
    
    public String getName() {
        return this.name;
    }
    
    static {
        KeybindComponent.keyResolver = (Function<String, Supplier<String>>)(string -> () -> string);
    }
}
