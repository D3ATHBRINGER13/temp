package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerAckPacket implements Packet<ServerGamePacketListener> {
    private int containerId;
    private short uid;
    private boolean accepted;
    
    public ServerboundContainerAckPacket() {
    }
    
    public ServerboundContainerAckPacket(final int integer, final short short2, final boolean boolean3) {
        this.containerId = integer;
        this.uid = short2;
        this.accepted = boolean3;
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleContainerAck(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
        this.uid = je.readShort();
        this.accepted = (je.readByte() != 0);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeShort(this.uid);
        je.writeByte(this.accepted ? 1 : 0);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public short getUid() {
        return this.uid;
    }
}
