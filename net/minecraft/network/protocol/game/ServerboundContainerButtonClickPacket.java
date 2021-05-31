package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerButtonClickPacket implements Packet<ServerGamePacketListener> {
    private int containerId;
    private int buttonId;
    
    public ServerboundContainerButtonClickPacket() {
    }
    
    public ServerboundContainerButtonClickPacket(final int integer1, final int integer2) {
        this.containerId = integer1;
        this.buttonId = integer2;
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleContainerButtonClick(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.containerId = je.readByte();
        this.buttonId = je.readByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.containerId);
        je.writeByte(this.buttonId);
    }
    
    public int getContainerId() {
        return this.containerId;
    }
    
    public int getButtonId() {
        return this.buttonId;
    }
}
