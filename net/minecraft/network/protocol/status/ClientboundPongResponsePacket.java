package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPongResponsePacket implements Packet<ClientStatusPacketListener> {
    private long time;
    
    public ClientboundPongResponsePacket() {
    }
    
    public ClientboundPongResponsePacket(final long long1) {
        this.time = long1;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.time = je.readLong();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeLong(this.time);
    }
    
    public void handle(final ClientStatusPacketListener qc) {
        qc.handlePongResponse(this);
    }
}
