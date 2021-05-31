package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface ClientLoginPacketListener extends PacketListener {
    void handleHello(final ClientboundHelloPacket pt);
    
    void handleGameProfile(final ClientboundGameProfilePacket ps);
    
    void handleDisconnect(final ClientboundLoginDisconnectPacket pv);
    
    void handleCompression(final ClientboundLoginCompressionPacket pu);
    
    void handleCustomQuery(final ClientboundCustomQueryPacket pr);
}
