package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelEventPacket implements Packet<ClientGamePacketListener> {
    private int type;
    private BlockPos pos;
    private int data;
    private boolean globalEvent;
    
    public ClientboundLevelEventPacket() {
    }
    
    public ClientboundLevelEventPacket(final int integer1, final BlockPos ew, final int integer3, final boolean boolean4) {
        this.type = integer1;
        this.pos = ew.immutable();
        this.data = integer3;
        this.globalEvent = boolean4;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.type = je.readInt();
        this.pos = je.readBlockPos();
        this.data = je.readInt();
        this.globalEvent = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.type);
        je.writeBlockPos(this.pos);
        je.writeInt(this.data);
        je.writeBoolean(this.globalEvent);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleLevelEvent(this);
    }
    
    public boolean isGlobalEvent() {
        return this.globalEvent;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getData() {
        return this.data;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
}
