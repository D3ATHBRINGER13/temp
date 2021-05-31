package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface ClientStatusPacketListener extends PacketListener {
    void handleStatusResponse(final ClientboundStatusResponsePacket qe);
    
    void handlePongResponse(final ClientboundPongResponsePacket qd);
}
