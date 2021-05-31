package net.minecraft.network.protocol.game;

import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.network.PacketListener;
import java.io.IOException;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.protocol.Packet;

public class ClientboundChunkBlocksUpdatePacket implements Packet<ClientGamePacketListener> {
    private ChunkPos chunkPos;
    private BlockUpdate[] updates;
    
    public ClientboundChunkBlocksUpdatePacket() {
    }
    
    public ClientboundChunkBlocksUpdatePacket(final int integer, final short[] arr, final LevelChunk bxt) {
        this.chunkPos = bxt.getPos();
        this.updates = new BlockUpdate[integer];
        for (int integer2 = 0; integer2 < this.updates.length; ++integer2) {
            this.updates[integer2] = new BlockUpdate(arr[integer2], bxt);
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.chunkPos = new ChunkPos(je.readInt(), je.readInt());
        this.updates = new BlockUpdate[je.readVarInt()];
        for (int integer3 = 0; integer3 < this.updates.length; ++integer3) {
            this.updates[integer3] = new BlockUpdate(je.readShort(), Block.BLOCK_STATE_REGISTRY.byId(je.readVarInt()));
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.chunkPos.x);
        je.writeInt(this.chunkPos.z);
        je.writeVarInt(this.updates.length);
        for (final BlockUpdate a6 : this.updates) {
            je.writeShort(a6.getOffset());
            je.writeVarInt(Block.getId(a6.getBlock()));
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleChunkBlocksUpdate(this);
    }
    
    public BlockUpdate[] getUpdates() {
        return this.updates;
    }
    
    public class BlockUpdate {
        private final short offset;
        private final BlockState block;
        
        public BlockUpdate(final short short2, final BlockState bvt) {
            this.offset = short2;
            this.block = bvt;
        }
        
        public BlockUpdate(final short short2, final LevelChunk bxt) {
            this.offset = short2;
            this.block = bxt.getBlockState(this.getPos());
        }
        
        public BlockPos getPos() {
            return new BlockPos(ClientboundChunkBlocksUpdatePacket.this.chunkPos.getBlockAt(this.offset >> 12 & 0xF, this.offset & 0xFF, this.offset >> 8 & 0xF));
        }
        
        public short getOffset() {
            return this.offset;
        }
        
        public BlockState getBlock() {
            return this.block;
        }
    }
}
