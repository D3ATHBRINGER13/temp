package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerAckPacket implements Packet<ClientGamePacketListener> {
    private int containerId;
    private short uid;
    private boolean accepted;
    
    public ClientboundContainerAckPacket() {
    }
    
    public ClientboundContainerAckPacket(final int integer, final short short2, final boolean boolean3) {
        this.containerId = integer;
        this.uid = short2;
        this.accepted = boolean3;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleContainerAck(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readUnsignedByte();
        this.uid = je.readShort();
        this.accepted = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeShort(this.uid);
        je.writeBoolean(this.accepted);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public short getUid() {
        return this.uid;
    }
    
    public boolean isAccepted() {
        return this.accepted;
    }
}
