package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundLockDifficultyPacket implements Packet<ServerGamePacketListener> {
    private boolean locked;
    
    public ServerboundLockDifficultyPacket() {
    }
    
    public ServerboundLockDifficultyPacket(final boolean boolean1) {
        this.locked = boolean1;
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleLockDifficulty(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.locked = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBoolean(this.locked);
    }
    
    public boolean isLocked() {
        return this.locked;
    }
}
