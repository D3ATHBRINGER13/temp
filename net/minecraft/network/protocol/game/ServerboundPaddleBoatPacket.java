package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPaddleBoatPacket implements Packet<ServerGamePacketListener> {
    private boolean left;
    private boolean right;
    
    public ServerboundPaddleBoatPacket() {
    }
    
    public ServerboundPaddleBoatPacket(final boolean boolean1, final boolean boolean2) {
        this.left = boolean1;
        this.right = boolean2;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.left = je.readBoolean();
        this.right = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBoolean(this.left);
        je.writeBoolean(this.right);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handlePaddleBoat(this);
    }
    
    public boolean getLeft() {
        return this.left;
    }
    
    public boolean getRight() {
        return this.right;
    }
}
