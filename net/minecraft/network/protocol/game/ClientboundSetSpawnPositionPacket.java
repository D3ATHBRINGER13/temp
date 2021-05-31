package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetSpawnPositionPacket implements Packet<ClientGamePacketListener> {
    private BlockPos pos;
    
    public ClientboundSetSpawnPositionPacket() {
    }
    
    public ClientboundSetSpawnPositionPacket(final BlockPos ew) {
        this.pos = ew;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetSpawn(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
}
