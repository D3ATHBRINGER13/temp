package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.protocol.Packet;

public class ServerboundSwingPacket implements Packet<ServerGamePacketListener> {
    private InteractionHand hand;
    
    public ServerboundSwingPacket() {
    }
    
    public ServerboundSwingPacket(final InteractionHand ahi) {
        this.hand = ahi;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.hand = je.<InteractionHand>readEnum(InteractionHand.class);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.hand);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleAnimate(this);
    }
    
    public InteractionHand getHand() {
        return this.hand;
    }
}
