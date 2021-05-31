package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicReferenceArray;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import java.util.function.BooleanSupplier;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.ChunkStatus;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.chunk.ChunkSource;

public class ClientChunkCache extends ChunkSource {
    private static final Logger LOGGER;
    private final LevelChunk emptyChunk;
    private final LevelLightEngine lightEngine;
    private volatile Storage storage;
    private final MultiPlayerLevel level;
    
    public ClientChunkCache(final MultiPlayerLevel dkf, final int integer) {
        this.level = dkf;
        this.emptyChunk = new EmptyLevelChunk(dkf, new ChunkPos(0, 0));
        this.lightEngine = new LevelLightEngine(this, true, dkf.getDimension().isHasSkyLight());
        this.storage = new Storage(calculateStorageRange(integer));
    }
    
    @Override
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }
    
    private static boolean isValidChunk(@Nullable final LevelChunk bxt, final int integer2, final int integer3) {
        if (bxt == null) {
            return false;
        }
        final ChunkPos bhd4 = bxt.getPos();
        return bhd4.x == integer2 && bhd4.z == integer3;
    }
    
    public void drop(final int integer1, final int integer2) {
        if (!this.storage.inRange(integer1, integer2)) {
            return;
        }
        final int integer3 = this.storage.getIndex(integer1, integer2);
        final LevelChunk bxt5 = this.storage.getChunk(integer3);
        if (isValidChunk(bxt5, integer1, integer2)) {
            this.storage.replace(integer3, bxt5, null);
        }
    }
    
    @Nullable
    @Override
    public LevelChunk getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        if (this.storage.inRange(integer1, integer2)) {
            final LevelChunk bxt6 = this.storage.getChunk(this.storage.getIndex(integer1, integer2));
            if (isValidChunk(bxt6, integer1, integer2)) {
                return bxt6;
            }
        }
        if (boolean4) {
            return this.emptyChunk;
        }
        return null;
    }
    
    public BlockGetter getLevel() {
        return this.level;
    }
    
    @Nullable
    public LevelChunk replaceWithPacketData(final Level bhr, final int integer2, final int integer3, final FriendlyByteBuf je, final CompoundTag id, final int integer6, final boolean boolean7) {
        if (!this.storage.inRange(integer2, integer3)) {
            ClientChunkCache.LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", integer2, integer3);
            return null;
        }
        final int integer7 = this.storage.getIndex(integer2, integer3);
        LevelChunk bxt10 = (LevelChunk)this.storage.chunks.get(integer7);
        if (!isValidChunk(bxt10, integer2, integer3)) {
            if (!boolean7) {
                ClientChunkCache.LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", integer2, integer3);
                return null;
            }
            bxt10 = new LevelChunk(bhr, new ChunkPos(integer2, integer3), new Biome[256]);
            bxt10.replaceWithPacketData(je, id, integer6, boolean7);
            this.storage.replace(integer7, bxt10);
        }
        else {
            bxt10.replaceWithPacketData(je, id, integer6, boolean7);
        }
        final LevelChunkSection[] arr11 = bxt10.getSections();
        final LevelLightEngine clb12 = this.getLightEngine();
        clb12.enableLightSources(new ChunkPos(integer2, integer3), true);
        for (int integer8 = 0; integer8 < arr11.length; ++integer8) {
            final LevelChunkSection bxu14 = arr11[integer8];
            clb12.updateSectionStatus(SectionPos.of(integer2, integer8, integer3), LevelChunkSection.isEmpty(bxu14));
        }
        return bxt10;
    }
    
    @Override
    public void tick(final BooleanSupplier booleanSupplier) {
    }
    
    public void updateViewCenter(final int integer1, final int integer2) {
        this.storage.viewCenterX = integer1;
        this.storage.viewCenterZ = integer2;
    }
    
    public void updateViewRadius(final int integer) {
        final int integer2 = this.storage.chunkRadius;
        final int integer3 = calculateStorageRange(integer);
        if (integer2 != integer3) {
            final Storage a5 = new Storage(integer3);
            a5.viewCenterX = this.storage.viewCenterX;
            a5.viewCenterZ = this.storage.viewCenterZ;
            for (int integer4 = 0; integer4 < this.storage.chunks.length(); ++integer4) {
                final LevelChunk bxt7 = (LevelChunk)this.storage.chunks.get(integer4);
                if (bxt7 != null) {
                    final ChunkPos bhd8 = bxt7.getPos();
                    if (a5.inRange(bhd8.x, bhd8.z)) {
                        a5.replace(a5.getIndex(bhd8.x, bhd8.z), bxt7);
                    }
                }
            }
            this.storage = a5;
        }
    }
    
    private static int calculateStorageRange(final int integer) {
        return Math.max(2, integer) + 3;
    }
    
    @Override
    public String gatherStats() {
        return new StringBuilder().append("Client Chunk Cache: ").append(this.storage.chunks.length()).append(", ").append(this.getLoadedChunksCount()).toString();
    }
    
    @Override
    public ChunkGenerator<?> getGenerator() {
        return null;
    }
    
    public int getLoadedChunksCount() {
        return this.storage.chunkCount;
    }
    
    public void onLightUpdate(final LightLayer bia, final SectionPos fp) {
        Minecraft.getInstance().levelRenderer.setSectionDirty(fp.x(), fp.y(), fp.z());
    }
    
    @Override
    public boolean isTickingChunk(final BlockPos ew) {
        return this.hasChunk(ew.getX() >> 4, ew.getZ() >> 4);
    }
    
    @Override
    public boolean isEntityTickingChunk(final ChunkPos bhd) {
        return this.hasChunk(bhd.x, bhd.z);
    }
    
    @Override
    public boolean isEntityTickingChunk(final Entity aio) {
        return this.hasChunk(Mth.floor(aio.x) >> 4, Mth.floor(aio.z) >> 4);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    final class Storage {
        private final AtomicReferenceArray<LevelChunk> chunks;
        private final int chunkRadius;
        private final int viewRange;
        private volatile int viewCenterX;
        private volatile int viewCenterZ;
        private int chunkCount;
        
        private Storage(final int integer) {
            this.chunkRadius = integer;
            this.viewRange = integer * 2 + 1;
            this.chunks = (AtomicReferenceArray<LevelChunk>)new AtomicReferenceArray(this.viewRange * this.viewRange);
        }
        
        private int getIndex(final int integer1, final int integer2) {
            return Math.floorMod(integer2, this.viewRange) * this.viewRange + Math.floorMod(integer1, this.viewRange);
        }
        
        protected void replace(final int integer, @Nullable final LevelChunk bxt) {
            final LevelChunk bxt2 = (LevelChunk)this.chunks.getAndSet(integer, bxt);
            if (bxt2 != null) {
                --this.chunkCount;
                ClientChunkCache.this.level.unload(bxt2);
            }
            if (bxt != null) {
                ++this.chunkCount;
            }
        }
        
        protected LevelChunk replace(final int integer, final LevelChunk bxt2, @Nullable final LevelChunk bxt3) {
            if (this.chunks.compareAndSet(integer, bxt2, bxt3) && bxt3 == null) {
                --this.chunkCount;
            }
            ClientChunkCache.this.level.unload(bxt2);
            return bxt2;
        }
        
        private boolean inRange(final int integer1, final int integer2) {
            return Math.abs(integer1 - this.viewCenterX) <= this.chunkRadius && Math.abs(integer2 - this.viewCenterZ) <= this.chunkRadius;
        }
        
        @Nullable
        protected LevelChunk getChunk(final int integer) {
            return (LevelChunk)this.chunks.get(integer);
        }
    }
}
