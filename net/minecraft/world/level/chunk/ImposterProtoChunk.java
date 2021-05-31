package net.minecraft.world.level.chunk;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.TickList;
import java.util.BitSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluid;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.biome.Biome;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public class ImposterProtoChunk extends ProtoChunk {
    private final LevelChunk wrapped;
    
    public ImposterProtoChunk(final LevelChunk bxt) {
        super(bxt.getPos(), UpgradeData.EMPTY);
        this.wrapped = bxt;
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos ew) {
        return this.wrapped.getBlockEntity(ew);
    }
    
    @Nullable
    @Override
    public BlockState getBlockState(final BlockPos ew) {
        return this.wrapped.getBlockState(ew);
    }
    
    @Override
    public FluidState getFluidState(final BlockPos ew) {
        return this.wrapped.getFluidState(ew);
    }
    
    public int getMaxLightLevel() {
        return this.wrapped.getMaxLightLevel();
    }
    
    @Nullable
    @Override
    public BlockState setBlockState(final BlockPos ew, final BlockState bvt, final boolean boolean3) {
        return null;
    }
    
    @Override
    public void setBlockEntity(final BlockPos ew, final BlockEntity btw) {
    }
    
    @Override
    public void addEntity(final Entity aio) {
    }
    
    @Override
    public void setStatus(final ChunkStatus bxm) {
    }
    
    @Override
    public LevelChunkSection[] getSections() {
        return this.wrapped.getSections();
    }
    
    @Nullable
    @Override
    public LevelLightEngine getLightEngine() {
        return this.wrapped.getLightEngine();
    }
    
    @Override
    public void setHeightmap(final Heightmap.Types a, final long[] arr) {
    }
    
    private Heightmap.Types fixType(final Heightmap.Types a) {
        if (a == Heightmap.Types.WORLD_SURFACE_WG) {
            return Heightmap.Types.WORLD_SURFACE;
        }
        if (a == Heightmap.Types.OCEAN_FLOOR_WG) {
            return Heightmap.Types.OCEAN_FLOOR;
        }
        return a;
    }
    
    @Override
    public int getHeight(final Heightmap.Types a, final int integer2, final int integer3) {
        return this.wrapped.getHeight(this.fixType(a), integer2, integer3);
    }
    
    @Override
    public ChunkPos getPos() {
        return this.wrapped.getPos();
    }
    
    @Override
    public void setLastSaveTime(final long long1) {
    }
    
    @Nullable
    @Override
    public StructureStart getStartForFeature(final String string) {
        return this.wrapped.getStartForFeature(string);
    }
    
    @Override
    public void setStartForFeature(final String string, final StructureStart ciw) {
    }
    
    @Override
    public Map<String, StructureStart> getAllStarts() {
        return this.wrapped.getAllStarts();
    }
    
    @Override
    public void setAllStarts(final Map<String, StructureStart> map) {
    }
    
    @Override
    public LongSet getReferencesForFeature(final String string) {
        return this.wrapped.getReferencesForFeature(string);
    }
    
    @Override
    public void addReferenceForFeature(final String string, final long long2) {
    }
    
    @Override
    public Map<String, LongSet> getAllReferences() {
        return this.wrapped.getAllReferences();
    }
    
    @Override
    public void setAllReferences(final Map<String, LongSet> map) {
    }
    
    @Override
    public Biome[] getBiomes() {
        return this.wrapped.getBiomes();
    }
    
    @Override
    public void setUnsaved(final boolean boolean1) {
    }
    
    @Override
    public boolean isUnsaved() {
        return false;
    }
    
    @Override
    public ChunkStatus getStatus() {
        return this.wrapped.getStatus();
    }
    
    @Override
    public void removeBlockEntity(final BlockPos ew) {
    }
    
    @Override
    public void markPosForPostprocessing(final BlockPos ew) {
    }
    
    @Override
    public void setBlockEntityNbt(final CompoundTag id) {
    }
    
    @Nullable
    @Override
    public CompoundTag getBlockEntityNbt(final BlockPos ew) {
        return this.wrapped.getBlockEntityNbt(ew);
    }
    
    @Nullable
    @Override
    public CompoundTag getBlockEntityNbtForSaving(final BlockPos ew) {
        return this.wrapped.getBlockEntityNbtForSaving(ew);
    }
    
    @Override
    public void setBiomes(final Biome[] arr) {
    }
    
    @Override
    public Stream<BlockPos> getLights() {
        return this.wrapped.getLights();
    }
    
    @Override
    public ProtoTickList<Block> getBlockTicks() {
        return new ProtoTickList<Block>((java.util.function.Predicate<Block>)(bmv -> bmv.defaultBlockState().isAir()), this.getPos());
    }
    
    @Override
    public ProtoTickList<Fluid> getLiquidTicks() {
        return new ProtoTickList<Fluid>((java.util.function.Predicate<Fluid>)(clj -> clj == Fluids.EMPTY), this.getPos());
    }
    
    @Override
    public BitSet getCarvingMask(final GenerationStep.Carving a) {
        return this.wrapped.getCarvingMask(a);
    }
    
    public LevelChunk getWrapped() {
        return this.wrapped;
    }
    
    @Override
    public boolean isLightCorrect() {
        return this.wrapped.isLightCorrect();
    }
    
    @Override
    public void setLightCorrect(final boolean boolean1) {
        this.wrapped.setLightCorrect(boolean1);
    }
}
