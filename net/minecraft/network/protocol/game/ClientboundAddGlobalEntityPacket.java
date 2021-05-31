package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ClientboundAddGlobalEntityPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private double x;
    private double y;
    private double z;
    private int type;
    
    public ClientboundAddGlobalEntityPacket() {
    }
    
    public ClientboundAddGlobalEntityPacket(final Entity aio) {
        this.id = aio.getId();
        this.x = aio.x;
        this.y = aio.y;
        this.z = aio.z;
        if (aio instanceof LightningBolt) {
            this.type = 1;
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.type = je.readByte();
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeByte(this.type);
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddGlobalEntity(this);
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
    
    public int getType() {
        return this.type;
    }
}
