package net.minecraft.client.renderer;

import java.util.Iterator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;

public class OffsettedRenderList extends ChunkRenderList {
    @Override
    public void render(final BlockLayer bhc) {
        if (!this.ready) {
            return;
        }
        for (final RenderChunk dpy4 : this.chunks) {
            final ListedRenderChunk dpx5 = (ListedRenderChunk)dpy4;
            GlStateManager.pushMatrix();
            this.translateToRelativeChunkPosition(dpy4);
            GlStateManager.callList(dpx5.getGlListId(bhc, dpx5.getCompiledChunk()));
            GlStateManager.popMatrix();
        }
        GlStateManager.clearCurrentColor();
        this.chunks.clear();
    }
}
