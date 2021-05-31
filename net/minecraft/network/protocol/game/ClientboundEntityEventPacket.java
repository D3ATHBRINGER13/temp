package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundEntityEventPacket implements Packet<ClientGamePacketListener> {
    private int entityId;
    private byte eventId;
    
    public ClientboundEntityEventPacket() {
    }
    
    public ClientboundEntityEventPacket(final Entity aio, final byte byte2) {
        this.entityId = aio.getId();
        this.eventId = byte2;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readInt();
        this.eventId = je.readByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.entityId);
        je.writeByte(this.eventId);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleEntityEvent(this);
    }
    
    public Entity getEntity(final Level bhr) {
        return bhr.getEntity(this.entityId);
    }
    
    public byte getEventId() {
        return this.eventId;
    }
}
