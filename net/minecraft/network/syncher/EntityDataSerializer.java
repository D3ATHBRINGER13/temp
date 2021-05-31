package net.minecraft.network.syncher;

import net.minecraft.network.FriendlyByteBuf;

public interface EntityDataSerializer<T> {
    void write(final FriendlyByteBuf je, final T object);
    
    T read(final FriendlyByteBuf je);
    
    default EntityDataAccessor<T> createAccessor(final int integer) {
        return new EntityDataAccessor<T>(integer, this);
    }
    
    T copy(final T object);
}
