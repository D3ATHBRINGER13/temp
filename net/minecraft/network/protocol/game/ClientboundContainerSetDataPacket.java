package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int id;
    private int value;
    
    public ClientboundContainerSetDataPacket() {
    }
    
    public ClientboundContainerSetDataPacket(final int integer1, final int integer2, final int integer3) {
        this.containerId = integer1;
        this.id = integer2;
        this.value = integer3;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleContainerSetData(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readUnsignedByte();
        this.id = je.readShort();
        this.value = je.readShort();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeShort(this.id);
        je.writeShort(this.value);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getValue() {
        return this.value;
    }
}
