package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.Validate;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.protocol.Packet;

public class ClientboundSoundPacket implements Packet<ClientGamePacketListener> {
    private SoundEvent sound;
    private SoundSource source;
    private int x;
    private int y;
    private int z;
    private float volume;
    private float pitch;
    
    public ClientboundSoundPacket() {
    }
    
    public ClientboundSoundPacket(final SoundEvent yo, final SoundSource yq, final double double3, final double double4, final double double5, final float float6, final float float7) {
        Validate.notNull(yo, "sound", new Object[0]);
        this.sound = yo;
        this.source = yq;
        this.x = (int)(double3 * 8.0);
        this.y = (int)(double4 * 8.0);
        this.z = (int)(double5 * 8.0);
        this.volume = float6;
        this.pitch = float7;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.sound = Registry.SOUND_EVENT.byId(je.readVarInt());
        this.source = je.<SoundSource>readEnum(SoundSource.class);
        this.x = je.readInt();
        this.y = je.readInt();
        this.z = je.readInt();
        this.volume = je.readFloat();
        this.pitch = je.readFloat();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
        je.writeEnum(this.source);
        je.writeInt(this.x);
        je.writeInt(this.y);
        je.writeInt(this.z);
        je.writeFloat(this.volume);
        je.writeFloat(this.pitch);
    }
    
    public SoundEvent getSound() {
        return this.sound;
    }
    
    public SoundSource getSource() {
        return this.source;
    }
    
    public double getX() {
        return this.x / 8.0f;
    }
    
    public double getY() {
        return this.y / 8.0f;
    }
    
    public double getZ() {
        return this.z / 8.0f;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleSoundEvent(this);
    }
}
