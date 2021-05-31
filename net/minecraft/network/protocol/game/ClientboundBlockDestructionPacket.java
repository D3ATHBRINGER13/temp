package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockDestructionPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private BlockPos pos;
    private int progress;
    
    public ClientboundBlockDestructionPacket() {
    }
    
    public ClientboundBlockDestructionPacket(final int integer1, final BlockPos ew, final int integer3) {
        this.id = integer1;
        this.pos = ew;
        this.progress = integer3;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.pos = je.readBlockPos();
        this.progress = je.readUnsignedByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeBlockPos(this.pos);
        je.writeByte(this.progress);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleBlockDestruction(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public int getProgress() {
        return this.progress;
    }
}
