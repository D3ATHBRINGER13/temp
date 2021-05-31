package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import java.util.Iterator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;

public class VboRenderList extends ChunkRenderList {
    @Override
    public void render(final BlockLayer bhc) {
        if (!this.ready) {
            return;
        }
        for (final RenderChunk dpy4 : this.chunks) {
            final VertexBuffer cva5 = dpy4.getBuffer(bhc.ordinal());
            GlStateManager.pushMatrix();
            this.translateToRelativeChunkPosition(dpy4);
            cva5.bind();
            this.applyVertexDeclaration();
            cva5.draw(7);
            GlStateManager.popMatrix();
        }
        VertexBuffer.unbind();
        GlStateManager.clearCurrentColor();
        this.chunks.clear();
    }
    
    private void applyVertexDeclaration() {
        GlStateManager.vertexPointer(3, 5126, 28, 0);
        GlStateManager.colorPointer(4, 5121, 28, 12);
        GlStateManager.texCoordPointer(2, 5126, 28, 16);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
        GlStateManager.texCoordPointer(2, 5122, 28, 24);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
    }
}
