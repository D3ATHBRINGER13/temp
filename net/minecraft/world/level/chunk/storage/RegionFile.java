package net.minecraft.world.level.chunk.storage;

import java.io.ByteArrayOutputStream;
import net.minecraft.Util;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.DataOutputStream;
import javax.annotation.Nullable;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import net.minecraft.world.level.ChunkPos;
import java.io.IOException;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.io.RandomAccessFile;

public class RegionFile implements AutoCloseable {
    private static final byte[] EMPTY_SECTOR;
    private final RandomAccessFile file;
    private final int[] offsets;
    private final int[] chunkTimestamps;
    private final List<Boolean> sectorFree;
    
    public RegionFile(final File file) throws IOException {
        this.offsets = new int[1024];
        this.chunkTimestamps = new int[1024];
        this.file = new RandomAccessFile(file, "rw");
        if (this.file.length() < 4096L) {
            this.file.write(RegionFile.EMPTY_SECTOR);
            this.file.write(RegionFile.EMPTY_SECTOR);
        }
        if ((this.file.length() & 0xFFFL) != 0x0L) {
            for (int integer3 = 0; integer3 < (this.file.length() & 0xFFFL); ++integer3) {
                this.file.write(0);
            }
        }
        int integer3 = (int)this.file.length() / 4096;
        this.sectorFree = (List<Boolean>)Lists.newArrayListWithCapacity(integer3);
        for (int integer4 = 0; integer4 < integer3; ++integer4) {
            this.sectorFree.add(true);
        }
        this.sectorFree.set(0, false);
        this.sectorFree.set(1, false);
        this.file.seek(0L);
        for (int integer4 = 0; integer4 < 1024; ++integer4) {
            final int integer5 = this.file.readInt();
            this.offsets[integer4] = integer5;
            if (integer5 != 0 && (integer5 >> 8) + (integer5 & 0xFF) <= this.sectorFree.size()) {
                for (int integer6 = 0; integer6 < (integer5 & 0xFF); ++integer6) {
                    this.sectorFree.set((integer5 >> 8) + integer6, false);
                }
            }
        }
        for (int integer4 = 0; integer4 < 1024; ++integer4) {
            final int integer5 = this.file.readInt();
            this.chunkTimestamps[integer4] = integer5;
        }
    }
    
    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(final ChunkPos bhd) throws IOException {
        final int integer3 = this.getOffset(bhd);
        if (integer3 == 0) {
            return null;
        }
        final int integer4 = integer3 >> 8;
        final int integer5 = integer3 & 0xFF;
        if (integer4 + integer5 > this.sectorFree.size()) {
            return null;
        }
        this.file.seek((long)(integer4 * 4096));
        final int integer6 = this.file.readInt();
        if (integer6 > 4096 * integer5) {
            return null;
        }
        if (integer6 <= 0) {
            return null;
        }
        final byte byte7 = this.file.readByte();
        if (byte7 == 1) {
            final byte[] arr8 = new byte[integer6 - 1];
            this.file.read(arr8);
            return new DataInputStream((InputStream)new BufferedInputStream((InputStream)new GZIPInputStream((InputStream)new ByteArrayInputStream(arr8))));
        }
        if (byte7 == 2) {
            final byte[] arr8 = new byte[integer6 - 1];
            this.file.read(arr8);
            return new DataInputStream((InputStream)new BufferedInputStream((InputStream)new InflaterInputStream((InputStream)new ByteArrayInputStream(arr8))));
        }
        return null;
    }
    
    public boolean doesChunkExist(final ChunkPos bhd) {
        final int integer3 = this.getOffset(bhd);
        if (integer3 == 0) {
            return false;
        }
        final int integer4 = integer3 >> 8;
        final int integer5 = integer3 & 0xFF;
        if (integer4 + integer5 > this.sectorFree.size()) {
            return false;
        }
        try {
            this.file.seek((long)(integer4 * 4096));
            final int integer6 = this.file.readInt();
            if (integer6 > 4096 * integer5) {
                return false;
            }
            if (integer6 <= 0) {
                return false;
            }
        }
        catch (IOException iOException6) {
            return false;
        }
        return true;
    }
    
    public DataOutputStream getChunkDataOutputStream(final ChunkPos bhd) {
        return new DataOutputStream((OutputStream)new BufferedOutputStream((OutputStream)new DeflaterOutputStream((OutputStream)new ChunkBuffer(bhd))));
    }
    
    protected synchronized void write(final ChunkPos bhd, final byte[] arr, final int integer) throws IOException {
        final int integer2 = this.getOffset(bhd);
        int integer3 = integer2 >> 8;
        final int integer4 = integer2 & 0xFF;
        final int integer5 = (integer + 5) / 4096 + 1;
        if (integer5 >= 256) {
            throw new RuntimeException(String.format("Too big to save, %d > 1048576", new Object[] { integer }));
        }
        if (integer3 != 0 && integer4 == integer5) {
            this.write(integer3, arr, integer);
        }
        else {
            for (int integer6 = 0; integer6 < integer4; ++integer6) {
                this.sectorFree.set(integer3 + integer6, true);
            }
            int integer6 = this.sectorFree.indexOf(true);
            int integer7 = 0;
            if (integer6 != -1) {
                for (int integer8 = integer6; integer8 < this.sectorFree.size(); ++integer8) {
                    if (integer7 != 0) {
                        if (this.sectorFree.get(integer8)) {
                            ++integer7;
                        }
                        else {
                            integer7 = 0;
                        }
                    }
                    else if (this.sectorFree.get(integer8)) {
                        integer6 = integer8;
                        integer7 = 1;
                    }
                    if (integer7 >= integer5) {
                        break;
                    }
                }
            }
            if (integer7 >= integer5) {
                integer3 = integer6;
                this.setOffset(bhd, integer3 << 8 | integer5);
                for (int integer8 = 0; integer8 < integer5; ++integer8) {
                    this.sectorFree.set(integer3 + integer8, false);
                }
                this.write(integer3, arr, integer);
            }
            else {
                this.file.seek(this.file.length());
                integer3 = this.sectorFree.size();
                for (int integer8 = 0; integer8 < integer5; ++integer8) {
                    this.file.write(RegionFile.EMPTY_SECTOR);
                    this.sectorFree.add(false);
                }
                this.write(integer3, arr, integer);
                this.setOffset(bhd, integer3 << 8 | integer5);
            }
        }
        this.setTimestamp(bhd, (int)(Util.getEpochMillis() / 1000L));
    }
    
    private void write(final int integer1, final byte[] arr, final int integer3) throws IOException {
        this.file.seek((long)(integer1 * 4096));
        this.file.writeInt(integer3 + 1);
        this.file.writeByte(2);
        this.file.write(arr, 0, integer3);
    }
    
    private int getOffset(final ChunkPos bhd) {
        return this.offsets[this.getOffsetIndex(bhd)];
    }
    
    public boolean hasChunk(final ChunkPos bhd) {
        return this.getOffset(bhd) != 0;
    }
    
    private void setOffset(final ChunkPos bhd, final int integer) throws IOException {
        final int integer2 = this.getOffsetIndex(bhd);
        this.offsets[integer2] = integer;
        this.file.seek((long)(integer2 * 4));
        this.file.writeInt(integer);
    }
    
    private int getOffsetIndex(final ChunkPos bhd) {
        return bhd.getRegionLocalX() + bhd.getRegionLocalZ() * 32;
    }
    
    private void setTimestamp(final ChunkPos bhd, final int integer) throws IOException {
        final int integer2 = this.getOffsetIndex(bhd);
        this.chunkTimestamps[integer2] = integer;
        this.file.seek((long)(4096 + integer2 * 4));
        this.file.writeInt(integer);
    }
    
    public void close() throws IOException {
        this.file.close();
    }
    
    static {
        EMPTY_SECTOR = new byte[4096];
    }
    
    class ChunkBuffer extends ByteArrayOutputStream {
        private final ChunkPos pos;
        
        public ChunkBuffer(final ChunkPos bhd) {
            super(8096);
            this.pos = bhd;
        }
        
        public void close() throws IOException {
            RegionFile.this.write(this.pos, this.buf, this.count);
        }
    }
}
