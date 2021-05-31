package net.minecraft.server.network;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public class ServerStatusPacketListenerImpl implements ServerStatusPacketListener {
    private static final Component DISCONNECT_REASON;
    private final MinecraftServer server;
    private final Connection connection;
    private boolean hasRequestedStatus;
    
    public ServerStatusPacketListenerImpl(final MinecraftServer minecraftServer, final Connection jc) {
        this.server = minecraftServer;
        this.connection = jc;
    }
    
    public void onDisconnect(final Component jo) {
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public void handleStatusRequest(final ServerboundStatusRequestPacket qi) {
        if (this.hasRequestedStatus) {
            this.connection.disconnect(ServerStatusPacketListenerImpl.DISCONNECT_REASON);
            return;
        }
        this.hasRequestedStatus = true;
        this.connection.send(new ClientboundStatusResponsePacket(this.server.getStatus()));
    }
    
    public void handlePingRequest(final ServerboundPingRequestPacket qh) {
        this.connection.send(new ClientboundPongResponsePacket(qh.getTime()));
        this.connection.disconnect(ServerStatusPacketListenerImpl.DISCONNECT_REASON);
    }
    
    static {
        DISCONNECT_REASON = new TranslatableComponent("multiplayer.status.request_handled", new Object[0]);
    }
}
