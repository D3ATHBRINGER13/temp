package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.Validate;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.protocol.Packet;

public class ClientboundSoundEntityPacket implements Packet<ClientGamePacketListener> {
    private SoundEvent sound;
    private SoundSource source;
    private int id;
    private float volume;
    private float pitch;
    
    public ClientboundSoundEntityPacket() {
    }
    
    public ClientboundSoundEntityPacket(final SoundEvent yo, final SoundSource yq, final Entity aio, final float float4, final float float5) {
        Validate.notNull(yo, "sound", new Object[0]);
        this.sound = yo;
        this.source = yq;
        this.id = aio.getId();
        this.volume = float4;
        this.pitch = float5;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.sound = Registry.SOUND_EVENT.byId(je.readVarInt());
        this.source = je.<SoundSource>readEnum(SoundSource.class);
        this.id = je.readVarInt();
        this.volume = je.readFloat();
        this.pitch = je.readFloat();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
        je.writeEnum(this.source);
        je.writeVarInt(this.id);
        je.writeFloat(this.volume);
        je.writeFloat(this.pitch);
    }
    
    public SoundEvent getSound() {
        return this.sound;
    }
    
    public SoundSource getSource() {
        return this.source;
    }
    
    public int getId() {
        return this.id;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSoundEntityEvent(this);
    }
}
