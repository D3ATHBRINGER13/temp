package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockUpdatePacket implements Packet<ClientGamePacketListener> {
    private BlockPos pos;
    private BlockState blockState;
    
    public ClientboundBlockUpdatePacket() {
    }
    
    public ClientboundBlockUpdatePacket(final BlockGetter bhb, final BlockPos ew) {
        this.pos = ew;
        this.blockState = bhb.getBlockState(ew);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.pos = je.readBlockPos();
        this.blockState = Block.BLOCK_STATE_REGISTRY.byId(je.readVarInt());
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeBlockPos(this.pos);
        je.writeVarInt(Block.getId(this.blockState));
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleBlockUpdate(this);
    }
    
    public BlockState getBlockState() {
        return this.blockState;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
}
