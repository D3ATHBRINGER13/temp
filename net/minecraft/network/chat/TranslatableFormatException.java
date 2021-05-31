package net.minecraft.network.chat;

public class TranslatableFormatException extends IllegalArgumentException {
    public TranslatableFormatException(final TranslatableComponent jy, final String string) {
        super(String.format("Error parsing: %s: %s", new Object[] { jy, string }));
    }
    
    public TranslatableFormatException(final TranslatableComponent jy, final int integer) {
        super(String.format("Invalid index %d requested for %s", new Object[] { integer, jy }));
    }
    
    public TranslatableFormatException(final TranslatableComponent jy, final Throwable throwable) {
        super(String.format("Error while parsing: %s", new Object[] { jy }), throwable);
    }
}
