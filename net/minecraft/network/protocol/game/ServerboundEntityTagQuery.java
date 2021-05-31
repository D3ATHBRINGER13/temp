package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundEntityTagQuery implements Packet<ServerGamePacketListener> {
    private int transactionId;
    private int entityId;
    
    public ServerboundEntityTagQuery() {
    }
    
    public ServerboundEntityTagQuery(final int integer1, final int integer2) {
        this.transactionId = integer1;
        this.entityId = integer2;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.transactionId = je.readVarInt();
        this.entityId = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.transactionId);
        je.writeVarInt(this.entityId);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleEntityTagQuery(this);
    }
    
    public int getTransactionId() {
        return this.transactionId;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
}
