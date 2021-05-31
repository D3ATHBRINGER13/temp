package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundChatPacket implements Packet<ClientGamePacketListener> {
    private Component message;
    private ChatType type;
    
    public ClientboundChatPacket() {
    }
    
    public ClientboundChatPacket(final Component jo) {
        this(jo, ChatType.SYSTEM);
    }
    
    public ClientboundChatPacket(final Component jo, final ChatType jm) {
        this.message = jo;
        this.type = jm;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.message = je.readComponent();
        this.type = ChatType.getForIndex(je.readByte());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeComponent(this.message);
        je.writeByte(this.type.getIndex());
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleChat(this);
    }
    
    public Component getMessage() {
        return this.message;
    }
    
    public boolean isSystem() {
        return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
    }
    
    public ChatType getType() {
        return this.type;
    }
    
    public boolean isSkippable() {
        return true;
    }
}
