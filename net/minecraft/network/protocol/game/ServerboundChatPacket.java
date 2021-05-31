package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundChatPacket implements Packet<ServerGamePacketListener> {
    private String message;
    
    public ServerboundChatPacket() {
    }
    
    public ServerboundChatPacket(String string) {
        if (string.length() > 256) {
            string = string.substring(0, 256);
        }
        this.message = string;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.message = je.readUtf(256);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeUtf(this.message);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleChat(this);
    }
    
    public String getMessage() {
        return this.message;
    }
}
