package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSelectTradePacket implements Packet<ServerGamePacketListener> {
    private int item;
    
    public ServerboundSelectTradePacket() {
    }
    
    public ServerboundSelectTradePacket(final int integer) {
        this.item = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.item = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.item);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSelectTrade(this);
    }
    
    public int getItem() {
        return this.item;
    }
}
