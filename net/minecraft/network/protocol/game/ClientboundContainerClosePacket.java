package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerClosePacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    
    public ClientboundContainerClosePacket() {
    }
    
    public ClientboundContainerClosePacket(final int integer) {
        this.containerId = integer;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleContainerClose(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readUnsignedByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
    }
}
