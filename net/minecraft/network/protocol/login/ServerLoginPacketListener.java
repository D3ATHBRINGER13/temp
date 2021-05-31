package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface ServerLoginPacketListener extends PacketListener {
    void handleHello(final ServerboundHelloPacket py);
    
    void handleKey(final ServerboundKeyPacket pz);
    
    void handleCustomQueryPacket(final ServerboundCustomQueryPacket px);
}
