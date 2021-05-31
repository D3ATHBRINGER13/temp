package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;

public class ListedRenderChunk extends RenderChunk {
    private final int listId;
    
    public ListedRenderChunk(final Level bhr, final LevelRenderer dng) {
        super(bhr, dng);
        this.listId = MemoryTracker.genLists(BlockLayer.values().length);
    }
    
    public int getGlListId(final BlockLayer bhc, final CompiledChunk dpw) {
        if (!dpw.isEmpty(bhc)) {
            return this.listId + bhc.ordinal();
        }
        return -1;
    }
    
    @Override
    public void releaseBuffers() {
        super.releaseBuffers();
        MemoryTracker.releaseLists(this.listId, BlockLayer.values().length);
    }
}
