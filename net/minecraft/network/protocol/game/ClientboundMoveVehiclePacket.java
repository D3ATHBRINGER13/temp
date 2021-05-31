package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundMoveVehiclePacket implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private float yRot;
    private float xRot;
    
    public ClientboundMoveVehiclePacket() {
    }
    
    public ClientboundMoveVehiclePacket(final Entity aio) {
        this.x = aio.x;
        this.y = aio.y;
        this.z = aio.z;
        this.yRot = aio.yRot;
        this.xRot = aio.xRot;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        this.yRot = je.readFloat();
        this.xRot = je.readFloat();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeFloat(this.yRot);
        je.writeFloat(this.xRot);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleMoveVehicle(this);
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
    
    public float getYRot() {
        return this.yRot;
    }
    
    public float getXRot() {
        return this.xRot;
    }
}
