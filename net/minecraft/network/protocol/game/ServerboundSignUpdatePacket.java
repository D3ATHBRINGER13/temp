package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ServerboundSignUpdatePacket implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private String[] lines;
    
    public ServerboundSignUpdatePacket() {
    }
    
    public ServerboundSignUpdatePacket(final BlockPos ew, final Component jo2, final Component jo3, final Component jo4, final Component jo5) {
        this.pos = ew;
        this.lines = new String[] { jo2.getString(), jo3.getString(), jo4.getString(), jo5.getString() };
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
        this.lines = new String[4];
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            this.lines[integer3] = je.readUtf(384);
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            je.writeUtf(this.lines[integer3]);
        }
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSignUpdate(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public String[] getLines() {
        return this.lines;
    }
}
