package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPingRequestPacket implements Packet<ServerStatusPacketListener> {
    private long time;
    
    public ServerboundPingRequestPacket() {
    }
    
    public ServerboundPingRequestPacket(final long long1) {
        this.time = long1;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.time = je.readLong();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeLong(this.time);
    }
    
    public void handle(final ServerStatusPacketListener qg) {
        qg.handlePingRequest(this);
    }
    
    public long getTime() {
        return this.time;
    }
}
