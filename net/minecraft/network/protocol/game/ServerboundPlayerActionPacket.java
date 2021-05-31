package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerActionPacket implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private Direction direction;
    private Action action;
    
    public ServerboundPlayerActionPacket() {
    }
    
    public ServerboundPlayerActionPacket(final Action a, final BlockPos ew, final Direction fb) {
        this.action = a;
        this.pos = ew.immutable();
        this.direction = fb;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.action = je.<Action>readEnum(Action.class);
        this.pos = je.readBlockPos();
        this.direction = Direction.from3DDataValue(je.readUnsignedByte());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeEnum(this.action);
        je.writeBlockPos(this.pos);
        je.writeByte(this.direction.get3DDataValue());
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handlePlayerAction(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public enum Action {
        START_DESTROY_BLOCK, 
        ABORT_DESTROY_BLOCK, 
        STOP_DESTROY_BLOCK, 
        DROP_ALL_ITEMS, 
        DROP_ITEM, 
        RELEASE_USE_ITEM, 
        SWAP_HELD_ITEMS;
    }
}
