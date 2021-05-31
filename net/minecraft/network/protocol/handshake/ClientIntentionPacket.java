package net.minecraft.network.protocol.handshake;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;

public class ClientIntentionPacket implements Packet<ServerHandshakePacketListener> {
    private int protocolVersion;
    private String hostName;
    private int port;
    private ConnectionProtocol intention;
    
    public ClientIntentionPacket() {
    }
    
    public ClientIntentionPacket(final String string, final int integer, final ConnectionProtocol jd) {
        this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
        this.hostName = string;
        this.port = integer;
        this.intention = jd;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.protocolVersion = je.readVarInt();
        this.hostName = je.readUtf(255);
        this.port = je.readUnsignedShort();
        this.intention = ConnectionProtocol.getById(je.readVarInt());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.protocolVersion);
        je.writeUtf(this.hostName);
        je.writeShort(this.port);
        je.writeVarInt(this.intention.getId());
    }
    
    public void handle(final ServerHandshakePacketListener po) {
        po.handleIntention(this);
    }
    
    public ConnectionProtocol getIntention() {
        return this.intention;
    }
    
    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}
