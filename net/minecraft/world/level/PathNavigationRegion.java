package net.minecraft.world.level;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public class PathNavigationRegion implements LevelReader {
    protected final int centerX;
    protected final int centerZ;
    protected final ChunkAccess[][] chunks;
    protected boolean allEmpty;
    protected final Level level;
    
    public PathNavigationRegion(final Level bhr, final BlockPos ew2, final BlockPos ew3) {
        this.level = bhr;
        this.centerX = ew2.getX() >> 4;
        this.centerZ = ew2.getZ() >> 4;
        final int integer5 = ew3.getX() >> 4;
        final int integer6 = ew3.getZ() >> 4;
        this.chunks = new ChunkAccess[integer5 - this.centerX + 1][integer6 - this.centerZ + 1];
        this.allEmpty = true;
        for (int integer7 = this.centerX; integer7 <= integer5; ++integer7) {
            for (int integer8 = this.centerZ; integer8 <= integer6; ++integer8) {
                this.chunks[integer7 - this.centerX][integer8 - this.centerZ] = bhr.getChunk(integer7, integer8, ChunkStatus.FULL, false);
            }
        }
        for (int integer7 = ew2.getX() >> 4; integer7 <= ew3.getX() >> 4; ++integer7) {
            for (int integer8 = ew2.getZ() >> 4; integer8 <= ew3.getZ() >> 4; ++integer8) {
                final ChunkAccess bxh9 = this.chunks[integer7 - this.centerX][integer8 - this.centerZ];
                if (bxh9 != null && !bxh9.isYSpaceEmpty(ew2.getY(), ew3.getY())) {
                    this.allEmpty = false;
                    return;
                }
            }
        }
    }
    
    public int getRawBrightness(final BlockPos ew, final int integer) {
        return this.level.getRawBrightness(ew, integer);
    }
    
    @Nullable
    public ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        final int integer3 = integer1 - this.centerX;
        final int integer4 = integer2 - this.centerZ;
        if (integer3 < 0 || integer3 >= this.chunks.length || integer4 < 0 || integer4 >= this.chunks[integer3].length) {
            return new EmptyLevelChunk(this.level, new ChunkPos(integer1, integer2));
        }
        final ChunkAccess bxh8 = this.chunks[integer3][integer4];
        return (bxh8 != null) ? bxh8 : new EmptyLevelChunk(this.level, new ChunkPos(integer1, integer2));
    }
    
    public boolean hasChunk(final int integer1, final int integer2) {
        final int integer3 = integer1 - this.centerX;
        final int integer4 = integer2 - this.centerZ;
        return integer3 >= 0 && integer3 < this.chunks.length && integer4 >= 0 && integer4 < this.chunks[integer3].length;
    }
    
    public BlockPos getHeightmapPos(final Heightmap.Types a, final BlockPos ew) {
        return this.level.getHeightmapPos(a, ew);
    }
    
    public int getHeight(final Heightmap.Types a, final int integer2, final int integer3) {
        return this.level.getHeight(a, integer2, integer3);
    }
    
    public int getSkyDarken() {
        return this.level.getSkyDarken();
    }
    
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }
    
    public boolean isUnobstructed(@Nullable final Entity aio, final VoxelShape ctc) {
        return true;
    }
    
    public boolean isClientSide() {
        return false;
    }
    
    public int getSeaLevel() {
        return this.level.getSeaLevel();
    }
    
    public Dimension getDimension() {
        return this.level.getDimension();
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        final ChunkAccess bxh3 = this.getChunk(ew);
        return bxh3.getBlockEntity(ew);
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        if (Level.isOutsideBuildHeight(ew)) {
            return Blocks.AIR.defaultBlockState();
        }
        final ChunkAccess bxh3 = this.getChunk(ew);
        return bxh3.getBlockState(ew);
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        if (Level.isOutsideBuildHeight(ew)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        final ChunkAccess bxh3 = this.getChunk(ew);
        return bxh3.getFluidState(ew);
    }
    
    public Biome getBiome(final BlockPos ew) {
        final ChunkAccess bxh3 = this.getChunk(ew);
        return bxh3.getBiome(ew);
    }
    
    public int getBrightness(final LightLayer bia, final BlockPos ew) {
        return this.level.getBrightness(bia, ew);
    }
}
