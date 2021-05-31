package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetCommandMinecartPacket implements Packet<ServerGamePacketListener> {
    private int entity;
    private String command;
    private boolean trackOutput;
    
    public ServerboundSetCommandMinecartPacket() {
    }
    
    public ServerboundSetCommandMinecartPacket(final int integer, final String string, final boolean boolean3) {
        this.entity = integer;
        this.command = string;
        this.trackOutput = boolean3;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.entity = je.readVarInt();
        this.command = je.readUtf(32767);
        this.trackOutput = je.readBoolean();
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.entity);
        je.writeUtf(this.command);
        je.writeBoolean(this.trackOutput);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSetCommandMinecart(this);
    }
    
    @Nullable
    public BaseCommandBlock getCommandBlock(final Level bhr) {
        final Entity aio3 = bhr.getEntity(this.entity);
        if (aio3 instanceof MinecartCommandBlock) {
            return ((MinecartCommandBlock)aio3).getCommandBlock();
        }
        return null;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public boolean isTrackOutput() {
        return this.trackOutput;
    }
}
