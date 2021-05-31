package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.Mth;
import net.minecraft.network.FriendlyByteBuf;
import com.google.common.collect.Lists;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundExplodePacket implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private float power;
    private List<BlockPos> toBlow;
    private float knockbackX;
    private float knockbackY;
    private float knockbackZ;
    
    public ClientboundExplodePacket() {
    }
    
    public ClientboundExplodePacket(final double double1, final double double2, final double double3, final float float4, final List<BlockPos> list, final Vec3 csi) {
        this.x = double1;
        this.y = double2;
        this.z = double3;
        this.power = float4;
        this.toBlow = (List<BlockPos>)Lists.newArrayList((Iterable)list);
        if (csi != null) {
            this.knockbackX = (float)csi.x;
            this.knockbackY = (float)csi.y;
            this.knockbackZ = (float)csi.z;
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.x = je.readFloat();
        this.y = je.readFloat();
        this.z = je.readFloat();
        this.power = je.readFloat();
        final int integer3 = je.readInt();
        this.toBlow = (List<BlockPos>)Lists.newArrayListWithCapacity(integer3);
        final int integer4 = Mth.floor(this.x);
        final int integer5 = Mth.floor(this.y);
        final int integer6 = Mth.floor(this.z);
        for (int integer7 = 0; integer7 < integer3; ++integer7) {
            final int integer8 = je.readByte() + integer4;
            final int integer9 = je.readByte() + integer5;
            final int integer10 = je.readByte() + integer6;
            this.toBlow.add(new BlockPos(integer8, integer9, integer10));
        }
        this.knockbackX = je.readFloat();
        this.knockbackY = je.readFloat();
        this.knockbackZ = je.readFloat();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeFloat((float)this.x);
        je.writeFloat((float)this.y);
        je.writeFloat((float)this.z);
        je.writeFloat(this.power);
        je.writeInt(this.toBlow.size());
        final int integer3 = Mth.floor(this.x);
        final int integer4 = Mth.floor(this.y);
        final int integer5 = Mth.floor(this.z);
        for (final BlockPos ew7 : this.toBlow) {
            final int integer6 = ew7.getX() - integer3;
            final int integer7 = ew7.getY() - integer4;
            final int integer8 = ew7.getZ() - integer5;
            je.writeByte(integer6);
            je.writeByte(integer7);
            je.writeByte(integer8);
        }
        je.writeFloat(this.knockbackX);
        je.writeFloat(this.knockbackY);
        je.writeFloat(this.knockbackZ);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleExplosion(this);
    }
    
    public float getKnockbackX() {
        return this.knockbackX;
    }
    
    public float getKnockbackY() {
        return this.knockbackY;
    }
    
    public float getKnockbackZ() {
        return this.knockbackZ;
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
    
    public float getPower() {
        return this.power;
    }
    
    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }
}
