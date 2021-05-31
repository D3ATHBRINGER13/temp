package net.minecraft.world.level.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;

public abstract class ChunkSource implements LightChunkGetter, AutoCloseable {
    @Nullable
    public LevelChunk getChunk(final int integer1, final int integer2, final boolean boolean3) {
        return (LevelChunk)this.getChunk(integer1, integer2, ChunkStatus.FULL, boolean3);
    }
    
    @Nullable
    public LevelChunk getChunkNow(final int integer1, final int integer2) {
        return this.getChunk(integer1, integer2, false);
    }
    
    @Nullable
    public BlockGetter getChunkForLighting(final int integer1, final int integer2) {
        return this.getChunk(integer1, integer2, ChunkStatus.EMPTY, false);
    }
    
    public boolean hasChunk(final int integer1, final int integer2) {
        return this.getChunk(integer1, integer2, ChunkStatus.FULL, false) != null;
    }
    
    @Nullable
    public abstract ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4);
    
    public abstract void tick(final BooleanSupplier booleanSupplier);
    
    public abstract String gatherStats();
    
    public abstract ChunkGenerator<?> getGenerator();
    
    public void close() throws IOException {
    }
    
    public abstract LevelLightEngine getLightEngine();
    
    public void setSpawnSettings(final boolean boolean1, final boolean boolean2) {
    }
    
    public void updateChunkForced(final ChunkPos bhd, final boolean boolean2) {
    }
    
    public boolean isEntityTickingChunk(final Entity aio) {
        return true;
    }
    
    public boolean isEntityTickingChunk(final ChunkPos bhd) {
        return true;
    }
    
    public boolean isTickingChunk(final BlockPos ew) {
        return true;
    }
}
