package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameProfilePacket implements Packet<ClientLoginPacketListener> {
    private GameProfile gameProfile;
    
    public ClientboundGameProfilePacket() {
    }
    
    public ClientboundGameProfilePacket(final GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        final String string3 = je.readUtf(36);
        final String string4 = je.readUtf(16);
        final UUID uUID5 = UUID.fromString(string3);
        this.gameProfile = new GameProfile(uUID5, string4);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        final UUID uUID3 = this.gameProfile.getId();
        je.writeUtf((uUID3 == null) ? "" : uUID3.toString());
        je.writeUtf(this.gameProfile.getName());
    }
    
    public void handle(final ClientLoginPacketListener pq) {
        pq.handleGameProfile(this);
    }
    
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}
