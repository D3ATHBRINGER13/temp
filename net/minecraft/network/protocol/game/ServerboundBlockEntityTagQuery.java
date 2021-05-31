package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ServerboundBlockEntityTagQuery implements Packet<ServerGamePacketListener> {
    private int transactionId;
    private BlockPos pos;
    
    public ServerboundBlockEntityTagQuery() {
    }
    
    public ServerboundBlockEntityTagQuery(final int integer, final BlockPos ew) {
        this.transactionId = integer;
        this.pos = ew;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.transactionId = je.readVarInt();
        this.pos = je.readBlockPos();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.transactionId);
        je.writeBlockPos(this.pos);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleBlockEntityTagQuery(this);
    }
    
    public int getTransactionId() {
        return this.transactionId;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
}
