package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundAnimatePacket implements Packet<ClientGamePacketListener> {
    private int id;
    private int action;
    
    public ClientboundAnimatePacket() {
    }
    
    public ClientboundAnimatePacket(final Entity aio, final int integer) {
        this.id = aio.getId();
        this.action = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.action = je.readUnsignedByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeByte(this.action);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAnimate(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getAction() {
        return this.action;
    }
}
