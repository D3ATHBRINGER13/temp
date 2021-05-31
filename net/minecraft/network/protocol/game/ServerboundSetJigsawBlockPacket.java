package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetJigsawBlockPacket implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private ResourceLocation attachementType;
    private ResourceLocation targetPool;
    private String finalState;
    
    public ServerboundSetJigsawBlockPacket() {
    }
    
    public ServerboundSetJigsawBlockPacket(final BlockPos ew, final ResourceLocation qv2, final ResourceLocation qv3, final String string) {
        this.pos = ew;
        this.attachementType = qv2;
        this.targetPool = qv3;
        this.finalState = string;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
        this.attachementType = je.readResourceLocation();
        this.targetPool = je.readResourceLocation();
        this.finalState = je.readUtf(32767);
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
        je.writeResourceLocation(this.attachementType);
        je.writeResourceLocation(this.targetPool);
        je.writeUtf(this.finalState);
    }
    
    public void handle(final ServerGamePacketListener nu) {
        nu.handleSetJigsawBlock(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public ResourceLocation getTargetPool() {
        return this.targetPool;
    }
    
    public ResourceLocation getAttachementType() {
        return this.attachementType;
    }
    
    public String getFinalState() {
        return this.finalState;
    }
}
