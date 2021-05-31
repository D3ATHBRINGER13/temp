package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;

public class ClientboundAddPlayerPacket implements Packet<ClientGamePacketListener> {
    private int entityId;
    private UUID playerId;
    private double x;
    private double y;
    private double z;
    private byte yRot;
    private byte xRot;
    private SynchedEntityData entityData;
    private List<SynchedEntityData.DataItem<?>> unpack;
    
    public ClientboundAddPlayerPacket() {
    }
    
    public ClientboundAddPlayerPacket(final Player awg) {
        this.entityId = awg.getId();
        this.playerId = awg.getGameProfile().getId();
        this.x = awg.x;
        this.y = awg.y;
        this.z = awg.z;
        this.yRot = (byte)(awg.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(awg.xRot * 256.0f / 360.0f);
        this.entityData = awg.getEntityData();
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entityId = je.readVarInt();
        this.playerId = je.readUUID();
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        this.yRot = je.readByte();
        this.xRot = je.readByte();
        this.unpack = SynchedEntityData.unpack(je);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entityId);
        je.writeUUID(this.playerId);
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeByte(this.yRot);
        je.writeByte(this.xRot);
        this.entityData.packAll(je);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddPlayer(this);
    }
    
    @Nullable
    public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
        return this.unpack;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public UUID getPlayerId() {
        return this.playerId;
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
}
