package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;

public class TheEndGeneratorSettings extends ChunkGeneratorSettings {
    private BlockPos spawnPosition;
    
    public TheEndGeneratorSettings setSpawnPosition(final BlockPos ew) {
        this.spawnPosition = ew;
        return this;
    }
    
    public BlockPos getSpawnPosition() {
        return this.spawnPosition;
    }
}
