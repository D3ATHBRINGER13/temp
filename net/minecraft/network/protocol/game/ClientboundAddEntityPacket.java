package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;

public class ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private int xa;
    private int ya;
    private int za;
    private int xRot;
    private int yRot;
    private EntityType<?> type;
    private int data;
    
    public ClientboundAddEntityPacket() {
    }
    
    public ClientboundAddEntityPacket(final int integer1, final UUID uUID, final double double3, final double double4, final double double5, final float float6, final float float7, final EntityType<?> ais, final int integer9, final Vec3 csi) {
        this.id = integer1;
        this.uuid = uUID;
        this.x = double3;
        this.y = double4;
        this.z = double5;
        this.xRot = Mth.floor(float6 * 256.0f / 360.0f);
        this.yRot = Mth.floor(float7 * 256.0f / 360.0f);
        this.type = ais;
        this.data = integer9;
        this.xa = (int)(Mth.clamp(csi.x, -3.9, 3.9) * 8000.0);
        this.ya = (int)(Mth.clamp(csi.y, -3.9, 3.9) * 8000.0);
        this.za = (int)(Mth.clamp(csi.z, -3.9, 3.9) * 8000.0);
    }
    
    public ClientboundAddEntityPacket(final Entity aio) {
        this(aio, 0);
    }
    
    public ClientboundAddEntityPacket(final Entity aio, final int integer) {
        this(aio.getId(), aio.getUUID(), aio.x, aio.y, aio.z, aio.xRot, aio.yRot, aio.getType(), integer, aio.getDeltaMovement());
    }
    
    public ClientboundAddEntityPacket(final Entity aio, final EntityType<?> ais, final int integer, final BlockPos ew) {
        this(aio.getId(), aio.getUUID(), ew.getX(), ew.getY(), ew.getZ(), aio.xRot, aio.yRot, ais, integer, aio.getDeltaMovement());
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.uuid = je.readUUID();
        this.type = Registry.ENTITY_TYPE.byId(je.readVarInt());
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        this.xRot = je.readByte();
        this.yRot = je.readByte();
        this.data = je.readInt();
        this.xa = je.readShort();
        this.ya = je.readShort();
        this.za = je.readShort();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeUUID(this.uuid);
        je.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeByte(this.xRot);
        je.writeByte(this.yRot);
        je.writeInt(this.data);
        je.writeShort(this.xa);
        je.writeShort(this.ya);
        je.writeShort(this.za);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddEntity(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    public UUID getUUID() {
        return this.uuid;
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
    
    public double getXa() {
        return this.xa / 8000.0;
    }
    
    public double getYa() {
        return this.ya / 8000.0;
    }
    
    public double getZa() {
        return this.za / 8000.0;
    }
    
    public int getxRot() {
        return this.xRot;
    }
    
    public int getyRot() {
        return this.yRot;
    }
    
    public EntityType<?> getType() {
        return this.type;
    }
    
    public int getData() {
        return this.data;
    }
}
