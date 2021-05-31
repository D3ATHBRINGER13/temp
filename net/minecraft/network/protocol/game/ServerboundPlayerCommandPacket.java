package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerCommandPacket implements Packet<ServerGamePacketListener> {
    private int id;
    private Action action;
    private int data;
    
    public ServerboundPlayerCommandPacket() {
    }
    
    public ServerboundPlayerCommandPacket(final Entity aio, final Action a) {
        this(aio, a, 0);
    }
    
    public ServerboundPlayerCommandPacket(final Entity aio, final Action a, final int integer) {
        this.id = aio.getId();
        this.action = a;
        this.data = integer;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.id = je.readVarInt();
        this.action = je.<Action>readEnum(Action.class);
        this.data = je.readVarInt();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.id);
        je.writeEnum(this.action);
        je.writeVarInt(this.data);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handlePlayerCommand(this);
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public int getData() {
        return this.data;
    }
    
    public enum Action {
        START_SNEAKING, 
        STOP_SNEAKING, 
        STOP_SLEEPING, 
        START_SPRINTING, 
        STOP_SPRINTING, 
        START_RIDING_JUMP, 
        STOP_RIDING_JUMP, 
        OPEN_INVENTORY, 
        START_FALL_FLYING;
    }
}
