package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.network.protocol.Packet;

public class ServerboundChangeDifficultyPacket implements Packet<ServerGamePacketListener> {
    private Difficulty difficulty;
    
    public ServerboundChangeDifficultyPacket() {
    }
    
    public ServerboundChangeDifficultyPacket(final Difficulty ahg) {
        this.difficulty = ahg;
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleChangeDifficulty(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.difficulty = Difficulty.byId(je.readUnsignedByte());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeByte(this.difficulty.getId());
    }
    
    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}
