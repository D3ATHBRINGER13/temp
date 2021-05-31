package net.minecraft.client.renderer.chunk;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.LightLayer;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndBiomeGetter;

public class RenderChunkRegion implements BlockAndBiomeGetter {
    protected final int centerX;
    protected final int centerZ;
    protected final BlockPos start;
    protected final int xLength;
    protected final int yLength;
    protected final int zLength;
    protected final LevelChunk[][] chunks;
    protected final BlockState[] blockStates;
    protected final FluidState[] fluidStates;
    protected final Level level;
    
    @Nullable
    public static RenderChunkRegion createIfNotEmpty(final Level bhr, final BlockPos ew2, final BlockPos ew3, final int integer) {
        final int integer2 = ew2.getX() - integer >> 4;
        final int integer3 = ew2.getZ() - integer >> 4;
        final int integer4 = ew3.getX() + integer >> 4;
        final int integer5 = ew3.getZ() + integer >> 4;
        final LevelChunk[][] arr9 = new LevelChunk[integer4 - integer2 + 1][integer5 - integer3 + 1];
        for (int integer6 = integer2; integer6 <= integer4; ++integer6) {
            for (int integer7 = integer3; integer7 <= integer5; ++integer7) {
                arr9[integer6 - integer2][integer7 - integer3] = bhr.getChunk(integer6, integer7);
            }
        }
        boolean boolean10 = true;
        for (int integer7 = ew2.getX() >> 4; integer7 <= ew3.getX() >> 4; ++integer7) {
            for (int integer8 = ew2.getZ() >> 4; integer8 <= ew3.getZ() >> 4; ++integer8) {
                final LevelChunk bxt13 = arr9[integer7 - integer2][integer8 - integer3];
                if (!bxt13.isYSpaceEmpty(ew2.getY(), ew3.getY())) {
                    boolean10 = false;
                }
            }
        }
        if (boolean10) {
            return null;
        }
        int integer7 = 1;
        final BlockPos ew4 = ew2.offset(-1, -1, -1);
        final BlockPos ew5 = ew3.offset(1, 1, 1);
        return new RenderChunkRegion(bhr, integer2, integer3, arr9, ew4, ew5);
    }
    
    public RenderChunkRegion(final Level bhr, final int integer2, final int integer3, final LevelChunk[][] arr, final BlockPos ew5, final BlockPos ew6) {
        this.level = bhr;
        this.centerX = integer2;
        this.centerZ = integer3;
        this.chunks = arr;
        this.start = ew5;
        this.xLength = ew6.getX() - ew5.getX() + 1;
        this.yLength = ew6.getY() - ew5.getY() + 1;
        this.zLength = ew6.getZ() - ew5.getZ() + 1;
        this.blockStates = new BlockState[this.xLength * this.yLength * this.zLength];
        this.fluidStates = new FluidState[this.xLength * this.yLength * this.zLength];
        for (final BlockPos ew7 : BlockPos.betweenClosed(ew5, ew6)) {
            final int integer4 = (ew7.getX() >> 4) - integer2;
            final int integer5 = (ew7.getZ() >> 4) - integer3;
            final LevelChunk bxt12 = arr[integer4][integer5];
            final int integer6 = this.index(ew7);
            this.blockStates[integer6] = bxt12.getBlockState(ew7);
            this.fluidStates[integer6] = bxt12.getFluidState(ew7);
        }
    }
    
    protected final int index(final BlockPos ew) {
        return this.index(ew.getX(), ew.getY(), ew.getZ());
    }
    
    protected int index(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer1 - this.start.getX();
        final int integer5 = integer2 - this.start.getY();
        final int integer6 = integer3 - this.start.getZ();
        return integer6 * this.xLength * this.yLength + integer5 * this.xLength + integer4;
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        return this.blockStates[this.index(ew)];
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        return this.fluidStates[this.index(ew)];
    }
    
    public int getBrightness(final LightLayer bia, final BlockPos ew) {
        return this.level.getBrightness(bia, ew);
    }
    
    public Biome getBiome(final BlockPos ew) {
        final int integer3 = (ew.getX() >> 4) - this.centerX;
        final int integer4 = (ew.getZ() >> 4) - this.centerZ;
        return this.chunks[integer3][integer4].getBiome(ew);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        return this.getBlockEntity(ew, LevelChunk.EntityCreationType.IMMEDIATE);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew, final LevelChunk.EntityCreationType a) {
        final int integer4 = (ew.getX() >> 4) - this.centerX;
        final int integer5 = (ew.getZ() >> 4) - this.centerZ;
        return this.chunks[integer4][integer5].getBlockEntity(ew, a);
    }
}
