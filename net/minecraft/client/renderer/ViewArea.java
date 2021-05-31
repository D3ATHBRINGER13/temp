package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.chunk.RenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.Level;

public class ViewArea {
    protected final LevelRenderer levelRenderer;
    protected final Level level;
    protected int chunkGridSizeY;
    protected int chunkGridSizeX;
    protected int chunkGridSizeZ;
    public RenderChunk[] chunks;
    
    public ViewArea(final Level bhr, final int integer, final LevelRenderer dng, final RenderChunkFactory dpz) {
        this.levelRenderer = dng;
        this.level = bhr;
        this.setViewDistance(integer);
        this.createChunks(dpz);
    }
    
    protected void createChunks(final RenderChunkFactory dpz) {
        final int integer3 = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
        this.chunks = new RenderChunk[integer3];
        for (int integer4 = 0; integer4 < this.chunkGridSizeX; ++integer4) {
            for (int integer5 = 0; integer5 < this.chunkGridSizeY; ++integer5) {
                for (int integer6 = 0; integer6 < this.chunkGridSizeZ; ++integer6) {
                    final int integer7 = this.getChunkIndex(integer4, integer5, integer6);
                    (this.chunks[integer7] = dpz.create(this.level, this.levelRenderer)).setOrigin(integer4 * 16, integer5 * 16, integer6 * 16);
                }
            }
        }
    }
    
    public void releaseAllBuffers() {
        for (final RenderChunk dpy5 : this.chunks) {
            dpy5.releaseBuffers();
        }
    }
    
    private int getChunkIndex(final int integer1, final int integer2, final int integer3) {
        return (integer3 * this.chunkGridSizeY + integer2) * this.chunkGridSizeX + integer1;
    }
    
    protected void setViewDistance(final int integer) {
        final int integer2 = integer * 2 + 1;
        this.chunkGridSizeX = integer2;
        this.chunkGridSizeY = 16;
        this.chunkGridSizeZ = integer2;
    }
    
    public void repositionCamera(final double double1, final double double2) {
        final int integer6 = Mth.floor(double1) - 8;
        final int integer7 = Mth.floor(double2) - 8;
        final int integer8 = this.chunkGridSizeX * 16;
        for (int integer9 = 0; integer9 < this.chunkGridSizeX; ++integer9) {
            final int integer10 = this.getCoordinate(integer6, integer8, integer9);
            for (int integer11 = 0; integer11 < this.chunkGridSizeZ; ++integer11) {
                final int integer12 = this.getCoordinate(integer7, integer8, integer11);
                for (int integer13 = 0; integer13 < this.chunkGridSizeY; ++integer13) {
                    final int integer14 = integer13 * 16;
                    final RenderChunk dpy15 = this.chunks[this.getChunkIndex(integer9, integer13, integer11)];
                    dpy15.setOrigin(integer10, integer14, integer12);
                }
            }
        }
    }
    
    private int getCoordinate(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer3 * 16;
        int integer5 = integer4 - integer1 + integer2 / 2;
        if (integer5 < 0) {
            integer5 -= integer2 - 1;
        }
        return integer4 - integer5 / integer2 * integer2;
    }
    
    public void setDirty(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        final int integer4 = Math.floorMod(integer1, this.chunkGridSizeX);
        final int integer5 = Math.floorMod(integer2, this.chunkGridSizeY);
        final int integer6 = Math.floorMod(integer3, this.chunkGridSizeZ);
        final RenderChunk dpy9 = this.chunks[this.getChunkIndex(integer4, integer5, integer6)];
        dpy9.setDirty(boolean4);
    }
    
    @Nullable
    protected RenderChunk getRenderChunkAt(final BlockPos ew) {
        int integer3 = Mth.intFloorDiv(ew.getX(), 16);
        final int integer4 = Mth.intFloorDiv(ew.getY(), 16);
        int integer5 = Mth.intFloorDiv(ew.getZ(), 16);
        if (integer4 < 0 || integer4 >= this.chunkGridSizeY) {
            return null;
        }
        integer3 = Mth.positiveModulo(integer3, this.chunkGridSizeX);
        integer5 = Mth.positiveModulo(integer5, this.chunkGridSizeZ);
        return this.chunks[this.getChunkIndex(integer3, integer4, integer5)];
    }
}
