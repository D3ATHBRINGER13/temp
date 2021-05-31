package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundStatusRequestPacket implements Packet<ServerStatusPacketListener> {
    public void read(final FriendlyByteBuf je) throws IOException {
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
    }
    
    public void handle(final ServerStatusPacketListener qg) {
        qg.handleStatusRequest(this);
    }
}
