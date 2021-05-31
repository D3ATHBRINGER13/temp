package net.minecraft.client.renderer;

import net.minecraft.world.level.BlockLayer;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.chunk.RenderChunk;
import java.util.List;

public abstract class ChunkRenderList {
    private double xOff;
    private double yOff;
    private double zOff;
    protected final List<RenderChunk> chunks;
    protected boolean ready;
    
    public ChunkRenderList() {
        this.chunks = (List<RenderChunk>)Lists.newArrayListWithCapacity(17424);
    }
    
    public void setCameraLocation(final double double1, final double double2, final double double3) {
        this.ready = true;
        this.chunks.clear();
        this.xOff = double1;
        this.yOff = double2;
        this.zOff = double3;
    }
    
    public void translateToRelativeChunkPosition(final RenderChunk dpy) {
        final BlockPos ew3 = dpy.getOrigin();
        GlStateManager.translatef((float)(ew3.getX() - this.xOff), (float)(ew3.getY() - this.yOff), (float)(ew3.getZ() - this.zOff));
    }
    
    public void add(final RenderChunk dpy, final BlockLayer bhc) {
        this.chunks.add(dpy);
    }
    
    public abstract void render(final BlockLayer bhc);
}
