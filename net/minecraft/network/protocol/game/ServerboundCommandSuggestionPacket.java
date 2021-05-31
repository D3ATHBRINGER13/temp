package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCommandSuggestionPacket implements Packet<ServerGamePacketListener> {
    private int id;
    private String command;
    
    public ServerboundCommandSuggestionPacket() {
    }
    
    public ServerboundCommandSuggestionPacket(final int integer, final String string) {
        this.id = integer;
        this.command = string;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.command = je.readUtf(32500);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeUtf(this.command, 32500);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleCustomCommandSuggestions(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getCommand() {
        return this.command;
    }
}
