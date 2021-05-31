package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetCameraPacket implements Packet<ClientGamePacketListener> {
    public int cameraId;
    
    public ClientboundSetCameraPacket() {
    }
    
    public ClientboundSetCameraPacket(final Entity aio) {
        this.cameraId = aio.getId();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.cameraId = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.cameraId);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetCamera(this);
    }
    
    @Nullable
    public Entity getEntity(final Level bhr) {
        return bhr.getEntity(this.cameraId);
    }
}
