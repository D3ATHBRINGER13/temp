package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundHorseScreenOpenPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int size;
    private int entityId;
    
    public ClientboundHorseScreenOpenPacket() {
    }
    
    public ClientboundHorseScreenOpenPacket(final int integer1, final int integer2, final int integer3) {
        this.containerId = integer1;
        this.size = integer2;
        this.entityId = integer3;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleHorseScreenOpen(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readUnsignedByte();
        this.size = je.readVarInt();
        this.entityId = je.readInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeVarInt(this.size);
        je.writeInt(this.entityId);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
}
