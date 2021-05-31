package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCustomQueryPacket implements Packet<ServerLoginPacketListener> {
    private int transactionId;
    private FriendlyByteBuf data;
    
    public ServerboundCustomQueryPacket() {
    }
    
    public ServerboundCustomQueryPacket(final int integer, @Nullable final FriendlyByteBuf je) {
        this.transactionId = integer;
        this.data = je;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.transactionId = je.readVarInt();
        if (je.readBoolean()) {
            final int integer3 = je.readableBytes();
            if (integer3 < 0 || integer3 > 1048576) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.data = new FriendlyByteBuf(je.readBytes(integer3));
        }
        else {
            this.data = null;
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.transactionId);
        if (this.data != null) {
            je.writeBoolean(true);
            je.writeBytes(this.data.copy());
        }
        else {
            je.writeBoolean(false);
        }
    }
    
    public void handle(final ServerLoginPacketListener pw) {
        pw.handleCustomQueryPacket(this);
    }
}
