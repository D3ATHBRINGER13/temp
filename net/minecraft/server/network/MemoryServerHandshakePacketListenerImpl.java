package net.minecraft.server.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;

public class MemoryServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final Connection connection;
    
    public MemoryServerHandshakePacketListenerImpl(final MinecraftServer minecraftServer, final Connection jc) {
        this.server = minecraftServer;
        this.connection = jc;
    }
    
    public void handleIntention(final ClientIntentionPacket pn) {
        this.connection.setProtocol(pn.getIntention());
        this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
    }
    
    public void onDisconnect(final Component jo) {
    }
    
    public Connection getConnection() {
        return this.connection;
    }
}
