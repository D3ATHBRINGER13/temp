package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ClientboundCustomQueryPacket implements Packet<ClientLoginPacketListener> {
    private int transactionId;
    private ResourceLocation identifier;
    private FriendlyByteBuf data;
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.transactionId = je.readVarInt();
        this.identifier = je.readResourceLocation();
        final int integer3 = je.readableBytes();
        if (integer3 < 0 || integer3 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new FriendlyByteBuf(je.readBytes(integer3));
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.transactionId);
        je.writeResourceLocation(this.identifier);
        je.writeBytes(this.data.copy());
    }
    
    public void handle(final ClientLoginPacketListener pq) {
        pq.handleCustomQuery(this);
    }
    
    public int getTransactionId() {
        return this.transactionId;
    }
}
