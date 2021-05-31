package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientGamePacketListener> {
    private long id;
    
    public ClientboundKeepAlivePacket() {
    }
    
    public ClientboundKeepAlivePacket(final long long1) {
        this.id = long1;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleKeepAlive(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readLong();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeLong(this.id);
    }
    
    public long getId() {
        return this.id;
    }
}
