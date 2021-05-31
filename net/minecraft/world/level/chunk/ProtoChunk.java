package net.minecraft.world.level.chunk;

import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.level.TickList;
import java.util.Collections;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.world.level.BlockGetter;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.function.Predicate;
import java.util.BitSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements ChunkAccess {
    private static final Logger LOGGER;
    private final ChunkPos chunkPos;
    private volatile boolean isDirty;
    private Biome[] biomes;
    @Nullable
    private volatile LevelLightEngine lightEngine;
    private final Map<Heightmap.Types, Heightmap> heightmaps;
    private volatile ChunkStatus status;
    private final Map<BlockPos, BlockEntity> blockEntities;
    private final Map<BlockPos, CompoundTag> blockEntityNbts;
    private final LevelChunkSection[] sections;
    private final List<CompoundTag> entities;
    private final List<BlockPos> lights;
    private final ShortList[] postProcessing;
    private final Map<String, StructureStart> structureStarts;
    private final Map<String, LongSet> structuresRefences;
    private final UpgradeData upgradeData;
    private final ProtoTickList<Block> blockTicks;
    private final ProtoTickList<Fluid> liquidTicks;
    private long inhabitedTime;
    private final Map<GenerationStep.Carving, BitSet> carvingMasks;
    private volatile boolean isLightCorrect;
    
    public ProtoChunk(final ChunkPos bhd, final UpgradeData byd) {
        this(bhd, byd, null, new ProtoTickList<Block>((java.util.function.Predicate<Block>)(bmv -> bmv == null || bmv.defaultBlockState().isAir()), bhd), new ProtoTickList<Fluid>((java.util.function.Predicate<Fluid>)(clj -> clj == null || clj == Fluids.EMPTY), bhd));
    }
    
    public ProtoChunk(final ChunkPos bhd, final UpgradeData byd, @Nullable final LevelChunkSection[] arr, final ProtoTickList<Block> byc4, final ProtoTickList<Fluid> byc5) {
        this.heightmaps = (Map<Heightmap.Types, Heightmap>)Maps.newEnumMap((Class)Heightmap.Types.class);
        this.status = ChunkStatus.EMPTY;
        this.blockEntities = (Map<BlockPos, BlockEntity>)Maps.newHashMap();
        this.blockEntityNbts = (Map<BlockPos, CompoundTag>)Maps.newHashMap();
        this.sections = new LevelChunkSection[16];
        this.entities = (List<CompoundTag>)Lists.newArrayList();
        this.lights = (List<BlockPos>)Lists.newArrayList();
        this.postProcessing = new ShortList[16];
        this.structureStarts = (Map<String, StructureStart>)Maps.newHashMap();
        this.structuresRefences = (Map<String, LongSet>)Maps.newHashMap();
        this.carvingMasks = (Map<GenerationStep.Carving, BitSet>)Maps.newHashMap();
        this.chunkPos = bhd;
        this.upgradeData = byd;
        this.blockTicks = byc4;
        this.liquidTicks = byc5;
        if (arr != null) {
            if (this.sections.length == arr.length) {
                System.arraycopy(arr, 0, this.sections, 0, this.sections.length);
            }
            else {
                ProtoChunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", arr.length, this.sections.length);
            }
        }
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        final int integer3 = ew.getY();
        if (Level.isOutsideBuildHeight(integer3)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        final LevelChunkSection bxu4 = this.getSections()[integer3 >> 4];
        if (LevelChunkSection.isEmpty(bxu4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return bxu4.getBlockState(ew.getX() & 0xF, integer3 & 0xF, ew.getZ() & 0xF);
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        final int integer3 = ew.getY();
        if (Level.isOutsideBuildHeight(integer3)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        final LevelChunkSection bxu4 = this.getSections()[integer3 >> 4];
        if (LevelChunkSection.isEmpty(bxu4)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return bxu4.getFluidState(ew.getX() & 0xF, integer3 & 0xF, ew.getZ() & 0xF);
    }
    
    public Stream<BlockPos> getLights() {
        return (Stream<BlockPos>)this.lights.stream();
    }
    
    public ShortList[] getPackedLights() {
        final ShortList[] arr2 = new ShortList[16];
        for (final BlockPos ew4 : this.lights) {
            ChunkAccess.getOrCreateOffsetList(arr2, ew4.getY() >> 4).add(packOffsetCoordinates(ew4));
        }
        return arr2;
    }
    
    public void addLight(final short short1, final int integer) {
        this.addLight(unpackOffsetCoordinates(short1, integer, this.chunkPos));
    }
    
    public void addLight(final BlockPos ew) {
        this.lights.add(ew.immutable());
    }
    
    @Nullable
    public BlockState setBlockState(final BlockPos ew, final BlockState bvt, final boolean boolean3) {
        final int integer5 = ew.getX();
        final int integer6 = ew.getY();
        final int integer7 = ew.getZ();
        if (integer6 < 0 || integer6 >= 256) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        if (this.sections[integer6 >> 4] == LevelChunk.EMPTY_SECTION && bvt.getBlock() == Blocks.AIR) {
            return bvt;
        }
        if (bvt.getLightEmission() > 0) {
            this.lights.add(new BlockPos((integer5 & 0xF) + this.getPos().getMinBlockX(), integer6, (integer7 & 0xF) + this.getPos().getMinBlockZ()));
        }
        final LevelChunkSection bxu8 = this.getOrCreateSection(integer6 >> 4);
        final BlockState bvt2 = bxu8.setBlockState(integer5 & 0xF, integer6 & 0xF, integer7 & 0xF, bvt);
        if (this.status.isOrAfter(ChunkStatus.FEATURES) && bvt != bvt2 && (bvt.getLightBlock(this, ew) != bvt2.getLightBlock(this, ew) || bvt.getLightEmission() != bvt2.getLightEmission() || bvt.useShapeForLightOcclusion() || bvt2.useShapeForLightOcclusion())) {
            final LevelLightEngine clb10 = this.getLightEngine();
            clb10.checkBlock(ew);
        }
        final EnumSet<Heightmap.Types> enumSet10 = this.getStatus().heightmapsAfter();
        EnumSet<Heightmap.Types> enumSet11 = null;
        for (final Heightmap.Types a13 : enumSet10) {
            final Heightmap bza14 = (Heightmap)this.heightmaps.get(a13);
            if (bza14 == null) {
                if (enumSet11 == null) {
                    enumSet11 = (EnumSet<Heightmap.Types>)EnumSet.noneOf((Class)Heightmap.Types.class);
                }
                enumSet11.add(a13);
            }
        }
        if (enumSet11 != null) {
            Heightmap.primeHeightmaps(this, (Set<Heightmap.Types>)enumSet11);
        }
        for (final Heightmap.Types a13 : enumSet10) {
            ((Heightmap)this.heightmaps.get(a13)).update(integer5 & 0xF, integer6, integer7 & 0xF, bvt);
        }
        return bvt2;
    }
    
    public LevelChunkSection getOrCreateSection(final int integer) {
        if (this.sections[integer] == LevelChunk.EMPTY_SECTION) {
            this.sections[integer] = new LevelChunkSection(integer << 4);
        }
        return this.sections[integer];
    }
    
    public void setBlockEntity(final BlockPos ew, final BlockEntity btw) {
        btw.setPosition(ew);
        this.blockEntities.put(ew, btw);
    }
    
    public Set<BlockPos> getBlockEntitiesPos() {
        final Set<BlockPos> set2 = (Set<BlockPos>)Sets.newHashSet((Iterable)this.blockEntityNbts.keySet());
        set2.addAll((Collection)this.blockEntities.keySet());
        return set2;
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        return (BlockEntity)this.blockEntities.get(ew);
    }
    
    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }
    
    public void addEntity(final CompoundTag id) {
        this.entities.add(id);
    }
    
    public void addEntity(final Entity aio) {
        final CompoundTag id3 = new CompoundTag();
        aio.save(id3);
        this.addEntity(id3);
    }
    
    public List<CompoundTag> getEntities() {
        return this.entities;
    }
    
    public void setBiomes(final Biome[] arr) {
        this.biomes = arr;
    }
    
    public Biome[] getBiomes() {
        return this.biomes;
    }
    
    public void setUnsaved(final boolean boolean1) {
        this.isDirty = boolean1;
    }
    
    public boolean isUnsaved() {
        return this.isDirty;
    }
    
    public ChunkStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final ChunkStatus bxm) {
        this.status = bxm;
        this.setUnsaved(true);
    }
    
    public LevelChunkSection[] getSections() {
        return this.sections;
    }
    
    @Nullable
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }
    
    public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
        return (Collection<Map.Entry<Heightmap.Types, Heightmap>>)Collections.unmodifiableSet(this.heightmaps.entrySet());
    }
    
    public void setHeightmap(final Heightmap.Types a, final long[] arr) {
        this.getOrCreateHeightmapUnprimed(a).setRawData(arr);
    }
    
    public Heightmap getOrCreateHeightmapUnprimed(final Heightmap.Types a) {
        return (Heightmap)this.heightmaps.computeIfAbsent(a, a -> new Heightmap(this, a));
    }
    
    public int getHeight(final Heightmap.Types a, final int integer2, final int integer3) {
        Heightmap bza5 = (Heightmap)this.heightmaps.get(a);
        if (bza5 == null) {
            Heightmap.primeHeightmaps(this, (Set<Heightmap.Types>)EnumSet.of((Enum)a));
            bza5 = (Heightmap)this.heightmaps.get(a);
        }
        return bza5.getFirstAvailable(integer2 & 0xF, integer3 & 0xF) - 1;
    }
    
    public ChunkPos getPos() {
        return this.chunkPos;
    }
    
    public void setLastSaveTime(final long long1) {
    }
    
    @Nullable
    public StructureStart getStartForFeature(final String string) {
        return (StructureStart)this.structureStarts.get(string);
    }
    
    public void setStartForFeature(final String string, final StructureStart ciw) {
        this.structureStarts.put(string, ciw);
        this.isDirty = true;
    }
    
    public Map<String, StructureStart> getAllStarts() {
        return (Map<String, StructureStart>)Collections.unmodifiableMap((Map)this.structureStarts);
    }
    
    public void setAllStarts(final Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll((Map)map);
        this.isDirty = true;
    }
    
    public LongSet getReferencesForFeature(final String string) {
        return (LongSet)this.structuresRefences.computeIfAbsent(string, string -> new LongOpenHashSet());
    }
    
    public void addReferenceForFeature(final String string, final long long2) {
        ((LongSet)this.structuresRefences.computeIfAbsent(string, string -> new LongOpenHashSet())).add(long2);
        this.isDirty = true;
    }
    
    public Map<String, LongSet> getAllReferences() {
        return (Map<String, LongSet>)Collections.unmodifiableMap((Map)this.structuresRefences);
    }
    
    public void setAllReferences(final Map<String, LongSet> map) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll((Map)map);
        this.isDirty = true;
    }
    
    public static short packOffsetCoordinates(final BlockPos ew) {
        final int integer2 = ew.getX();
        final int integer3 = ew.getY();
        final int integer4 = ew.getZ();
        final int integer5 = integer2 & 0xF;
        final int integer6 = integer3 & 0xF;
        final int integer7 = integer4 & 0xF;
        return (short)(integer5 | integer6 << 4 | integer7 << 8);
    }
    
    public static BlockPos unpackOffsetCoordinates(final short short1, final int integer, final ChunkPos bhd) {
        final int integer2 = (short1 & 0xF) + (bhd.x << 4);
        final int integer3 = (short1 >>> 4 & 0xF) + (integer << 4);
        final int integer4 = (short1 >>> 8 & 0xF) + (bhd.z << 4);
        return new BlockPos(integer2, integer3, integer4);
    }
    
    public void markPosForPostprocessing(final BlockPos ew) {
        if (!Level.isOutsideBuildHeight(ew)) {
            ChunkAccess.getOrCreateOffsetList(this.postProcessing, ew.getY() >> 4).add(packOffsetCoordinates(ew));
        }
    }
    
    public ShortList[] getPostProcessing() {
        return this.postProcessing;
    }
    
    public void addPackedPostProcess(final short short1, final int integer) {
        ChunkAccess.getOrCreateOffsetList(this.postProcessing, integer).add(short1);
    }
    
    public ProtoTickList<Block> getBlockTicks() {
        return this.blockTicks;
    }
    
    public ProtoTickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }
    
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }
    
    public void setInhabitedTime(final long long1) {
        this.inhabitedTime = long1;
    }
    
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }
    
    public void setBlockEntityNbt(final CompoundTag id) {
        this.blockEntityNbts.put(new BlockPos(id.getInt("x"), id.getInt("y"), id.getInt("z")), id);
    }
    
    public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
        return (Map<BlockPos, CompoundTag>)Collections.unmodifiableMap((Map)this.blockEntityNbts);
    }
    
    public CompoundTag getBlockEntityNbt(final BlockPos ew) {
        return (CompoundTag)this.blockEntityNbts.get(ew);
    }
    
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(final BlockPos ew) {
        final BlockEntity btw3 = this.getBlockEntity(ew);
        if (btw3 != null) {
            return btw3.save(new CompoundTag());
        }
        return (CompoundTag)this.blockEntityNbts.get(ew);
    }
    
    public void removeBlockEntity(final BlockPos ew) {
        this.blockEntities.remove(ew);
        this.blockEntityNbts.remove(ew);
    }
    
    public BitSet getCarvingMask(final GenerationStep.Carving a) {
        return (BitSet)this.carvingMasks.computeIfAbsent(a, a -> new BitSet(65536));
    }
    
    public void setCarvingMask(final GenerationStep.Carving a, final BitSet bitSet) {
        this.carvingMasks.put(a, bitSet);
    }
    
    public void setLightEngine(final LevelLightEngine clb) {
        this.lightEngine = clb;
    }
    
    public boolean isLightCorrect() {
        return this.isLightCorrect;
    }
    
    public void setLightCorrect(final boolean boolean1) {
        this.isLightCorrect = boolean1;
        this.setUnsaved(true);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
