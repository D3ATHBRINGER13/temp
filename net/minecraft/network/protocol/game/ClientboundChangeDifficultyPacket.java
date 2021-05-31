package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.network.protocol.Packet;

public class ClientboundChangeDifficultyPacket implements Packet<ClientGamePacketListener> {
    private Difficulty difficulty;
    private boolean locked;
    
    public ClientboundChangeDifficultyPacket() {
    }
    
    public ClientboundChangeDifficultyPacket(final Difficulty ahg, final boolean boolean2) {
        this.difficulty = ahg;
        this.locked = boolean2;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleChangeDifficulty(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.difficulty = Difficulty.byId(je.readUnsignedByte());
        this.locked = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.difficulty.getId());
        je.writeBoolean(this.locked);
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}
