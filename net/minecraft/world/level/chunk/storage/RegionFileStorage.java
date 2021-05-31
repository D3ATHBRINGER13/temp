package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataOutputStream;
import java.io.DataOutput;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import java.io.IOException;
import net.minecraft.world.level.ChunkPos;
import java.io.File;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

public abstract class RegionFileStorage implements AutoCloseable {
    protected final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache;
    private final File folder;
    
    protected RegionFileStorage(final File file) {
        this.regionCache = (Long2ObjectLinkedOpenHashMap<RegionFile>)new Long2ObjectLinkedOpenHashMap();
        this.folder = file;
    }
    
    private RegionFile getRegionFile(final ChunkPos bhd) throws IOException {
        final long long3 = ChunkPos.asLong(bhd.getRegionX(), bhd.getRegionZ());
        final RegionFile byi5 = (RegionFile)this.regionCache.getAndMoveToFirst(long3);
        if (byi5 != null) {
            return byi5;
        }
        if (this.regionCache.size() >= 256) {
            ((RegionFile)this.regionCache.removeLast()).close();
        }
        if (!this.folder.exists()) {
            this.folder.mkdirs();
        }
        final File file6 = new File(this.folder, new StringBuilder().append("r.").append(bhd.getRegionX()).append(".").append(bhd.getRegionZ()).append(".mca").toString());
        final RegionFile byi6 = new RegionFile(file6);
        this.regionCache.putAndMoveToFirst(long3, byi6);
        return byi6;
    }
    
    @Nullable
    public CompoundTag read(final ChunkPos bhd) throws IOException {
        final RegionFile byi3 = this.getRegionFile(bhd);
        try (final DataInputStream dataInputStream4 = byi3.getChunkDataInputStream(bhd)) {
            if (dataInputStream4 == null) {
                return null;
            }
            return NbtIo.read(dataInputStream4);
        }
    }
    
    protected void write(final ChunkPos bhd, final CompoundTag id) throws IOException {
        final RegionFile byi4 = this.getRegionFile(bhd);
        try (final DataOutputStream dataOutputStream5 = byi4.getChunkDataOutputStream(bhd)) {
            NbtIo.write(id, (DataOutput)dataOutputStream5);
        }
    }
    
    public void close() throws IOException {
        for (final RegionFile byi3 : this.regionCache.values()) {
            byi3.close();
        }
    }
}
