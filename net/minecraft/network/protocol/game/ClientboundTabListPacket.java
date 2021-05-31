package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundTabListPacket implements Packet<ClientGamePacketListener> {
    private Component header;
    private Component footer;
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.header = je.readComponent();
        this.footer = je.readComponent();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeComponent(this.header);
        je.writeComponent(this.footer);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleTabListCustomisation(this);
    }
    
    public Component getHeader() {
        return this.header;
    }
    
    public Component getFooter() {
        return this.footer;
    }
}
