package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerClosePacket implements Packet<ServerGamePacketListener> {
    private int containerId;
    
    public ServerboundContainerClosePacket() {
    }
    
    public ServerboundContainerClosePacket(final int integer) {
        this.containerId = integer;
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleContainerClose(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
    }
}
