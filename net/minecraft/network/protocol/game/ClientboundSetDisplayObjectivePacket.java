package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Objects;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.world.scores.Objective;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetDisplayObjectivePacket implements Packet<ClientGamePacketListener> {
    private int slot;
    private String objectiveName;
    
    public ClientboundSetDisplayObjectivePacket() {
    }
    
    public ClientboundSetDisplayObjectivePacket(final int integer, @Nullable final Objective ctf) {
        this.slot = integer;
        if (ctf == null) {
            this.objectiveName = "";
        }
        else {
            this.objectiveName = ctf.getName();
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.slot = je.readByte();
        this.objectiveName = je.readUtf(16);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.slot);
        je.writeUtf(this.objectiveName);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetDisplayObjective(this);
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    @Nullable
    public String getObjectiveName() {
        return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
    }
}
