package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginCompressionPacket implements Packet<ClientLoginPacketListener> {
    private int compressionThreshold;
    
    public ClientboundLoginCompressionPacket() {
    }
    
    public ClientboundLoginCompressionPacket(final int integer) {
        this.compressionThreshold = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.compressionThreshold = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.compressionThreshold);
    }
    
    public void handle(final ClientLoginPacketListener pq) {
        pq.handleCompression(this);
    }
    
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}
