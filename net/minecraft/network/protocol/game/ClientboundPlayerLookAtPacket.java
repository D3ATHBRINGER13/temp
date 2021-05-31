package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerLookAtPacket implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private int entity;
    private EntityAnchorArgument.Anchor fromAnchor;
    private EntityAnchorArgument.Anchor toAnchor;
    private boolean atEntity;
    
    public ClientboundPlayerLookAtPacket() {
    }
    
    public ClientboundPlayerLookAtPacket(final EntityAnchorArgument.Anchor a, final double double2, final double double3, final double double4) {
        this.fromAnchor = a;
        this.x = double2;
        this.y = double3;
        this.z = double4;
    }
    
    public ClientboundPlayerLookAtPacket(final EntityAnchorArgument.Anchor a1, final Entity aio, final EntityAnchorArgument.Anchor a3) {
        this.fromAnchor = a1;
        this.entity = aio.getId();
        this.toAnchor = a3;
        final Vec3 csi5 = a3.apply(aio);
        this.x = csi5.x;
        this.y = csi5.y;
        this.z = csi5.z;
        this.atEntity = true;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.fromAnchor = je.<EntityAnchorArgument.Anchor>readEnum(EntityAnchorArgument.Anchor.class);
        this.x = je.readDouble();
        this.y = je.readDouble();
        this.z = je.readDouble();
        if (je.readBoolean()) {
            this.atEntity = true;
            this.entity = je.readVarInt();
            this.toAnchor = je.<EntityAnchorArgument.Anchor>readEnum(EntityAnchorArgument.Anchor.class);
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.fromAnchor);
        je.writeDouble(this.x);
        je.writeDouble(this.y);
        je.writeDouble(this.z);
        je.writeBoolean(this.atEntity);
        if (this.atEntity) {
            je.writeVarInt(this.entity);
            je.writeEnum(this.toAnchor);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleLookAt(this);
    }
    
    public EntityAnchorArgument.Anchor getFromAnchor() {
        return this.fromAnchor;
    }
    
    @Nullable
    public Vec3 getPosition(final Level bhr) {
        if (!this.atEntity) {
            return new Vec3(this.x, this.y, this.z);
        }
        final Entity aio3 = bhr.getEntity(this.entity);
        if (aio3 == null) {
            return new Vec3(this.x, this.y, this.z);
        }
        return this.toAnchor.apply(aio3);
    }
}
