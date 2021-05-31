package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ServerboundCustomPayloadPacket implements Packet<ServerGamePacketListener> {
    public static final ResourceLocation BRAND;
    private ResourceLocation identifier;
    private FriendlyByteBuf data;
    
    public ServerboundCustomPayloadPacket() {
    }
    
    public ServerboundCustomPayloadPacket(final ResourceLocation qv, final FriendlyByteBuf je) {
        this.identifier = qv;
        this.data = je;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.identifier = je.readResourceLocation();
        final int integer3 = je.readableBytes();
        if (integer3 < 0 || integer3 > 32767) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new FriendlyByteBuf(je.readBytes(integer3));
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeResourceLocation(this.identifier);
        je.writeBytes(this.data);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
    
    static {
        BRAND = new ResourceLocation("brand");
    }
}
