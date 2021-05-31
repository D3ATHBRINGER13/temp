package net.minecraft.core;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class DefaultedRegistry<T> extends MappedRegistry<T> {
    private final ResourceLocation defaultKey;
    private T defaultValue;
    
    public DefaultedRegistry(final String string) {
        this.defaultKey = new ResourceLocation(string);
    }
    
    @Override
    public <V extends T> V registerMapping(final int integer, final ResourceLocation qv, final V object) {
        if (this.defaultKey.equals(qv)) {
            this.defaultValue = object;
        }
        return super.<V>registerMapping(integer, qv, object);
    }
    
    @Override
    public int getId(@Nullable final T object) {
        final int integer3 = super.getId(object);
        return (integer3 == -1) ? super.getId(this.defaultValue) : integer3;
    }
    
    @Nonnull
    @Override
    public ResourceLocation getKey(final T object) {
        final ResourceLocation qv3 = super.getKey(object);
        return (qv3 == null) ? this.defaultKey : qv3;
    }
    
    @Nonnull
    @Override
    public T get(@Nullable final ResourceLocation qv) {
        final T object3 = super.get(qv);
        return (object3 == null) ? this.defaultValue : object3;
    }
    
    @Nonnull
    @Override
    public T byId(final int integer) {
        final T object3 = super.byId(integer);
        return (object3 == null) ? this.defaultValue : object3;
    }
    
    @Nonnull
    @Override
    public T getRandom(final Random random) {
        final T object3 = super.getRandom(random);
        return (object3 == null) ? this.defaultValue : object3;
    }
    
    public ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }
}
