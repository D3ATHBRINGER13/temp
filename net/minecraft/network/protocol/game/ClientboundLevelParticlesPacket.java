package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelParticlesPacket implements Packet<ClientGamePacketListener> {
    private float x;
    private float y;
    private float z;
    private float xDist;
    private float yDist;
    private float zDist;
    private float maxSpeed;
    private int count;
    private boolean overrideLimiter;
    private ParticleOptions particle;
    
    public ClientboundLevelParticlesPacket() {
    }
    
    public <T extends ParticleOptions> ClientboundLevelParticlesPacket(final T gf, final boolean boolean2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8, final float float9, final int integer) {
        this.particle = gf;
        this.overrideLimiter = boolean2;
        this.x = float3;
        this.y = float4;
        this.z = float5;
        this.xDist = float6;
        this.yDist = float7;
        this.zDist = float8;
        this.maxSpeed = float9;
        this.count = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        ParticleType<?> gg3 = Registry.PARTICLE_TYPE.byId(je.readInt());
        if (gg3 == null) {
            gg3 = ParticleTypes.BARRIER;
        }
        this.overrideLimiter = je.readBoolean();
        this.x = je.readFloat();
        this.y = je.readFloat();
        this.z = je.readFloat();
        this.xDist = je.readFloat();
        this.yDist = je.readFloat();
        this.zDist = je.readFloat();
        this.maxSpeed = je.readFloat();
        this.count = je.readInt();
        this.particle = this.<ParticleOptions>readParticle(je, gg3);
    }
    
    private <T extends ParticleOptions> T readParticle(final FriendlyByteBuf je, final ParticleType<T> gg) {
        return gg.getDeserializer().fromNetwork(gg, je);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
        je.writeBoolean(this.overrideLimiter);
        je.writeFloat(this.x);
        je.writeFloat(this.y);
        je.writeFloat(this.z);
        je.writeFloat(this.xDist);
        je.writeFloat(this.yDist);
        je.writeFloat(this.zDist);
        je.writeFloat(this.maxSpeed);
        je.writeInt(this.count);
        this.particle.writeToNetwork(je);
    }
    
    public boolean isOverrideLimiter() {
        return this.overrideLimiter;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public float getXDist() {
        return this.xDist;
    }
    
    public float getYDist() {
        return this.yDist;
    }
    
    public float getZDist() {
        return this.zDist;
    }
    
    public float getMaxSpeed() {
        return this.maxSpeed;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public ParticleOptions getParticle() {
        return this.particle;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleParticleEvent(this);
    }
}
