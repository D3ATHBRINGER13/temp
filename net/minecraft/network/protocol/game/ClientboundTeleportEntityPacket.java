package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundTeleportEntityPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private double x;
    private double y;
    private double z;
    private byte yRot;
    private byte xRot;
    private boolean onGround;
    
    public ClientboundTeleportEntityPacket() {
    }
    
    public ClientboundTeleportEntityPacket(final Entity aio) {
        this.id = aio.getId();
        this.x = aio.x;
        this.y = aio.y;
        this.z = aio.z;
        this.yRot = (byte)(aio.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(aio.xRot * 256.0f / 360.0f);
        this.onGround = aio.onGround;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        this.yRot = je.readByte();
        this.xRot = je.readByte();
        this.onGround = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeByte(this.yRot);
        je.writeByte(this.xRot);
        je.writeBoolean(this.onGround);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleTeleportEntity(this);
    }
    
    public int getId() {
        return this.id;
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
    
    public byte getyRot() {
        return this.yRot;
    }
    
    public byte getxRot() {
        return this.xRot;
    }
    
    public boolean isOnGround() {
        return this.onGround;
    }
}
