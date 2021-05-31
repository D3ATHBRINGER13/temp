package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.util.BitSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.TickList;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.shorts.ShortList;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Map;
import java.util.Collection;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public interface ChunkAccess extends FeatureAccess {
    @Nullable
    BlockState setBlockState(final BlockPos ew, final BlockState bvt, final boolean boolean3);
    
    void setBlockEntity(final BlockPos ew, final BlockEntity btw);
    
    void addEntity(final Entity aio);
    
    @Nullable
    default LevelChunkSection getHighestSection() {
        final LevelChunkSection[] arr2 = this.getSections();
        for (int integer3 = arr2.length - 1; integer3 >= 0; --integer3) {
            final LevelChunkSection bxu4 = arr2[integer3];
            if (!LevelChunkSection.isEmpty(bxu4)) {
                return bxu4;
            }
        }
        return null;
    }
    
    default int getHighestSectionPosition() {
        final LevelChunkSection bxu2 = this.getHighestSection();
        return (bxu2 == null) ? 0 : bxu2.bottomBlockY();
    }
    
    Set<BlockPos> getBlockEntitiesPos();
    
    LevelChunkSection[] getSections();
    
    @Nullable
    LevelLightEngine getLightEngine();
    
    default int getRawBrightness(final BlockPos ew, final int integer, final boolean boolean3) {
        final LevelLightEngine clb5 = this.getLightEngine();
        if (clb5 == null || !this.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            return 0;
        }
        final int integer2 = boolean3 ? (clb5.getLayerListener(LightLayer.SKY).getLightValue(ew) - integer) : 0;
        final int integer3 = clb5.getLayerListener(LightLayer.BLOCK).getLightValue(ew);
        return Math.max(integer3, integer2);
    }
    
    Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps();
    
    void setHeightmap(final Heightmap.Types a, final long[] arr);
    
    Heightmap getOrCreateHeightmapUnprimed(final Heightmap.Types a);
    
    int getHeight(final Heightmap.Types a, final int integer2, final int integer3);
    
    ChunkPos getPos();
    
    void setLastSaveTime(final long long1);
    
    Map<String, StructureStart> getAllStarts();
    
    void setAllStarts(final Map<String, StructureStart> map);
    
    default Biome getBiome(final BlockPos ew) {
        final int integer3 = ew.getX() & 0xF;
        final int integer4 = ew.getZ() & 0xF;
        return this.getBiomes()[integer4 << 4 | integer3];
    }
    
    default boolean isYSpaceEmpty(int integer1, int integer2) {
        if (integer1 < 0) {
            integer1 = 0;
        }
        if (integer2 >= 256) {
            integer2 = 255;
        }
        for (int integer3 = integer1; integer3 <= integer2; integer3 += 16) {
            if (!LevelChunkSection.isEmpty(this.getSections()[integer3 >> 4])) {
                return false;
            }
        }
        return true;
    }
    
    Biome[] getBiomes();
    
    void setUnsaved(final boolean boolean1);
    
    boolean isUnsaved();
    
    ChunkStatus getStatus();
    
    void removeBlockEntity(final BlockPos ew);
    
    void setLightEngine(final LevelLightEngine clb);
    
    default void markPosForPostprocessing(final BlockPos ew) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", ew);
    }
    
    ShortList[] getPostProcessing();
    
    default void addPackedPostProcess(final short short1, final int integer) {
        getOrCreateOffsetList(this.getPostProcessing(), integer).add(short1);
    }
    
    default void setBlockEntityNbt(final CompoundTag id) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }
    
    @Nullable
    CompoundTag getBlockEntityNbt(final BlockPos ew);
    
    @Nullable
    CompoundTag getBlockEntityNbtForSaving(final BlockPos ew);
    
    default void setBiomes(final Biome[] arr) {
        throw new UnsupportedOperationException();
    }
    
    Stream<BlockPos> getLights();
    
    TickList<Block> getBlockTicks();
    
    TickList<Fluid> getLiquidTicks();
    
    default BitSet getCarvingMask(final GenerationStep.Carving a) {
        throw new RuntimeException("Meaningless in this context");
    }
    
    UpgradeData getUpgradeData();
    
    void setInhabitedTime(final long long1);
    
    long getInhabitedTime();
    
    default ShortList getOrCreateOffsetList(final ShortList[] arr, final int integer) {
        if (arr[integer] == null) {
            arr[integer] = (ShortList)new ShortArrayList();
        }
        return arr[integer];
    }
    
    boolean isLightCorrect();
    
    void setLightCorrect(final boolean boolean1);
}
