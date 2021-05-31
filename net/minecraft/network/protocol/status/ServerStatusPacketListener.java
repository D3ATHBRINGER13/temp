package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface ServerStatusPacketListener extends PacketListener {
    void handlePingRequest(final ServerboundPingRequestPacket qh);
    
    void handleStatusRequest(final ServerboundStatusRequestPacket qi);
}
