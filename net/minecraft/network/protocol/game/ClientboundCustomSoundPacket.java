package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;

public class ClientboundCustomSoundPacket implements Packet<ClientGamePacketListener> {
    private ResourceLocation name;
    private SoundSource source;
    private int x;
    private int y;
    private int z;
    private float volume;
    private float pitch;
    
    public ClientboundCustomSoundPacket() {
        this.y = Integer.MAX_VALUE;
    }
    
    public ClientboundCustomSoundPacket(final ResourceLocation qv, final SoundSource yq, final Vec3 csi, final float float4, final float float5) {
        this.y = Integer.MAX_VALUE;
        this.name = qv;
        this.source = yq;
        this.x = (int)(csi.x * 8.0);
        this.y = (int)(csi.y * 8.0);
        this.z = (int)(csi.z * 8.0);
        this.volume = float4;
        this.pitch = float5;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.name = je.readResourceLocation();
        this.source = je.<SoundSource>readEnum(SoundSource.class);
        this.x = je.readInt();
        this.y = je.readInt();
        this.z = je.readInt();
        this.volume = je.readFloat();
        this.pitch = je.readFloat();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeResourceLocation(this.name);
        je.writeEnum(this.source);
        je.writeInt(this.x);
        je.writeInt(this.y);
        je.writeInt(this.z);
        je.writeFloat(this.volume);
        je.writeFloat(this.pitch);
    }
    
    public ResourceLocation getName() {
        return this.name;
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
        kf.handleCustomSoundEvent(this);
    }
}
