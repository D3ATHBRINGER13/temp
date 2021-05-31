package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheRadiusPacket implements Packet<ClientGamePacketListener> {
    private int radius;
    
    public ClientboundSetChunkCacheRadiusPacket() {
    }
    
    public ClientboundSetChunkCacheRadiusPacket(final int integer) {
        this.radius = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.radius = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.radius);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetChunkCacheRadius(this);
    }
    
    public int getRadius() {
        return this.radius;
    }
}
