package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerInputPacket implements Packet<ServerGamePacketListener> {
    private float xxa;
    private float zza;
    private boolean isJumping;
    private boolean isSneaking;
    
    public ServerboundPlayerInputPacket() {
    }
    
    public ServerboundPlayerInputPacket(final float float1, final float float2, final boolean boolean3, final boolean boolean4) {
        this.xxa = float1;
        this.zza = float2;
        this.isJumping = boolean3;
        this.isSneaking = boolean4;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.xxa = je.readFloat();
        this.zza = je.readFloat();
        final byte byte3 = je.readByte();
        this.isJumping = ((byte3 & 0x1) > 0);
        this.isSneaking = ((byte3 & 0x2) > 0);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeFloat(this.xxa);
        je.writeFloat(this.zza);
        byte byte3 = 0;
        if (this.isJumping) {
            byte3 |= 0x1;
        }
        if (this.isSneaking) {
            byte3 |= 0x2;
        }
        je.writeByte(byte3);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handlePlayerInput(this);
    }
    
    public float getXxa() {
        return this.xxa;
    }
    
    public float getZza() {
        return this.zza;
    }
    
    public boolean isJumping() {
        return this.isJumping;
    }
    
    public boolean isSneaking() {
        return this.isSneaking;
    }
}
