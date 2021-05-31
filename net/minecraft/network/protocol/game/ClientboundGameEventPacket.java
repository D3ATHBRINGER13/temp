package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameEventPacket implements Packet<ClientGamePacketListener> {
    public static final String[] EVENT_LANGUAGE_ID;
    private int event;
    private float param;
    
    public ClientboundGameEventPacket() {
    }
    
    public ClientboundGameEventPacket(final int integer, final float float2) {
        this.event = integer;
        this.param = float2;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.event = je.readUnsignedByte();
        this.param = je.readFloat();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.event);
        je.writeFloat(this.param);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleGameEvent(this);
    }
    
    public int getEvent() {
        return this.event;
    }
    
    public float getParam() {
        return this.param;
    }
    
    static {
        EVENT_LANGUAGE_ID = new String[] { "block.minecraft.bed.not_valid" };
    }
}
