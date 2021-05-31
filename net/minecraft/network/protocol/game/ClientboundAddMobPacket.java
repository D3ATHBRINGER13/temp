package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;

public class ClientboundAddMobPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private UUID uuid;
    private int type;
    private double x;
    private double y;
    private double z;
    private int xd;
    private int yd;
    private int zd;
    private byte yRot;
    private byte xRot;
    private byte yHeadRot;
    private SynchedEntityData entityData;
    private List<SynchedEntityData.DataItem<?>> unpack;
    
    public ClientboundAddMobPacket() {
    }
    
    public ClientboundAddMobPacket(final LivingEntity aix) {
        this.id = aix.getId();
        this.uuid = aix.getUUID();
        this.type = Registry.ENTITY_TYPE.getId(aix.getType());
        this.x = aix.x;
        this.y = aix.y;
        this.z = aix.z;
        this.yRot = (byte)(aix.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(aix.xRot * 256.0f / 360.0f);
        this.yHeadRot = (byte)(aix.yHeadRot * 256.0f / 360.0f);
        final double double3 = 3.9;
        final Vec3 csi5 = aix.getDeltaMovement();
        final double double4 = Mth.clamp(csi5.x, -3.9, 3.9);
        final double double5 = Mth.clamp(csi5.y, -3.9, 3.9);
        final double double6 = Mth.clamp(csi5.z, -3.9, 3.9);
        this.xd = (int)(double4 * 8000.0);
        this.yd = (int)(double5 * 8000.0);
        this.zd = (int)(double6 * 8000.0);
        this.entityData = aix.getEntityData();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.uuid = je.readUUID();
        this.type = je.readVarInt();
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        this.yRot = je.readByte();
        this.xRot = je.readByte();
        this.yHeadRot = je.readByte();
        this.xd = je.readShort();
        this.yd = je.readShort();
        this.zd = je.readShort();
        this.unpack = SynchedEntityData.unpack(je);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeUUID(this.uuid);
        je.writeVarInt(this.type);
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeByte(this.yRot);
        je.writeByte(this.xRot);
        je.writeByte(this.yHeadRot);
        je.writeShort(this.xd);
        je.writeShort(this.yd);
        je.writeShort(this.zd);
        this.entityData.packAll(je);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddMob(this);
    }
    
    @Nullable
    public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
        return this.unpack;
    }
    
    public int getId() {
        return this.id;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public int getType() {
        return this.type;
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
    
    public int getXd() {
        return this.xd;
    }
    
    public int getYd() {
        return this.yd;
    }
    
    public int getZd() {
        return this.zd;
    }
    
    public byte getyRot() {
        return this.yRot;
    }
    
    public byte getxRot() {
        return this.xRot;
    }
    
    public byte getyHeadRot() {
        return this.yHeadRot;
    }
}
