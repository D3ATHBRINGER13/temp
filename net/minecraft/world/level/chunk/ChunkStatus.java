package net.minecraft.world.level.chunk;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.GenerationStep;
import java.util.Set;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.server.level.ServerLevel;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkHolder;
import com.mojang.datafixers.util.Either;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.EnumSet;

public class ChunkStatus {
    private static final EnumSet<Heightmap.Types> PRE_FEATURES;
    private static final EnumSet<Heightmap.Types> POST_FEATURES;
    private static final LoadingTask PASSTHROUGH_LOAD_TASK;
    public static final ChunkStatus EMPTY;
    public static final ChunkStatus STRUCTURE_STARTS;
    public static final ChunkStatus STRUCTURE_REFERENCES;
    public static final ChunkStatus BIOMES;
    public static final ChunkStatus NOISE;
    public static final ChunkStatus SURFACE;
    public static final ChunkStatus CARVERS;
    public static final ChunkStatus LIQUID_CARVERS;
    public static final ChunkStatus FEATURES;
    public static final ChunkStatus LIGHT;
    public static final ChunkStatus SPAWN;
    public static final ChunkStatus HEIGHTMAPS;
    public static final ChunkStatus FULL;
    private static final List<ChunkStatus> STATUS_BY_RANGE;
    private static final IntList RANGE_BY_STATUS;
    private final String name;
    private final int index;
    private final ChunkStatus parent;
    private final GenerationTask generationTask;
    private final LoadingTask loadingTask;
    private final int range;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Types> heightmapsAfter;
    
    private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(final ChunkStatus bxm, final ThreadedLevelLightEngine vn, final ChunkAccess bxh) {
        final boolean boolean4 = isLighted(bxm, bxh);
        if (!bxh.getStatus().isOrAfter(bxm)) {
            ((ProtoChunk)bxh).setStatus(bxm);
        }
        return (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>)vn.lightChunk(bxh, boolean4).thenApply(Either::left);
    }
    
    private static ChunkStatus registerSimple(final String string, @Nullable final ChunkStatus bxm, final int integer, final EnumSet<Heightmap.Types> enumSet, final ChunkType a, final SimpleGenerationTask d) {
        return register(string, bxm, integer, enumSet, a, d);
    }
    
    private static ChunkStatus register(final String string, @Nullable final ChunkStatus bxm, final int integer, final EnumSet<Heightmap.Types> enumSet, final ChunkType a, final GenerationTask b) {
        return register(string, bxm, integer, enumSet, a, b, ChunkStatus.PASSTHROUGH_LOAD_TASK);
    }
    
    private static ChunkStatus register(final String string, @Nullable final ChunkStatus bxm, final int integer, final EnumSet<Heightmap.Types> enumSet, final ChunkType a, final GenerationTask b, final LoadingTask c) {
        return Registry.<ChunkStatus>register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, bxm, integer, enumSet, a, b, c));
    }
    
    public static List<ChunkStatus> getStatusList() {
        final List<ChunkStatus> list1 = (List<ChunkStatus>)Lists.newArrayList();
        ChunkStatus bxm2;
        for (bxm2 = ChunkStatus.FULL; bxm2.getParent() != bxm2; bxm2 = bxm2.getParent()) {
            list1.add(bxm2);
        }
        list1.add(bxm2);
        Collections.reverse((List)list1);
        return list1;
    }
    
    private static boolean isLighted(final ChunkStatus bxm, final ChunkAccess bxh) {
        return bxh.getStatus().isOrAfter(bxm) && bxh.isLightCorrect();
    }
    
    public static ChunkStatus getStatus(final int integer) {
        if (integer >= ChunkStatus.STATUS_BY_RANGE.size()) {
            return ChunkStatus.EMPTY;
        }
        if (integer < 0) {
            return ChunkStatus.FULL;
        }
        return (ChunkStatus)ChunkStatus.STATUS_BY_RANGE.get(integer);
    }
    
    public static int maxDistance() {
        return ChunkStatus.STATUS_BY_RANGE.size();
    }
    
    public static int getDistance(final ChunkStatus bxm) {
        return ChunkStatus.RANGE_BY_STATUS.getInt(bxm.getIndex());
    }
    
    ChunkStatus(final String string, @Nullable final ChunkStatus bxm, final int integer, final EnumSet<Heightmap.Types> enumSet, final ChunkType a, final GenerationTask b, final LoadingTask c) {
        this.name = string;
        this.parent = ((bxm == null) ? this : bxm);
        this.generationTask = b;
        this.loadingTask = c;
        this.range = integer;
        this.chunkType = a;
        this.heightmapsAfter = enumSet;
        this.index = ((bxm == null) ? 0 : (bxm.getIndex() + 1));
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ChunkStatus getParent() {
        return this.parent;
    }
    
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generate(final ServerLevel vk, final ChunkGenerator<?> bxi, final StructureManager cjp, final ThreadedLevelLightEngine vn, final Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, final List<ChunkAccess> list) {
        return this.generationTask.doWork(this, vk, bxi, cjp, vn, function, list, (ChunkAccess)list.get(list.size() / 2));
    }
    
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> load(final ServerLevel vk, final StructureManager cjp, final ThreadedLevelLightEngine vn, final Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, final ChunkAccess bxh) {
        return this.loadingTask.doWork(this, vk, cjp, vn, function, bxh);
    }
    
    public int getRange() {
        return this.range;
    }
    
    public ChunkType getChunkType() {
        return this.chunkType;
    }
    
    public static ChunkStatus byName(final String string) {
        return Registry.CHUNK_STATUS.get(ResourceLocation.tryParse(string));
    }
    
    public EnumSet<Heightmap.Types> heightmapsAfter() {
        return this.heightmapsAfter;
    }
    
    public boolean isOrAfter(final ChunkStatus bxm) {
        return this.getIndex() >= bxm.getIndex();
    }
    
    public String toString() {
        return Registry.CHUNK_STATUS.getKey(this).toString();
    }
    
    static {
        PRE_FEATURES = EnumSet.of((Enum)Heightmap.Types.OCEAN_FLOOR_WG, (Enum)Heightmap.Types.WORLD_SURFACE_WG);
        POST_FEATURES = EnumSet.of((Enum)Heightmap.Types.OCEAN_FLOOR, (Enum)Heightmap.Types.WORLD_SURFACE, (Enum)Heightmap.Types.MOTION_BLOCKING, (Enum)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        PASSTHROUGH_LOAD_TASK = ((bxm, vk, cjp, vn, function, bxh) -> {
            if (bxh instanceof ProtoChunk && !bxh.getStatus().isOrAfter(bxm)) {
                bxh.setStatus(bxm);
            }
            return (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>)CompletableFuture.completedFuture(Either.left((Object)bxh));
        });
        EMPTY = registerSimple("empty", (ChunkStatus)null, -1, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> {});
        STRUCTURE_STARTS = register("structure_starts", ChunkStatus.EMPTY, 0, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (bxm, vk, bxi, cjp, vn, function, list, bxh) -> {
            if (!bxh.getStatus().isOrAfter(bxm)) {
                if (vk.getLevelData().isGenerateMapFeatures()) {
                    bxi.createStructures(bxh, bxi, cjp);
                }
                if (bxh instanceof ProtoChunk) {
                    bxh.setStatus(bxm);
                }
            }
            return (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>)CompletableFuture.completedFuture(Either.left((Object)bxh));
        });
        STRUCTURE_REFERENCES = registerSimple("structure_references", ChunkStatus.STRUCTURE_STARTS, 8, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.createReferences(new WorldGenRegion(vk, list), bxh));
        BIOMES = registerSimple("biomes", ChunkStatus.STRUCTURE_REFERENCES, 0, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.createBiomes(bxh));
        NOISE = registerSimple("noise", ChunkStatus.BIOMES, 8, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.fillFromNoise(new WorldGenRegion(vk, list), bxh));
        SURFACE = registerSimple("surface", ChunkStatus.NOISE, 0, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.buildSurfaceAndBedrock(bxh));
        CARVERS = registerSimple("carvers", ChunkStatus.SURFACE, 0, ChunkStatus.PRE_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.applyCarvers(bxh, GenerationStep.Carving.AIR));
        LIQUID_CARVERS = registerSimple("liquid_carvers", ChunkStatus.CARVERS, 0, ChunkStatus.POST_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.applyCarvers(bxh, GenerationStep.Carving.LIQUID));
        FEATURES = register("features", ChunkStatus.LIQUID_CARVERS, 8, ChunkStatus.POST_FEATURES, ChunkType.PROTOCHUNK, (bxm, vk, bxi, cjp, vn, function, list, bxh) -> {
            bxh.setLightEngine(vn);
            if (!bxh.getStatus().isOrAfter(bxm)) {
                Heightmap.primeHeightmaps(bxh, (Set<Heightmap.Types>)EnumSet.of((Enum)Heightmap.Types.MOTION_BLOCKING, (Enum)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (Enum)Heightmap.Types.OCEAN_FLOOR, (Enum)Heightmap.Types.WORLD_SURFACE));
                bxi.applyBiomeDecoration(new WorldGenRegion(vk, list));
                if (bxh instanceof ProtoChunk) {
                    bxh.setStatus(bxm);
                }
            }
            return (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>)CompletableFuture.completedFuture(Either.left((Object)bxh));
        });
        LIGHT = register("light", ChunkStatus.FEATURES, 1, ChunkStatus.POST_FEATURES, ChunkType.PROTOCHUNK, (bxm, vk, bxi, cjp, vn, function, list, bxh) -> lightChunk(bxm, vn, bxh), (bxm, vk, cjp, vn, function, bxh) -> lightChunk(bxm, vn, bxh));
        SPAWN = registerSimple("spawn", ChunkStatus.LIGHT, 0, ChunkStatus.POST_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> bxi.spawnOriginalMobs(new WorldGenRegion(vk, list)));
        HEIGHTMAPS = registerSimple("heightmaps", ChunkStatus.SPAWN, 0, ChunkStatus.POST_FEATURES, ChunkType.PROTOCHUNK, (vk, bxi, list, bxh) -> {});
        FULL = register("full", ChunkStatus.HEIGHTMAPS, 0, ChunkStatus.POST_FEATURES, ChunkType.LEVELCHUNK, (bxm, vk, bxi, cjp, vn, function, list, bxh) -> (CompletableFuture)function.apply(bxh), (bxm, vk, cjp, vn, function, bxh) -> (CompletableFuture)function.apply(bxh));
        STATUS_BY_RANGE = (List)ImmutableList.of(ChunkStatus.FULL, ChunkStatus.FEATURES, ChunkStatus.LIQUID_CARVERS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS);
        RANGE_BY_STATUS = Util.<IntList>make((IntList)new IntArrayList(getStatusList().size()), (java.util.function.Consumer<IntList>)(intArrayList -> {
            int integer2 = 0;
            for (int integer3 = getStatusList().size() - 1; integer3 >= 0; --integer3) {
                while (integer2 + 1 < ChunkStatus.STATUS_BY_RANGE.size() && integer3 <= ((ChunkStatus)ChunkStatus.STATUS_BY_RANGE.get(integer2 + 1)).getIndex()) {
                    ++integer2;
                }
                intArrayList.add(0, integer2);
            }
        }));
    }
    
    interface SimpleGenerationTask extends GenerationTask {
        default CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(final ChunkStatus bxm, final ServerLevel vk, final ChunkGenerator<?> bxi, final StructureManager cjp, final ThreadedLevelLightEngine vn, final Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, final List<ChunkAccess> list, final ChunkAccess bxh) {
            if (!bxh.getStatus().isOrAfter(bxm)) {
                this.doWork(vk, bxi, list, bxh);
                if (bxh instanceof ProtoChunk) {
                    ((ProtoChunk)bxh).setStatus(bxm);
                }
            }
            return (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>)CompletableFuture.completedFuture(Either.left((Object)bxh));
        }
        
        void doWork(final ServerLevel vk, final ChunkGenerator<?> bxi, final List<ChunkAccess> list, final ChunkAccess bxh);
    }
    
    public enum ChunkType {
        PROTOCHUNK, 
        LEVELCHUNK;
    }
    
    interface GenerationTask {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(final ChunkStatus bxm, final ServerLevel vk, final ChunkGenerator<?> bxi, final StructureManager cjp, final ThreadedLevelLightEngine vn, final Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, final List<ChunkAccess> list, final ChunkAccess bxh);
    }
    
    interface LoadingTask {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(final ChunkStatus bxm, final ServerLevel vk, final StructureManager cjp, final ThreadedLevelLightEngine vn, final Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, final ChunkAccess bxh);
    }
}
