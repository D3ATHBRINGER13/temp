package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundRenameItemPacket implements Packet<ServerGamePacketListener> {
    private String name;
    
    public ServerboundRenameItemPacket() {
    }
    
    public ServerboundRenameItemPacket(final String string) {
        this.name = string;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.name = je.readUtf(32767);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeUtf(this.name);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleRenameItem(this);
    }
    
    public String getName() {
        return this.name;
    }
}
