package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
    private Action action;
    
    public ServerboundClientCommandPacket() {
    }
    
    public ServerboundClientCommandPacket(final Action a) {
        this.action = a;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.action = je.<Action>readEnum(Action.class);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.action);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleClientCommand(this);
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public enum Action {
        PERFORM_RESPAWN, 
        REQUEST_STATS;
    }
}
