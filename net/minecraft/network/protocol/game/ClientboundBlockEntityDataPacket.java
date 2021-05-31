package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockEntityDataPacket implements Packet<ClientGamePacketListener> {
    private BlockPos pos;
    private int type;
    private CompoundTag tag;
    
    public ClientboundBlockEntityDataPacket() {
    }
    
    public ClientboundBlockEntityDataPacket(final BlockPos ew, final int integer, final CompoundTag id) {
        this.pos = ew;
        this.type = integer;
        this.tag = id;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
        this.type = je.readUnsignedByte();
        this.tag = je.readNbt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
        je.writeByte((byte)this.type);
        je.writeNbt(this.tag);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleBlockEntityData(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public int getType() {
        return this.type;
    }
    
    public CompoundTag getTag() {
        return this.tag;
    }
}
