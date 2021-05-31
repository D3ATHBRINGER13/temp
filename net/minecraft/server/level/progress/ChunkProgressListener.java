package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.ChunkPos;

public interface ChunkProgressListener {
    void updateSpawnPos(final ChunkPos bhd);
    
    void onStatusChange(final ChunkPos bhd, @Nullable final ChunkStatus bxm);
    
    void stop();
}
