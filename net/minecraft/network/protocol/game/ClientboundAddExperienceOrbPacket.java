package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.network.protocol.Packet;

public class ClientboundAddExperienceOrbPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private double x;
    private double y;
    private double z;
    private int value;
    
    public ClientboundAddExperienceOrbPacket() {
    }
    
    public ClientboundAddExperienceOrbPacket(final ExperienceOrb aiu) {
        this.id = aiu.getId();
        this.x = aiu.x;
        this.y = aiu.y;
        this.z = aiu.z;
        this.value = aiu.getValue();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        this.value = je.readShort();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeShort(this.value);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddExperienceOrb(this);
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
    
    public int getValue() {
        return this.value;
    }
}
