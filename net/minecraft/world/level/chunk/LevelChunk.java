package net.minecraft.world.level.chunk;

import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkTickList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.world.level.LevelAccessor;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Collections;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import java.util.function.Predicate;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Collection;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Iterator;
import java.util.function.Function;
import net.minecraft.world.entity.EntityType;
import com.google.common.collect.Maps;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.ChunkPos;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkHolder;
import java.util.function.Supplier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.TickList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import java.util.Map;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.Logger;

public class LevelChunk implements ChunkAccess {
    private static final Logger LOGGER;
    public static final LevelChunkSection EMPTY_SECTION;
    private final LevelChunkSection[] sections;
    private final Biome[] biomes;
    private final Map<BlockPos, CompoundTag> pendingBlockEntities;
    private boolean loaded;
    private final Level level;
    private final Map<Heightmap.Types, Heightmap> heightmaps;
    private final UpgradeData upgradeData;
    private final Map<BlockPos, BlockEntity> blockEntities;
    private final ClassInstanceMultiMap<Entity>[] entitySections;
    private final Map<String, StructureStart> structureStarts;
    private final Map<String, LongSet> structuresRefences;
    private final ShortList[] postProcessing;
    private TickList<Block> blockTicks;
    private TickList<Fluid> liquidTicks;
    private boolean lastSaveHadEntities;
    private long lastSaveTime;
    private volatile boolean unsaved;
    private long inhabitedTime;
    @Nullable
    private Supplier<ChunkHolder.FullChunkStatus> fullStatus;
    @Nullable
    private Consumer<LevelChunk> postLoad;
    private final ChunkPos chunkPos;
    private volatile boolean isLightCorrect;
    
    public LevelChunk(final Level bhr, final ChunkPos bhd, final Biome[] arr) {
        this(bhr, bhd, arr, UpgradeData.EMPTY, EmptyTickList.empty(), EmptyTickList.empty(), 0L, null, null);
    }
    
    public LevelChunk(final Level bhr, final ChunkPos bhd, final Biome[] arr, final UpgradeData byd, final TickList<Block> big5, final TickList<Fluid> big6, final long long7, @Nullable final LevelChunkSection[] arr, @Nullable final Consumer<LevelChunk> consumer) {
        this.sections = new LevelChunkSection[16];
        this.pendingBlockEntities = (Map<BlockPos, CompoundTag>)Maps.newHashMap();
        this.heightmaps = (Map<Heightmap.Types, Heightmap>)Maps.newEnumMap((Class)Heightmap.Types.class);
        this.blockEntities = (Map<BlockPos, BlockEntity>)Maps.newHashMap();
        this.structureStarts = (Map<String, StructureStart>)Maps.newHashMap();
        this.structuresRefences = (Map<String, LongSet>)Maps.newHashMap();
        this.postProcessing = new ShortList[16];
        this.entitySections = new ClassInstanceMultiMap[16];
        this.level = bhr;
        this.chunkPos = bhd;
        this.upgradeData = byd;
        for (final Heightmap.Types a15 : Heightmap.Types.values()) {
            if (ChunkStatus.FULL.heightmapsAfter().contains(a15)) {
                this.heightmaps.put(a15, new Heightmap(this, a15));
            }
        }
        for (int integer12 = 0; integer12 < this.entitySections.length; ++integer12) {
            this.entitySections[integer12] = new ClassInstanceMultiMap<Entity>(Entity.class);
        }
        this.biomes = arr;
        this.blockTicks = big5;
        this.liquidTicks = big6;
        this.inhabitedTime = long7;
        this.postLoad = consumer;
        if (arr != null) {
            if (this.sections.length == arr.length) {
                System.arraycopy(arr, 0, this.sections, 0, this.sections.length);
            }
            else {
                LevelChunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", arr.length, this.sections.length);
            }
        }
    }
    
    public LevelChunk(final Level bhr, final ProtoChunk byb) {
        this(bhr, byb.getPos(), byb.getBiomes(), byb.getUpgradeData(), byb.getBlockTicks(), byb.getLiquidTicks(), byb.getInhabitedTime(), byb.getSections(), null);
        for (final CompoundTag id5 : byb.getEntities()) {
            EntityType.loadEntityRecursive(id5, bhr, (Function<Entity, Entity>)(aio -> {
                this.addEntity(aio);
                return aio;
            }));
        }
        for (final BlockEntity btw5 : byb.getBlockEntities().values()) {
            this.addBlockEntity(btw5);
        }
        this.pendingBlockEntities.putAll((Map)byb.getBlockEntityNbts());
        for (int integer4 = 0; integer4 < byb.getPostProcessing().length; ++integer4) {
            this.postProcessing[integer4] = byb.getPostProcessing()[integer4];
        }
        this.setAllStarts(byb.getAllStarts());
        this.setAllReferences(byb.getAllReferences());
        for (final Map.Entry<Heightmap.Types, Heightmap> entry5 : byb.getHeightmaps()) {
            if (ChunkStatus.FULL.heightmapsAfter().contains(entry5.getKey())) {
                this.getOrCreateHeightmapUnprimed((Heightmap.Types)entry5.getKey()).setRawData(((Heightmap)entry5.getValue()).getRawData());
            }
        }
        this.setLightCorrect(byb.isLightCorrect());
        this.unsaved = true;
    }
    
    public Heightmap getOrCreateHeightmapUnprimed(final Heightmap.Types a) {
        return (Heightmap)this.heightmaps.computeIfAbsent(a, a -> new Heightmap(this, a));
    }
    
    public Set<BlockPos> getBlockEntitiesPos() {
        final Set<BlockPos> set2 = (Set<BlockPos>)Sets.newHashSet((Iterable)this.pendingBlockEntities.keySet());
        set2.addAll((Collection)this.blockEntities.keySet());
        return set2;
    }
    
    public LevelChunkSection[] getSections() {
        return this.sections;
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        final int integer3 = ew.getX();
        final int integer4 = ew.getY();
        final int integer5 = ew.getZ();
        if (this.level.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
            BlockState bvt6 = null;
            if (integer4 == 60) {
                bvt6 = Blocks.BARRIER.defaultBlockState();
            }
            if (integer4 == 70) {
                bvt6 = DebugLevelSource.getBlockStateFor(integer3, integer5);
            }
            return (bvt6 == null) ? Blocks.AIR.defaultBlockState() : bvt6;
        }
        try {
            if (integer4 >= 0 && integer4 >> 4 < this.sections.length) {
                final LevelChunkSection bxu6 = this.sections[integer4 >> 4];
                if (!LevelChunkSection.isEmpty(bxu6)) {
                    return bxu6.getBlockState(integer3 & 0xF, integer4 & 0xF, integer5 & 0xF);
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        catch (Throwable throwable6) {
            final CrashReport d7 = CrashReport.forThrowable(throwable6, "Getting block state");
            final CrashReportCategory e8 = d7.addCategory("Block being got");
            e8.setDetail("Location", (CrashReportDetail<String>)(() -> CrashReportCategory.formatLocation(integer3, integer4, integer5)));
            throw new ReportedException(d7);
        }
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        return this.getFluidState(ew.getX(), ew.getY(), ew.getZ());
    }
    
    public FluidState getFluidState(final int integer1, final int integer2, final int integer3) {
        try {
            if (integer2 >= 0 && integer2 >> 4 < this.sections.length) {
                final LevelChunkSection bxu5 = this.sections[integer2 >> 4];
                if (!LevelChunkSection.isEmpty(bxu5)) {
                    return bxu5.getFluidState(integer1 & 0xF, integer2 & 0xF, integer3 & 0xF);
                }
            }
            return Fluids.EMPTY.defaultFluidState();
        }
        catch (Throwable throwable5) {
            final CrashReport d6 = CrashReport.forThrowable(throwable5, "Getting fluid state");
            final CrashReportCategory e7 = d6.addCategory("Block being got");
            e7.setDetail("Location", (CrashReportDetail<String>)(() -> CrashReportCategory.formatLocation(integer1, integer2, integer3)));
            throw new ReportedException(d6);
        }
    }
    
    @Nullable
    public BlockState setBlockState(final BlockPos ew, final BlockState bvt, final boolean boolean3) {
        final int integer5 = ew.getX() & 0xF;
        final int integer6 = ew.getY();
        final int integer7 = ew.getZ() & 0xF;
        LevelChunkSection bxu8 = this.sections[integer6 >> 4];
        if (bxu8 == LevelChunk.EMPTY_SECTION) {
            if (bvt.isAir()) {
                return null;
            }
            bxu8 = new LevelChunkSection(integer6 >> 4 << 4);
            this.sections[integer6 >> 4] = bxu8;
        }
        final boolean boolean4 = bxu8.isEmpty();
        final BlockState bvt2 = bxu8.setBlockState(integer5, integer6 & 0xF, integer7, bvt);
        if (bvt2 == bvt) {
            return null;
        }
        final Block bmv11 = bvt.getBlock();
        final Block bmv12 = bvt2.getBlock();
        ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update(integer5, integer6, integer7, bvt);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(integer5, integer6, integer7, bvt);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update(integer5, integer6, integer7, bvt);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update(integer5, integer6, integer7, bvt);
        final boolean boolean5 = bxu8.isEmpty();
        if (boolean4 != boolean5) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(ew, boolean5);
        }
        if (!this.level.isClientSide) {
            bvt2.onRemove(this.level, ew, bvt, boolean3);
        }
        else if (bmv12 != bmv11 && bmv12 instanceof EntityBlock) {
            this.level.removeBlockEntity(ew);
        }
        if (bxu8.getBlockState(integer5, integer6 & 0xF, integer7).getBlock() != bmv11) {
            return null;
        }
        if (bmv12 instanceof EntityBlock) {
            final BlockEntity btw14 = this.getBlockEntity(ew, EntityCreationType.CHECK);
            if (btw14 != null) {
                btw14.clearCache();
            }
        }
        if (!this.level.isClientSide) {
            bvt.onPlace(this.level, ew, bvt2, boolean3);
        }
        if (bmv11 instanceof EntityBlock) {
            BlockEntity btw14 = this.getBlockEntity(ew, EntityCreationType.CHECK);
            if (btw14 == null) {
                btw14 = ((EntityBlock)bmv11).newBlockEntity(this.level);
                this.level.setBlockEntity(ew, btw14);
            }
            else {
                btw14.clearCache();
            }
        }
        this.unsaved = true;
        return bvt2;
    }
    
    @Nullable
    public LevelLightEngine getLightEngine() {
        return this.level.getChunkSource().getLightEngine();
    }
    
    public int getRawBrightness(final BlockPos ew, final int integer) {
        return this.getRawBrightness(ew, integer, this.level.getDimension().isHasSkyLight());
    }
    
    public void addEntity(final Entity aio) {
        this.lastSaveHadEntities = true;
        final int integer3 = Mth.floor(aio.x / 16.0);
        final int integer4 = Mth.floor(aio.z / 16.0);
        if (integer3 != this.chunkPos.x || integer4 != this.chunkPos.z) {
            LevelChunk.LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", integer3, integer4, this.chunkPos.x, this.chunkPos.z, aio);
            aio.removed = true;
        }
        int integer5 = Mth.floor(aio.y / 16.0);
        if (integer5 < 0) {
            integer5 = 0;
        }
        if (integer5 >= this.entitySections.length) {
            integer5 = this.entitySections.length - 1;
        }
        aio.inChunk = true;
        aio.xChunk = this.chunkPos.x;
        aio.yChunk = integer5;
        aio.zChunk = this.chunkPos.z;
        this.entitySections[integer5].add(aio);
    }
    
    public void setHeightmap(final Heightmap.Types a, final long[] arr) {
        ((Heightmap)this.heightmaps.get(a)).setRawData(arr);
    }
    
    public void removeEntity(final Entity aio) {
        this.removeEntity(aio, aio.yChunk);
    }
    
    public void removeEntity(final Entity aio, int integer) {
        if (integer < 0) {
            integer = 0;
        }
        if (integer >= this.entitySections.length) {
            integer = this.entitySections.length - 1;
        }
        this.entitySections[integer].remove(aio);
    }
    
    public int getHeight(final Heightmap.Types a, final int integer2, final int integer3) {
        return ((Heightmap)this.heightmaps.get(a)).getFirstAvailable(integer2 & 0xF, integer3 & 0xF) - 1;
    }
    
    @Nullable
    private BlockEntity createBlockEntity(final BlockPos ew) {
        final BlockState bvt3 = this.getBlockState(ew);
        final Block bmv4 = bvt3.getBlock();
        if (!bmv4.isEntityBlock()) {
            return null;
        }
        return ((EntityBlock)bmv4).newBlockEntity(this.level);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        return this.getBlockEntity(ew, EntityCreationType.CHECK);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew, final EntityCreationType a) {
        BlockEntity btw4 = (BlockEntity)this.blockEntities.get(ew);
        if (btw4 == null) {
            final CompoundTag id5 = (CompoundTag)this.pendingBlockEntities.remove(ew);
            if (id5 != null) {
                final BlockEntity btw5 = this.promotePendingBlockEntity(ew, id5);
                if (btw5 != null) {
                    return btw5;
                }
            }
        }
        if (btw4 == null) {
            if (a == EntityCreationType.IMMEDIATE) {
                btw4 = this.createBlockEntity(ew);
                this.level.setBlockEntity(ew, btw4);
            }
        }
        else if (btw4.isRemoved()) {
            this.blockEntities.remove(ew);
            return null;
        }
        return btw4;
    }
    
    public void addBlockEntity(final BlockEntity btw) {
        this.setBlockEntity(btw.getBlockPos(), btw);
        if (this.loaded || this.level.isClientSide()) {
            this.level.setBlockEntity(btw.getBlockPos(), btw);
        }
    }
    
    public void setBlockEntity(final BlockPos ew, final BlockEntity btw) {
        if (!(this.getBlockState(ew).getBlock() instanceof EntityBlock)) {
            return;
        }
        btw.setLevel(this.level);
        btw.setPosition(ew);
        btw.clearRemoved();
        final BlockEntity btw2 = (BlockEntity)this.blockEntities.put(ew.immutable(), btw);
        if (btw2 != null && btw2 != btw) {
            btw2.setRemoved();
        }
    }
    
    public void setBlockEntityNbt(final CompoundTag id) {
        this.pendingBlockEntities.put(new BlockPos(id.getInt("x"), id.getInt("y"), id.getInt("z")), id);
    }
    
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(final BlockPos ew) {
        final BlockEntity btw3 = this.getBlockEntity(ew);
        if (btw3 != null && !btw3.isRemoved()) {
            final CompoundTag id4 = btw3.save(new CompoundTag());
            id4.putBoolean("keepPacked", false);
            return id4;
        }
        CompoundTag id4 = (CompoundTag)this.pendingBlockEntities.get(ew);
        if (id4 != null) {
            id4 = id4.copy();
            id4.putBoolean("keepPacked", true);
        }
        return id4;
    }
    
    public void removeBlockEntity(final BlockPos ew) {
        if (this.loaded || this.level.isClientSide()) {
            final BlockEntity btw3 = (BlockEntity)this.blockEntities.remove(ew);
            if (btw3 != null) {
                btw3.setRemoved();
            }
        }
    }
    
    public void runPostLoad() {
        if (this.postLoad != null) {
            this.postLoad.accept(this);
            this.postLoad = null;
        }
    }
    
    public void markUnsaved() {
        this.unsaved = true;
    }
    
    public void getEntities(@Nullable final Entity aio, final AABB csc, final List<Entity> list, @Nullable final Predicate<? super Entity> predicate) {
        int integer6 = Mth.floor((csc.minY - 2.0) / 16.0);
        int integer7 = Mth.floor((csc.maxY + 2.0) / 16.0);
        integer6 = Mth.clamp(integer6, 0, this.entitySections.length - 1);
        integer7 = Mth.clamp(integer7, 0, this.entitySections.length - 1);
        for (int integer8 = integer6; integer8 <= integer7; ++integer8) {
            if (!this.entitySections[integer8].isEmpty()) {
                for (final Entity aio2 : this.entitySections[integer8]) {
                    if (aio2.getBoundingBox().intersects(csc) && aio2 != aio) {
                        if (predicate == null || predicate.test(aio2)) {
                            list.add(aio2);
                        }
                        if (!(aio2 instanceof EnderDragon)) {
                            continue;
                        }
                        for (final EnderDragonPart asn14 : ((EnderDragon)aio2).getSubEntities()) {
                            if (asn14 != aio && asn14.getBoundingBox().intersects(csc) && (predicate == null || predicate.test(asn14))) {
                                list.add(asn14);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void getEntities(@Nullable final EntityType<?> ais, final AABB csc, final List<Entity> list, final Predicate<? super Entity> predicate) {
        int integer6 = Mth.floor((csc.minY - 2.0) / 16.0);
        int integer7 = Mth.floor((csc.maxY + 2.0) / 16.0);
        integer6 = Mth.clamp(integer6, 0, this.entitySections.length - 1);
        integer7 = Mth.clamp(integer7, 0, this.entitySections.length - 1);
        for (int integer8 = integer6; integer8 <= integer7; ++integer8) {
            for (final Entity aio10 : this.entitySections[integer8].<Entity>find(Entity.class)) {
                if (ais != null && aio10.getType() != ais) {
                    continue;
                }
                if (!aio10.getBoundingBox().intersects(csc) || !predicate.test(aio10)) {
                    continue;
                }
                list.add(aio10);
            }
        }
    }
    
    public <T extends Entity> void getEntitiesOfClass(final Class<? extends T> class1, final AABB csc, final List<T> list, @Nullable final Predicate<? super T> predicate) {
        int integer6 = Mth.floor((csc.minY - 2.0) / 16.0);
        int integer7 = Mth.floor((csc.maxY + 2.0) / 16.0);
        integer6 = Mth.clamp(integer6, 0, this.entitySections.length - 1);
        integer7 = Mth.clamp(integer7, 0, this.entitySections.length - 1);
        for (int integer8 = integer6; integer8 <= integer7; ++integer8) {
            for (final T aio10 : this.entitySections[integer8].find(class1)) {
                if (aio10.getBoundingBox().intersects(csc) && (predicate == null || predicate.test(aio10))) {
                    list.add(aio10);
                }
            }
        }
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public ChunkPos getPos() {
        return this.chunkPos;
    }
    
    public void replaceWithPacketData(final FriendlyByteBuf je, final CompoundTag id, final int integer, final boolean boolean4) {
        final Predicate<BlockPos> predicate6 = (Predicate<BlockPos>)(boolean4 ? (ew -> true) : (ew -> (integer & 1 << (ew.getY() >> 4)) != 0x0));
        Sets.newHashSet((Iterable)this.blockEntities.keySet()).stream().filter((Predicate)predicate6).forEach(this.level::removeBlockEntity);
        for (int integer2 = 0; integer2 < this.sections.length; ++integer2) {
            LevelChunkSection bxu8 = this.sections[integer2];
            if ((integer & 1 << integer2) == 0x0) {
                if (boolean4 && bxu8 != LevelChunk.EMPTY_SECTION) {
                    this.sections[integer2] = LevelChunk.EMPTY_SECTION;
                }
            }
            else {
                if (bxu8 == LevelChunk.EMPTY_SECTION) {
                    bxu8 = new LevelChunkSection(integer2 << 4);
                    this.sections[integer2] = bxu8;
                }
                bxu8.read(je);
            }
        }
        if (boolean4) {
            for (int integer2 = 0; integer2 < this.biomes.length; ++integer2) {
                this.biomes[integer2] = Registry.BIOME.byId(je.readInt());
            }
        }
        for (final Heightmap.Types a10 : Heightmap.Types.values()) {
            final String string11 = a10.getSerializationKey();
            if (id.contains(string11, 12)) {
                this.setHeightmap(a10, id.getLongArray(string11));
            }
        }
        for (final BlockEntity btw8 : this.blockEntities.values()) {
            btw8.clearCache();
        }
    }
    
    public Biome[] getBiomes() {
        return this.biomes;
    }
    
    public void setLoaded(final boolean boolean1) {
        this.loaded = boolean1;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
        return (Collection<Map.Entry<Heightmap.Types, Heightmap>>)Collections.unmodifiableSet(this.heightmaps.entrySet());
    }
    
    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }
    
    public ClassInstanceMultiMap<Entity>[] getEntitySections() {
        return this.entitySections;
    }
    
    public CompoundTag getBlockEntityNbt(final BlockPos ew) {
        return (CompoundTag)this.pendingBlockEntities.get(ew);
    }
    
    public Stream<BlockPos> getLights() {
        return (Stream<BlockPos>)StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), 0, this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), 255, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter(ew -> this.getBlockState(ew).getLightEmission() != 0);
    }
    
    public TickList<Block> getBlockTicks() {
        return this.blockTicks;
    }
    
    public TickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }
    
    public void setUnsaved(final boolean boolean1) {
        this.unsaved = boolean1;
    }
    
    public boolean isUnsaved() {
        return this.unsaved || (this.lastSaveHadEntities && this.level.getGameTime() != this.lastSaveTime);
    }
    
    public void setLastSaveHadEntities(final boolean boolean1) {
        this.lastSaveHadEntities = boolean1;
    }
    
    public void setLastSaveTime(final long long1) {
        this.lastSaveTime = long1;
    }
    
    @Nullable
    public StructureStart getStartForFeature(final String string) {
        return (StructureStart)this.structureStarts.get(string);
    }
    
    public void setStartForFeature(final String string, final StructureStart ciw) {
        this.structureStarts.put(string, ciw);
    }
    
    public Map<String, StructureStart> getAllStarts() {
        return this.structureStarts;
    }
    
    public void setAllStarts(final Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll((Map)map);
    }
    
    public LongSet getReferencesForFeature(final String string) {
        return (LongSet)this.structuresRefences.computeIfAbsent(string, string -> new LongOpenHashSet());
    }
    
    public void addReferenceForFeature(final String string, final long long2) {
        ((LongSet)this.structuresRefences.computeIfAbsent(string, string -> new LongOpenHashSet())).add(long2);
    }
    
    public Map<String, LongSet> getAllReferences() {
        return this.structuresRefences;
    }
    
    public void setAllReferences(final Map<String, LongSet> map) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll((Map)map);
    }
    
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }
    
    public void setInhabitedTime(final long long1) {
        this.inhabitedTime = long1;
    }
    
    public void postProcessGeneration() {
        final ChunkPos bhd2 = this.getPos();
        for (int integer3 = 0; integer3 < this.postProcessing.length; ++integer3) {
            if (this.postProcessing[integer3] != null) {
                for (final Short short5 : this.postProcessing[integer3]) {
                    final BlockPos ew6 = ProtoChunk.unpackOffsetCoordinates(short5, integer3, bhd2);
                    final BlockState bvt7 = this.getBlockState(ew6);
                    final BlockState bvt8 = Block.updateFromNeighbourShapes(bvt7, this.level, ew6);
                    this.level.setBlock(ew6, bvt8, 20);
                }
                this.postProcessing[integer3].clear();
            }
        }
        this.unpackTicks();
        for (final BlockPos ew7 : Sets.newHashSet((Iterable)this.pendingBlockEntities.keySet())) {
            this.getBlockEntity(ew7);
        }
        this.pendingBlockEntities.clear();
        this.upgradeData.upgrade(this);
    }
    
    @Nullable
    private BlockEntity promotePendingBlockEntity(final BlockPos ew, final CompoundTag id) {
        BlockEntity btw4;
        if ("DUMMY".equals(id.getString("id"))) {
            final Block bmv5 = this.getBlockState(ew).getBlock();
            if (bmv5 instanceof EntityBlock) {
                btw4 = ((EntityBlock)bmv5).newBlockEntity(this.level);
            }
            else {
                btw4 = null;
                LevelChunk.LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", ew, this.getBlockState(ew));
            }
        }
        else {
            btw4 = BlockEntity.loadStatic(id);
        }
        if (btw4 != null) {
            btw4.setPosition(ew);
            this.addBlockEntity(btw4);
        }
        else {
            LevelChunk.LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(ew), ew);
        }
        return btw4;
    }
    
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }
    
    public ShortList[] getPostProcessing() {
        return this.postProcessing;
    }
    
    public void unpackTicks() {
        if (this.blockTicks instanceof ProtoTickList) {
            ((ProtoTickList)this.blockTicks).copyOut(this.level.getBlockTicks(), ew -> this.getBlockState(ew).getBlock());
            this.blockTicks = EmptyTickList.empty();
        }
        else if (this.blockTicks instanceof ChunkTickList) {
            this.level.getBlockTicks().addAll(((ChunkTickList)this.blockTicks).ticks());
            this.blockTicks = EmptyTickList.empty();
        }
        if (this.liquidTicks instanceof ProtoTickList) {
            ((ProtoTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks(), ew -> this.getFluidState(ew).getType());
            this.liquidTicks = EmptyTickList.empty();
        }
        else if (this.liquidTicks instanceof ChunkTickList) {
            this.level.getLiquidTicks().addAll(((ChunkTickList)this.liquidTicks).ticks());
            this.liquidTicks = EmptyTickList.empty();
        }
    }
    
    public void packTicks(final ServerLevel vk) {
        if (this.blockTicks == EmptyTickList.empty()) {
            this.blockTicks = new ChunkTickList<Block>((java.util.function.Function<Block, ResourceLocation>)Registry.BLOCK::getKey, vk.getBlockTicks().fetchTicksInChunk(this.chunkPos, true, false));
            this.setUnsaved(true);
        }
        if (this.liquidTicks == EmptyTickList.empty()) {
            this.liquidTicks = new ChunkTickList<Fluid>((java.util.function.Function<Fluid, ResourceLocation>)Registry.FLUID::getKey, vk.getLiquidTicks().fetchTicksInChunk(this.chunkPos, true, false));
            this.setUnsaved(true);
        }
    }
    
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }
    
    public ChunkHolder.FullChunkStatus getFullStatus() {
        if (this.fullStatus == null) {
            return ChunkHolder.FullChunkStatus.BORDER;
        }
        return (ChunkHolder.FullChunkStatus)this.fullStatus.get();
    }
    
    public void setFullStatus(final Supplier<ChunkHolder.FullChunkStatus> supplier) {
        this.fullStatus = supplier;
    }
    
    public void setLightEngine(final LevelLightEngine clb) {
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
        EMPTY_SECTION = null;
    }
    
    public enum EntityCreationType {
        IMMEDIATE, 
        QUEUED, 
        CHECK;
    }
}
