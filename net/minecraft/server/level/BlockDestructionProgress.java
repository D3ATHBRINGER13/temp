package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class BlockDestructionProgress {
    private final int id;
    private final BlockPos pos;
    private int progress;
    private int updatedRenderTick;
    
    public BlockDestructionProgress(final int integer, final BlockPos ew) {
        this.id = integer;
        this.pos = ew;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public void setProgress(int integer) {
        if (integer > 10) {
            integer = 10;
        }
        this.progress = integer;
    }
    
    public int getProgress() {
        return this.progress;
    }
    
    public void updateTick(final int integer) {
        this.updatedRenderTick = integer;
    }
    
    public int getUpdatedRenderTick() {
        return this.updatedRenderTick;
    }
}
