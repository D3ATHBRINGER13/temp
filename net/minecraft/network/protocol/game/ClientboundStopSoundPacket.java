package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ClientboundStopSoundPacket implements Packet<ClientGamePacketListener> {
    private ResourceLocation name;
    private SoundSource source;
    
    public ClientboundStopSoundPacket() {
    }
    
    public ClientboundStopSoundPacket(@Nullable final ResourceLocation qv, @Nullable final SoundSource yq) {
        this.name = qv;
        this.source = yq;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        final int integer3 = je.readByte();
        if ((integer3 & 0x1) > 0) {
            this.source = je.<SoundSource>readEnum(SoundSource.class);
        }
        if ((integer3 & 0x2) > 0) {
            this.name = je.readResourceLocation();
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        if (this.source != null) {
            if (this.name != null) {
                je.writeByte(3);
                je.writeEnum(this.source);
                je.writeResourceLocation(this.name);
            }
            else {
                je.writeByte(1);
                je.writeEnum(this.source);
            }
        }
        else if (this.name != null) {
            je.writeByte(2);
            je.writeResourceLocation(this.name);
        }
        else {
            je.writeByte(0);
        }
    }
    
    @Nullable
    public ResourceLocation getName() {
        return this.name;
    }
    
    @Nullable
    public SoundSource getSource() {
        return this.source;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleStopSoundEvent(this);
    }
}
