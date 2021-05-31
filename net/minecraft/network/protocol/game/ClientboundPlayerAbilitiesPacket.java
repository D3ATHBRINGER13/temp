package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerAbilitiesPacket implements Packet<ClientGamePacketListener> {
    private boolean invulnerable;
    private boolean isFlying;
    private boolean canFly;
    private boolean instabuild;
    private float flyingSpeed;
    private float walkingSpeed;
    
    public ClientboundPlayerAbilitiesPacket() {
    }
    
    public ClientboundPlayerAbilitiesPacket(final Abilities awd) {
        this.setInvulnerable(awd.invulnerable);
        this.setFlying(awd.flying);
        this.setCanFly(awd.mayfly);
        this.setInstabuild(awd.instabuild);
        this.setFlyingSpeed(awd.getFlyingSpeed());
        this.setWalkingSpeed(awd.getWalkingSpeed());
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        final byte byte3 = je.readByte();
        this.setInvulnerable((byte3 & 0x1) > 0);
        this.setFlying((byte3 & 0x2) > 0);
        this.setCanFly((byte3 & 0x4) > 0);
        this.setInstabuild((byte3 & 0x8) > 0);
        this.setFlyingSpeed(je.readFloat());
        this.setWalkingSpeed(je.readFloat());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        byte byte3 = 0;
        if (this.isInvulnerable()) {
            byte3 |= 0x1;
        }
        if (this.isFlying()) {
            byte3 |= 0x2;
        }
        if (this.canFly()) {
            byte3 |= 0x4;
        }
        if (this.canInstabuild()) {
            byte3 |= 0x8;
        }
        je.writeByte(byte3);
        je.writeFloat(this.flyingSpeed);
        je.writeFloat(this.walkingSpeed);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handlePlayerAbilities(this);
    }
    
    public boolean isInvulnerable() {
        return this.invulnerable;
    }
    
    public void setInvulnerable(final boolean boolean1) {
        this.invulnerable = boolean1;
    }
    
    public boolean isFlying() {
        return this.isFlying;
    }
    
    public void setFlying(final boolean boolean1) {
        this.isFlying = boolean1;
    }
    
    public boolean canFly() {
        return this.canFly;
    }
    
    public void setCanFly(final boolean boolean1) {
        this.canFly = boolean1;
    }
    
    public boolean canInstabuild() {
        return this.instabuild;
    }
    
    public void setInstabuild(final boolean boolean1) {
        this.instabuild = boolean1;
    }
    
    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }
    
    public void setFlyingSpeed(final float float1) {
        this.flyingSpeed = float1;
    }
    
    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }
    
    public void setWalkingSpeed(final float float1) {
        this.walkingSpeed = float1;
    }
}
