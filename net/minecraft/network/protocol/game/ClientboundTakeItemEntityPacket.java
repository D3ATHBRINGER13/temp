package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundTakeItemEntityPacket implements Packet<ClientGamePacketListener> {
    private int itemId;
    private int playerId;
    private int amount;
    
    public ClientboundTakeItemEntityPacket() {
    }
    
    public ClientboundTakeItemEntityPacket(final int integer1, final int integer2, final int integer3) {
        this.itemId = integer1;
        this.playerId = integer2;
        this.amount = integer3;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.itemId = je.readVarInt();
        this.playerId = je.readVarInt();
        this.amount = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.itemId);
        je.writeVarInt(this.playerId);
        je.writeVarInt(this.amount);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleTakeItemEntity(this);
    }
    
    public int getItemId() {
        return this.itemId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getAmount() {
        return this.amount;
    }
}
