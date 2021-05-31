package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetEntityLinkPacket implements Packet<ClientGamePacketListener> {
    private int sourceId;
    private int destId;
    
    public ClientboundSetEntityLinkPacket() {
    }
    
    public ClientboundSetEntityLinkPacket(final Entity aio1, @Nullable final Entity aio2) {
        this.sourceId = aio1.getId();
        this.destId = ((aio2 != null) ? aio2.getId() : 0);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.sourceId = je.readInt();
        this.destId = je.readInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.sourceId);
        je.writeInt(this.destId);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleEntityLinkPacket(this);
    }
    
    public int getSourceId() {
        return this.sourceId;
    }
    
    public int getDestId() {
        return this.destId;
    }
}
