package net.minecraft.server.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final Connection connection;
    
    public ServerHandshakePacketListenerImpl(final MinecraftServer minecraftServer, final Connection jc) {
        this.server = minecraftServer;
        this.connection = jc;
    }
    
    public void handleIntention(final ClientIntentionPacket pn) {
        switch (pn.getIntention()) {
            case LOGIN: {
                this.connection.setProtocol(ConnectionProtocol.LOGIN);
                if (pn.getProtocolVersion() > SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    final Component jo3 = new TranslatableComponent("multiplayer.disconnect.outdated_server", new Object[] { SharedConstants.getCurrentVersion().getName() });
                    this.connection.send(new ClientboundLoginDisconnectPacket(jo3));
                    this.connection.disconnect(jo3);
                    break;
                }
                if (pn.getProtocolVersion() < SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    final Component jo3 = new TranslatableComponent("multiplayer.disconnect.outdated_client", new Object[] { SharedConstants.getCurrentVersion().getName() });
                    this.connection.send(new ClientboundLoginDisconnectPacket(jo3));
                    this.connection.disconnect(jo3);
                    break;
                }
                this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
                break;
            }
            case STATUS: {
                this.connection.setProtocol(ConnectionProtocol.STATUS);
                this.connection.setListener(new ServerStatusPacketListenerImpl(this.server, this.connection));
                break;
            }
            default: {
                throw new UnsupportedOperationException(new StringBuilder().append("Invalid intention ").append(pn.getIntention()).toString());
            }
        }
    }
    
    public void onDisconnect(final Component jo) {
    }
    
    public Connection getConnection() {
        return this.connection;
    }
}
