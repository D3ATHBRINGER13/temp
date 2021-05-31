package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.protocol.Packet;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
    private int entityId;
    private byte effectId;
    private byte effectAmplifier;
    private int effectDurationTicks;
    private byte flags;
    
    public ClientboundUpdateMobEffectPacket() {
    }
    
    public ClientboundUpdateMobEffectPacket(final int integer, final MobEffectInstance aii) {
        this.entityId = integer;
        this.effectId = (byte)(MobEffect.getId(aii.getEffect()) & 0xFF);
        this.effectAmplifier = (byte)(aii.getAmplifier() & 0xFF);
        if (aii.getDuration() > 32767) {
            this.effectDurationTicks = 32767;
        }
        else {
            this.effectDurationTicks = aii.getDuration();
        }
        this.flags = 0;
        if (aii.isAmbient()) {
            this.flags |= 0x1;
        }
        if (aii.isVisible()) {
            this.flags |= 0x2;
        }
        if (aii.showIcon()) {
            this.flags |= 0x4;
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
        this.effectId = je.readByte();
        this.effectAmplifier = je.readByte();
        this.effectDurationTicks = je.readVarInt();
        this.flags = je.readByte();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
        je.writeByte(this.effectId);
        je.writeByte(this.effectAmplifier);
        je.writeVarInt(this.effectDurationTicks);
        je.writeByte(this.flags);
    }
    
    public boolean isSuperLongDuration() {
        return this.effectDurationTicks == 32767;
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleUpdateMobEffect(this);
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public byte getEffectId() {
        return this.effectId;
    }
    
    public byte getEffectAmplifier() {
        return this.effectAmplifier;
    }
    
    public int getEffectDurationTicks() {
        return this.effectDurationTicks;
    }
    
    public boolean isEffectVisible() {
        return (this.flags & 0x2) == 0x2;
    }
    
    public boolean isEffectAmbient() {
        return (this.flags & 0x1) == 0x1;
    }
    
    public boolean effectShowsIcon() {
        return (this.flags & 0x4) == 0x4;
    }
}
