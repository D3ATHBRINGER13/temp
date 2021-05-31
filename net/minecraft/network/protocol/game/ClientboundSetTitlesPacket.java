package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitlesPacket implements Packet<ClientGamePacketListener> {
    private Type type;
    private Component text;
    private int fadeInTime;
    private int stayTime;
    private int fadeOutTime;
    
    public ClientboundSetTitlesPacket() {
    }
    
    public ClientboundSetTitlesPacket(final Type a, final Component jo) {
        this(a, jo, -1, -1, -1);
    }
    
    public ClientboundSetTitlesPacket(final int integer1, final int integer2, final int integer3) {
        this(Type.TIMES, null, integer1, integer2, integer3);
    }
    
    public ClientboundSetTitlesPacket(final Type a, @Nullable final Component jo, final int integer3, final int integer4, final int integer5) {
        this.type = a;
        this.text = jo;
        this.fadeInTime = integer3;
        this.stayTime = integer4;
        this.fadeOutTime = integer5;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.type = je.<Type>readEnum(Type.class);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            this.text = je.readComponent();
        }
        if (this.type == Type.TIMES) {
            this.fadeInTime = je.readInt();
            this.stayTime = je.readInt();
            this.fadeOutTime = je.readInt();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.type);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            je.writeComponent(this.text);
        }
        if (this.type == Type.TIMES) {
            je.writeInt(this.fadeInTime);
            je.writeInt(this.stayTime);
            je.writeInt(this.fadeOutTime);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetTitles(this);
    }
    
    public Type getType() {
        return this.type;
    }
    
    public Component getText() {
        return this.text;
    }
    
    public int getFadeInTime() {
        return this.fadeInTime;
    }
    
    public int getStayTime() {
        return this.stayTime;
    }
    
    public int getFadeOutTime() {
        return this.fadeOutTime;
    }
    
    public enum Type {
        TITLE, 
        SUBTITLE, 
        ACTIONBAR, 
        TIMES, 
        CLEAR, 
        RESET;
    }
}
