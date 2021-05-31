package net.minecraft.world.level.chunk;

import java.util.function.Consumer;
import net.minecraft.Util;
import java.util.Arrays;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.server.level.ChunkHolder;
import java.util.function.Predicate;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class EmptyLevelChunk extends LevelChunk {
    private static final Biome[] BIOMES;
    
    public EmptyLevelChunk(final Level bhr, final ChunkPos bhd) {
        super(bhr, bhd, EmptyLevelChunk.BIOMES);
    }
    
    @Override
    public BlockState getBlockState(final BlockPos ew) {
        return Blocks.VOID_AIR.defaultBlockState();
    }
    
    @Nullable
    @Override
    public BlockState setBlockState(final BlockPos ew, final BlockState bvt, final boolean boolean3) {
        return null;
    }
    
    @Override
    public FluidState getFluidState(final BlockPos ew) {
        return Fluids.EMPTY.defaultFluidState();
    }
    
    @Nullable
    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }
    
    public int getLightEmission(final BlockPos ew) {
        return 0;
    }
    
    @Override
    public void addEntity(final Entity aio) {
    }
    
    @Override
    public void removeEntity(final Entity aio) {
    }
    
    @Override
    public void removeEntity(final Entity aio, final int integer) {
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos ew, final EntityCreationType a) {
        return null;
    }
    
    @Override
    public void addBlockEntity(final BlockEntity btw) {
    }
    
    @Override
    public void setBlockEntity(final BlockPos ew, final BlockEntity btw) {
    }
    
    @Override
    public void removeBlockEntity(final BlockPos ew) {
    }
    
    @Override
    public void markUnsaved() {
    }
    
    @Override
    public void getEntities(@Nullable final Entity aio, final AABB csc, final List<Entity> list, final Predicate<? super Entity> predicate) {
    }
    
    @Override
    public <T extends Entity> void getEntitiesOfClass(final Class<? extends T> class1, final AABB csc, final List<T> list, final Predicate<? super T> predicate) {
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    public boolean isYSpaceEmpty(final int integer1, final int integer2) {
        return true;
    }
    
    @Override
    public ChunkHolder.FullChunkStatus getFullStatus() {
        return ChunkHolder.FullChunkStatus.BORDER;
    }
    
    static {
        BIOMES = Util.<Biome[]>make(new Biome[256], (java.util.function.Consumer<Biome[]>)(arr -> Arrays.fill((Object[])arr, Biomes.PLAINS)));
    }
}
