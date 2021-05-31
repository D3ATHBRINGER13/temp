package net.minecraft.world.level.chunk;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;

public interface Palette<T> {
    int idFor(final T object);
    
    boolean maybeHas(final T object);
    
    @Nullable
    T valueFor(final int integer);
    
    void read(final FriendlyByteBuf je);
    
    void write(final FriendlyByteBuf je);
    
    int getSerializedSize();
    
    void read(final ListTag ik);
}
