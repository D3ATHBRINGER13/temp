package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.world.entity.decoration.Motive;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;

public class ClientboundAddPaintingPacket implements Packet<ClientGamePacketListener> {
    private int id;
    private UUID uuid;
    private BlockPos pos;
    private Direction direction;
    private int motive;
    
    public ClientboundAddPaintingPacket() {
    }
    
    public ClientboundAddPaintingPacket(final Painting atq) {
        this.id = atq.getId();
        this.uuid = atq.getUUID();
        this.pos = atq.getPos();
        this.direction = atq.getDirection();
        this.motive = Registry.MOTIVE.getId(atq.motive);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.uuid = je.readUUID();
        this.motive = je.readVarInt();
        this.pos = je.readBlockPos();
        this.direction = Direction.from2DDataValue(je.readUnsignedByte());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeUUID(this.uuid);
        je.writeVarInt(this.motive);
        je.writeBlockPos(this.pos);
        je.writeByte(this.direction.get2DDataValue());
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleAddPainting(this);
    }
    
    public int getId() {
        return this.id;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public Motive getMotive() {
        return Registry.MOTIVE.byId(this.motive);
    }
}
