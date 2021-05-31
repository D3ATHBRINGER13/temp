package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundRotateHeadPacket implements Packet<ClientGamePacketListener> {
    private int entityId;
    private byte yHeadRot;
    
    public ClientboundRotateHeadPacket() {
    }
    
    public ClientboundRotateHeadPacket(final Entity aio, final byte byte2) {
        this.entityId = aio.getId();
        this.yHeadRot = byte2;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
        this.yHeadRot = je.readByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
        je.writeByte(this.yHeadRot);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleRotateMob(this);
    }
    
    public Entity getEntity(final Level bhr) {
        return bhr.getEntity(this.entityId);
    }
    
    public byte getYHeadRot() {
        return this.yHeadRot;
    }
}
