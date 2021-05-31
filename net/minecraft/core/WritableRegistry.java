package net.minecraft.core;

import net.minecraft.resources.ResourceLocation;

public abstract class WritableRegistry<T> extends Registry<T> {
    public abstract <V extends T> V registerMapping(final int integer, final ResourceLocation qv, final V object);
    
    public abstract <V extends T> V register(final ResourceLocation qv, final V object);
    
    public abstract boolean isEmpty();
}
