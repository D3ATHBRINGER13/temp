package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginDisconnectPacket implements Packet<ClientLoginPacketListener> {
    private Component reason;
    
    public ClientboundLoginDisconnectPacket() {
    }
    
    public ClientboundLoginDisconnectPacket(final Component jo) {
        this.reason = jo;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.reason = Component.Serializer.fromJsonLenient(je.readUtf(262144));
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeComponent(this.reason);
    }
    
    public void handle(final ClientLoginPacketListener pq) {
        pq.handleDisconnect(this);
    }
    
    public Component getReason() {
        return this.reason;
    }
}
