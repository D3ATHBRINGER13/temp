package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;

public class ClientboundTagQueryPacket implements Packet<ClientGamePacketListener> {
    private int transactionId;
    @Nullable
    private CompoundTag tag;
    
    public ClientboundTagQueryPacket() {
    }
    
    public ClientboundTagQueryPacket(final int integer, @Nullable final CompoundTag id) {
        this.transactionId = integer;
        this.tag = id;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.transactionId = je.readVarInt();
        this.tag = je.readNbt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.transactionId);
        je.writeNbt(this.tag);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleTagQueryPacket(this);
    }
    
    public int getTransactionId() {
        return this.transactionId;
    }
    
    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
    
    public boolean isSkippable() {
        return true;
    }
}
