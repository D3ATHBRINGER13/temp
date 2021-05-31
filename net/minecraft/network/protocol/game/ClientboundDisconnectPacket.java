package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundDisconnectPacket implements Packet<ClientGamePacketListener> {
    private Component reason;
    
    public ClientboundDisconnectPacket() {
    }
    
    public ClientboundDisconnectPacket(final Component jo) {
        this.reason = jo;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.reason = je.readComponent();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeComponent(this.reason);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleDisconnect(this);
    }
    
    public Component getReason() {
        return this.reason;
    }
}
