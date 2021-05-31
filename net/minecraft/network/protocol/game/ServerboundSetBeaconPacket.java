package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetBeaconPacket implements Packet<ServerGamePacketListener> {
    private int primary;
    private int secondary;
    
    public ServerboundSetBeaconPacket() {
    }
    
    public ServerboundSetBeaconPacket(final int integer1, final int integer2) {
        this.primary = integer1;
        this.secondary = integer2;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.primary = je.readVarInt();
        this.secondary = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.primary);
        je.writeVarInt(this.secondary);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSetBeaconPacket(this);
    }
    
    public int getPrimary() {
        return this.primary;
    }
    
    public int getSecondary() {
        return this.secondary;
    }
}
