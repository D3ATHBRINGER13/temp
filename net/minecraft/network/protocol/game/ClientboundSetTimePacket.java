package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTimePacket implements Packet<ClientGamePacketListener> {
    private long gameTime;
    private long dayTime;
    
    public ClientboundSetTimePacket() {
    }
    
    public ClientboundSetTimePacket(final long long1, final long long2, final boolean boolean3) {
        this.gameTime = long1;
        this.dayTime = long2;
        if (!boolean3) {
            this.dayTime = -this.dayTime;
            if (this.dayTime == 0L) {
                this.dayTime = -1L;
            }
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.gameTime = je.readLong();
        this.dayTime = je.readLong();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeLong(this.gameTime);
        je.writeLong(this.dayTime);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSetTime(this);
    }
    
    public long getGameTime() {
        return this.gameTime;
    }
    
    public long getDayTime() {
        return this.dayTime;
    }
}
