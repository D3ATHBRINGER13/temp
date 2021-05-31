package net.minecraft.network.protocol;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;

public interface Packet<T extends PacketListener> {
    void read(final FriendlyByteBuf je) throws IOException;
    
    void write(final FriendlyByteBuf je) throws IOException;
    
    void handle(final T jh);
    
    default boolean isSkippable() {
        return false;
    }
}
