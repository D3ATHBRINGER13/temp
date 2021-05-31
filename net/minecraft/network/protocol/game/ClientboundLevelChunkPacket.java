package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.core.Registry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Map;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelChunkPacket implements Packet<ClientGamePacketListener> {
    private int x;
    private int z;
    private int availableSections;
    private CompoundTag heightmaps;
    private byte[] buffer;
    private List<CompoundTag> blockEntitiesTags;
    private boolean fullChunk;
    
    public ClientboundLevelChunkPacket() {
    }
    
    public ClientboundLevelChunkPacket(final LevelChunk bxt, final int integer) {
        final ChunkPos bhd4 = bxt.getPos();
        this.x = bhd4.x;
        this.z = bhd4.z;
        this.fullChunk = (integer == 65535);
        this.heightmaps = new CompoundTag();
        for (final Map.Entry<Heightmap.Types, Heightmap> entry6 : bxt.getHeightmaps()) {
            if (!((Heightmap.Types)entry6.getKey()).sendToClient()) {
                continue;
            }
            this.heightmaps.put(((Heightmap.Types)entry6.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)entry6.getValue()).getRawData()));
        }
        this.buffer = new byte[this.calculateChunkSize(bxt, integer)];
        this.availableSections = this.extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), bxt, integer);
        this.blockEntitiesTags = (List<CompoundTag>)Lists.newArrayList();
        for (final Map.Entry<BlockPos, BlockEntity> entry7 : bxt.getBlockEntities().entrySet()) {
            final BlockPos ew7 = (BlockPos)entry7.getKey();
            final BlockEntity btw8 = (BlockEntity)entry7.getValue();
            final int integer2 = ew7.getY() >> 4;
            if (!this.isFullChunk() && (integer & 1 << integer2) == 0x0) {
                continue;
            }
            final CompoundTag id10 = btw8.getUpdateTag();
            this.blockEntitiesTags.add(id10);
        }
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.x = je.readInt();
        this.z = je.readInt();
        this.fullChunk = je.readBoolean();
        this.availableSections = je.readVarInt();
        this.heightmaps = je.readNbt();
        final int integer3 = je.readVarInt();
        if (integer3 > 2097152) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        je.readBytes(this.buffer = new byte[integer3]);
        final int integer4 = je.readVarInt();
        this.blockEntitiesTags = (List<CompoundTag>)Lists.newArrayList();
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            this.blockEntitiesTags.add(je.readNbt());
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeInt(this.x);
        je.writeInt(this.z);
        je.writeBoolean(this.fullChunk);
        je.writeVarInt(this.availableSections);
        je.writeNbt(this.heightmaps);
        je.writeVarInt(this.buffer.length);
        je.writeBytes(this.buffer);
        je.writeVarInt(this.blockEntitiesTags.size());
        for (final CompoundTag id4 : this.blockEntitiesTags) {
            je.writeNbt(id4);
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleLevelChunk(this);
    }
    
    public FriendlyByteBuf getReadBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
    }
    
    private ByteBuf getWriteBuffer() {
        final ByteBuf byteBuf2 = Unpooled.wrappedBuffer(this.buffer);
        byteBuf2.writerIndex(0);
        return byteBuf2;
    }
    
    public int extractChunkData(final FriendlyByteBuf je, final LevelChunk bxt, final int integer) {
        int integer2 = 0;
        final LevelChunkSection[] arr6 = bxt.getSections();
        for (int integer3 = 0, integer4 = arr6.length; integer3 < integer4; ++integer3) {
            final LevelChunkSection bxu9 = arr6[integer3];
            if (bxu9 != LevelChunk.EMPTY_SECTION && (!this.isFullChunk() || !bxu9.isEmpty())) {
                if ((integer & 1 << integer3) != 0x0) {
                    integer2 |= 1 << integer3;
                    bxu9.write(je);
                }
            }
        }
        if (this.isFullChunk()) {
            final Biome[] arr7 = bxt.getBiomes();
            for (int integer4 = 0; integer4 < arr7.length; ++integer4) {
                je.writeInt(Registry.BIOME.getId(arr7[integer4]));
            }
        }
        return integer2;
    }
    
    protected int calculateChunkSize(final LevelChunk bxt, final int integer) {
        int integer2 = 0;
        final LevelChunkSection[] arr5 = bxt.getSections();
        for (int integer3 = 0, integer4 = arr5.length; integer3 < integer4; ++integer3) {
            final LevelChunkSection bxu8 = arr5[integer3];
            if (bxu8 != LevelChunk.EMPTY_SECTION && (!this.isFullChunk() || !bxu8.isEmpty())) {
                if ((integer & 1 << integer3) != 0x0) {
                    integer2 += bxu8.getSerializedSize();
                }
            }
        }
        if (this.isFullChunk()) {
            integer2 += bxt.getBiomes().length * 4;
        }
        return integer2;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public int getAvailableSections() {
        return this.availableSections;
    }
    
    public boolean isFullChunk() {
        return this.fullChunk;
    }
    
    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }
    
    public List<CompoundTag> getBlockEntitiesTags() {
        return this.blockEntitiesTags;
    }
}
