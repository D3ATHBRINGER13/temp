package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class StoringChunkProgressListener implements ChunkProgressListener {
    private final LoggerChunkProgressListener delegate;
    private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
    private ChunkPos spawnPos;
    private final int fullDiameter;
    private final int radius;
    private final int diameter;
    private boolean started;
    
    public StoringChunkProgressListener(final int integer) {
        this.spawnPos = new ChunkPos(0, 0);
        this.delegate = new LoggerChunkProgressListener(integer);
        this.fullDiameter = integer * 2 + 1;
        this.radius = integer + ChunkStatus.maxDistance();
        this.diameter = this.radius * 2 + 1;
        this.statuses = (Long2ObjectOpenHashMap<ChunkStatus>)new Long2ObjectOpenHashMap();
    }
    
    public void updateSpawnPos(final ChunkPos bhd) {
        if (!this.started) {
            return;
        }
        this.delegate.updateSpawnPos(bhd);
        this.spawnPos = bhd;
    }
    
    public void onStatusChange(final ChunkPos bhd, @Nullable final ChunkStatus bxm) {
        if (!this.started) {
            return;
        }
        this.delegate.onStatusChange(bhd, bxm);
        if (bxm == null) {
            this.statuses.remove(bhd.toLong());
        }
        else {
            this.statuses.put(bhd.toLong(), bxm);
        }
    }
    
    public void start() {
        this.started = true;
        this.statuses.clear();
    }
    
    public void stop() {
        this.started = false;
        this.delegate.stop();
    }
    
    public int getFullDiameter() {
        return this.fullDiameter;
    }
    
    public int getDiameter() {
        return this.diameter;
    }
    
    public int getProgress() {
        return this.delegate.getProgress();
    }
    
    @Nullable
    public ChunkStatus getStatus(final int integer1, final int integer2) {
        return (ChunkStatus)this.statuses.get(ChunkPos.asLong(integer1 + this.spawnPos.x - this.radius, integer2 + this.spawnPos.z - this.radius));
    }
}
