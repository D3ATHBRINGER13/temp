package net.minecraft.server.level;

import net.minecraft.core.Registry;
import java.util.concurrent.CompletionStage;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelType;
import java.util.function.BooleanSupplier;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import java.util.Optional;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.profiling.ProfilerFiller;
import java.util.Arrays;
import net.minecraft.world.level.chunk.LevelChunk;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.level.ChunkPos;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import java.util.function.Supplier;
import net.minecraft.server.level.progress.ChunkProgressListener;
import java.util.concurrent.Executor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import java.util.List;
import net.minecraft.world.level.chunk.ChunkSource;

public class ServerChunkCache extends ChunkSource {
    private static final int MAGIC_NUMBER;
    private static final List<ChunkStatus> CHUNK_STATUSES;
    private final DistanceManager distanceManager;
    private final ChunkGenerator<?> generator;
    private final ServerLevel level;
    private final Thread mainThread;
    private final ThreadedLevelLightEngine lightEngine;
    private final MainThreadExecutor mainThreadProcessor;
    public final ChunkMap chunkMap;
    private final DimensionDataStorage dataStorage;
    private long lastInhabitedUpdate;
    private boolean spawnEnemies;
    private boolean spawnFriendlies;
    private final long[] lastChunkPos;
    private final ChunkStatus[] lastChunkStatus;
    private final ChunkAccess[] lastChunk;
    
    public ServerChunkCache(final ServerLevel vk, final File file, final DataFixer dataFixer, final StructureManager cjp, final Executor executor, final ChunkGenerator<?> bxi, final int integer, final ChunkProgressListener vt, final Supplier<DimensionDataStorage> supplier) {
        this.spawnEnemies = true;
        this.spawnFriendlies = true;
        this.lastChunkPos = new long[4];
        this.lastChunkStatus = new ChunkStatus[4];
        this.lastChunk = new ChunkAccess[4];
        this.level = vk;
        this.mainThreadProcessor = new MainThreadExecutor((Level)vk);
        this.generator = bxi;
        this.mainThread = Thread.currentThread();
        final File file2 = vk.getDimension().getType().getStorageFolder(file);
        final File file3 = new File(file2, "data");
        file3.mkdirs();
        this.dataStorage = new DimensionDataStorage(file3, dataFixer);
        this.chunkMap = new ChunkMap(vk, file, dataFixer, cjp, executor, this.mainThreadProcessor, this, this.getGenerator(), vt, supplier, integer);
        this.lightEngine = this.chunkMap.getLightEngine();
        this.distanceManager = this.chunkMap.getDistanceManager();
        this.clearCache();
    }
    
    @Override
    public ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }
    
    @Nullable
    private ChunkHolder getVisibleChunkIfPresent(final long long1) {
        return this.chunkMap.getVisibleChunkIfPresent(long1);
    }
    
    public int getTickingGenerated() {
        return this.chunkMap.getTickingGenerated();
    }
    
    private void storeInCache(final long long1, final ChunkAccess bxh, final ChunkStatus bxm) {
        for (int integer6 = 3; integer6 > 0; --integer6) {
            this.lastChunkPos[integer6] = this.lastChunkPos[integer6 - 1];
            this.lastChunkStatus[integer6] = this.lastChunkStatus[integer6 - 1];
            this.lastChunk[integer6] = this.lastChunk[integer6 - 1];
        }
        this.lastChunkPos[0] = long1;
        this.lastChunkStatus[0] = bxm;
        this.lastChunk[0] = bxh;
    }
    
    @Nullable
    @Override
    public ChunkAccess getChunk(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        if (Thread.currentThread() != this.mainThread) {
            return (ChunkAccess)CompletableFuture.supplyAsync(() -> this.getChunk(integer1, integer2, bxm, boolean4), (Executor)this.mainThreadProcessor).join();
        }
        final long long6 = ChunkPos.asLong(integer1, integer2);
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            if (long6 == this.lastChunkPos[integer3] && bxm == this.lastChunkStatus[integer3]) {
                final ChunkAccess bxh9 = this.lastChunk[integer3];
                if (bxh9 != null || !boolean4) {
                    return bxh9;
                }
            }
        }
        final CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture8 = this.getChunkFutureMainThread(integer1, integer2, bxm, boolean4);
        this.mainThreadProcessor.managedBlock(completableFuture8::isDone);
        final ChunkAccess bxh9 = (ChunkAccess)((Either)completableFuture8.join()).map(bxh -> bxh, a -> {
            if (boolean4) {
                throw new IllegalStateException(new StringBuilder().append("Chunk not there when requested: ").append(a).toString());
            }
            return null;
        });
        this.storeInCache(long6, bxh9, bxm);
        return bxh9;
    }
    
    @Nullable
    @Override
    public LevelChunk getChunkNow(final int integer1, final int integer2) {
        if (Thread.currentThread() != this.mainThread) {
            return null;
        }
        final long long4 = ChunkPos.asLong(integer1, integer2);
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            if (long4 == this.lastChunkPos[integer3] && this.lastChunkStatus[integer3] == ChunkStatus.FULL) {
                final ChunkAccess bxh7 = this.lastChunk[integer3];
                return (bxh7 instanceof LevelChunk) ? ((LevelChunk)bxh7) : null;
            }
        }
        final ChunkHolder uv6 = this.getVisibleChunkIfPresent(long4);
        if (uv6 == null) {
            return null;
        }
        final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either7 = (Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>)uv6.getFutureIfPresent(ChunkStatus.FULL).getNow(null);
        if (either7 == null) {
            return null;
        }
        final ChunkAccess bxh8 = (ChunkAccess)either7.left().orElse(null);
        if (bxh8 != null) {
            this.storeInCache(long4, bxh8, ChunkStatus.FULL);
            if (bxh8 instanceof LevelChunk) {
                return (LevelChunk)bxh8;
            }
        }
        return null;
    }
    
    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill((Object[])this.lastChunkStatus, null);
        Arrays.fill((Object[])this.lastChunk, null);
    }
    
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        final boolean boolean5 = Thread.currentThread() == this.mainThread;
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture7;
        if (boolean5) {
            completableFuture7 = this.getChunkFutureMainThread(integer1, integer2, bxm, boolean4);
            this.mainThreadProcessor.managedBlock(completableFuture7::isDone);
        }
        else {
            completableFuture7 = (CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>)CompletableFuture.supplyAsync(() -> this.getChunkFutureMainThread(integer1, integer2, bxm, boolean4), (Executor)this.mainThreadProcessor).thenCompose(completableFuture -> completableFuture);
        }
        return completableFuture7;
    }
    
    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(final int integer1, final int integer2, final ChunkStatus bxm, final boolean boolean4) {
        final ChunkPos bhd6 = new ChunkPos(integer1, integer2);
        final long long7 = bhd6.toLong();
        final int integer3 = 33 + ChunkStatus.getDistance(bxm);
        ChunkHolder uv10 = this.getVisibleChunkIfPresent(long7);
        if (boolean4) {
            this.distanceManager.<ChunkPos>addTicket(TicketType.UNKNOWN, bhd6, integer3, bhd6);
            if (this.chunkAbsent(uv10, integer3)) {
                final ProfilerFiller agn11 = this.level.getProfiler();
                agn11.push("chunkLoad");
                this.runDistanceManagerUpdates();
                uv10 = this.getVisibleChunkIfPresent(long7);
                agn11.pop();
                if (this.chunkAbsent(uv10, integer3)) {
                    throw new IllegalStateException("No chunk holder after ticket has been added");
                }
            }
        }
        if (this.chunkAbsent(uv10, integer3)) {
            return ChunkHolder.UNLOADED_CHUNK_FUTURE;
        }
        return uv10.getOrScheduleFuture(bxm, this.chunkMap);
    }
    
    private boolean chunkAbsent(@Nullable final ChunkHolder uv, final int integer) {
        return uv == null || uv.getTicketLevel() > integer;
    }
    
    @Override
    public boolean hasChunk(final int integer1, final int integer2) {
        final ChunkHolder uv4 = this.getVisibleChunkIfPresent(new ChunkPos(integer1, integer2).toLong());
        final int integer3 = 33 + ChunkStatus.getDistance(ChunkStatus.FULL);
        return !this.chunkAbsent(uv4, integer3);
    }
    
    @Override
    public BlockGetter getChunkForLighting(final int integer1, final int integer2) {
        final long long4 = ChunkPos.asLong(integer1, integer2);
        final ChunkHolder uv6 = this.getVisibleChunkIfPresent(long4);
        if (uv6 == null) {
            return null;
        }
        int integer3 = ServerChunkCache.CHUNK_STATUSES.size() - 1;
        while (true) {
            final ChunkStatus bxm8 = (ChunkStatus)ServerChunkCache.CHUNK_STATUSES.get(integer3);
            final Optional<ChunkAccess> optional9 = (Optional<ChunkAccess>)((Either)uv6.getFutureIfPresentUnchecked(bxm8).getNow(ChunkHolder.UNLOADED_CHUNK)).left();
            if (optional9.isPresent()) {
                return (BlockGetter)optional9.get();
            }
            if (bxm8 == ChunkStatus.LIGHT.getParent()) {
                return null;
            }
            --integer3;
        }
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public boolean pollTask() {
        return this.mainThreadProcessor.pollTask();
    }
    
    private boolean runDistanceManagerUpdates() {
        final boolean boolean2 = this.distanceManager.runAllUpdates(this.chunkMap);
        final boolean boolean3 = this.chunkMap.promoteChunkMap();
        if (boolean2 || boolean3) {
            this.clearCache();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isEntityTickingChunk(final Entity aio) {
        final long long3 = ChunkPos.asLong(Mth.floor(aio.x) >> 4, Mth.floor(aio.z) >> 4);
        return this.checkChunkFuture(long3, (Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>>)ChunkHolder::getEntityTickingChunkFuture);
    }
    
    @Override
    public boolean isEntityTickingChunk(final ChunkPos bhd) {
        return this.checkChunkFuture(bhd.toLong(), (Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>>)ChunkHolder::getEntityTickingChunkFuture);
    }
    
    @Override
    public boolean isTickingChunk(final BlockPos ew) {
        final long long3 = ChunkPos.asLong(ew.getX() >> 4, ew.getZ() >> 4);
        return this.checkChunkFuture(long3, (Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>>)ChunkHolder::getTickingChunkFuture);
    }
    
    public boolean isInAccessibleChunk(final Entity aio) {
        final long long3 = ChunkPos.asLong(Mth.floor(aio.x) >> 4, Mth.floor(aio.z) >> 4);
        return this.checkChunkFuture(long3, (Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>>)ChunkHolder::getFullChunkFuture);
    }
    
    private boolean checkChunkFuture(final long long1, final Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>> function) {
        final ChunkHolder uv5 = this.getVisibleChunkIfPresent(long1);
        if (uv5 == null) {
            return false;
        }
        final Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either6 = (Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>)((CompletableFuture)function.apply(uv5)).getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK);
        return either6.left().isPresent();
    }
    
    public void save(final boolean boolean1) {
        this.runDistanceManagerUpdates();
        this.chunkMap.saveAllChunks(boolean1);
    }
    
    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightEngine.close();
        this.chunkMap.close();
    }
    
    @Override
    public void tick(final BooleanSupplier booleanSupplier) {
        this.level.getProfiler().push("purge");
        this.distanceManager.purgeStaleTickets();
        this.runDistanceManagerUpdates();
        this.level.getProfiler().popPush("chunks");
        this.tickChunks();
        this.level.getProfiler().popPush("unload");
        this.chunkMap.tick(booleanSupplier);
        this.level.getProfiler().pop();
        this.clearCache();
    }
    
    private void tickChunks() {
        final long long2 = this.level.getGameTime();
        final long long3 = long2 - this.lastInhabitedUpdate;
        this.lastInhabitedUpdate = long2;
        final LevelData com6 = this.level.getLevelData();
        final boolean boolean7 = com6.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES;
        final boolean boolean8 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        if (!boolean7) {
            this.level.getProfiler().push("pollingChunks");
            final int integer9 = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
            final BlockPos ew10 = this.level.getSharedSpawnPos();
            final boolean boolean9 = com6.getGameTime() % 400L == 0L;
            this.level.getProfiler().push("naturalSpawnCount");
            final int integer10 = this.distanceManager.getNaturalSpawnChunkCount();
            final MobCategory[] arr13 = MobCategory.values();
            final Object2IntMap<MobCategory> object2IntMap14 = this.level.getMobCategoryCounts();
            this.level.getProfiler().pop();
            this.chunkMap.getChunks().forEach(uv -> {
                final Optional<LevelChunk> optional12 = (Optional<LevelChunk>)((Either)uv.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK)).left();
                if (!optional12.isPresent()) {
                    return;
                }
                final LevelChunk bxt13 = (LevelChunk)optional12.get();
                this.level.getProfiler().push("broadcast");
                uv.broadcastChanges(bxt13);
                this.level.getProfiler().pop();
                final ChunkPos bhd14 = uv.getPos();
                if (this.chunkMap.noPlayersCloseForSpawning(bhd14)) {
                    return;
                }
                bxt13.setInhabitedTime(bxt13.getInhabitedTime() + long3);
                if (boolean8 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds(bxt13.getPos())) {
                    this.level.getProfiler().push("spawner");
                    for (final MobCategory aiz18 : arr13) {
                        if (aiz18 != MobCategory.MISC) {
                            if (!aiz18.isFriendly() || this.spawnFriendlies) {
                                if (aiz18.isFriendly() || this.spawnEnemies) {
                                    if (!aiz18.isPersistent() || boolean9) {
                                        final int integer9 = aiz18.getMaxInstancesPerChunk() * integer10 / ServerChunkCache.MAGIC_NUMBER;
                                        if (object2IntMap14.getInt(aiz18) <= integer9) {
                                            NaturalSpawner.spawnCategoryForChunk(aiz18, this.level, bxt13, ew10);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.level.getProfiler().pop();
                }
                this.level.tickChunk(bxt13, integer9);
            });
            this.level.getProfiler().push("customSpawners");
            if (boolean8) {
                this.generator.tickCustomSpawners(this.level, this.spawnEnemies, this.spawnFriendlies);
            }
            this.level.getProfiler().pop();
            this.level.getProfiler().pop();
        }
        this.chunkMap.tick();
    }
    
    @Override
    public String gatherStats() {
        return new StringBuilder().append("ServerChunkCache: ").append(this.getLoadedChunksCount()).toString();
    }
    
    @VisibleForTesting
    public int getPendingTasksCount() {
        return this.mainThreadProcessor.getPendingTasksCount();
    }
    
    @Override
    public ChunkGenerator<?> getGenerator() {
        return this.generator;
    }
    
    public int getLoadedChunksCount() {
        return this.chunkMap.size();
    }
    
    public void blockChanged(final BlockPos ew) {
        final int integer3 = ew.getX() >> 4;
        final int integer4 = ew.getZ() >> 4;
        final ChunkHolder uv5 = this.getVisibleChunkIfPresent(ChunkPos.asLong(integer3, integer4));
        if (uv5 != null) {
            uv5.blockChanged(ew.getX() & 0xF, ew.getY(), ew.getZ() & 0xF);
        }
    }
    
    public void onLightUpdate(final LightLayer bia, final SectionPos fp) {
        this.mainThreadProcessor.execute(() -> {
            final ChunkHolder uv4 = this.getVisibleChunkIfPresent(fp.chunk().toLong());
            if (uv4 != null) {
                uv4.sectionLightChanged(bia, fp.y());
            }
        });
    }
    
    public <T> void addRegionTicket(final TicketType<T> vp, final ChunkPos bhd, final int integer, final T object) {
        this.distanceManager.<T>addRegionTicket(vp, bhd, integer, object);
    }
    
    public <T> void removeRegionTicket(final TicketType<T> vp, final ChunkPos bhd, final int integer, final T object) {
        this.distanceManager.<T>removeRegionTicket(vp, bhd, integer, object);
    }
    
    @Override
    public void updateChunkForced(final ChunkPos bhd, final boolean boolean2) {
        this.distanceManager.updateChunkForced(bhd, boolean2);
    }
    
    public void move(final ServerPlayer vl) {
        this.chunkMap.move(vl);
    }
    
    public void removeEntity(final Entity aio) {
        this.chunkMap.removeEntity(aio);
    }
    
    public void addEntity(final Entity aio) {
        this.chunkMap.addEntity(aio);
    }
    
    public void broadcastAndSend(final Entity aio, final Packet<?> kc) {
        this.chunkMap.broadcastAndSend(aio, kc);
    }
    
    public void broadcast(final Entity aio, final Packet<?> kc) {
        this.chunkMap.broadcast(aio, kc);
    }
    
    public void setViewDistance(final int integer) {
        this.chunkMap.setViewDistance(integer);
    }
    
    @Override
    public void setSpawnSettings(final boolean boolean1, final boolean boolean2) {
        this.spawnEnemies = boolean1;
        this.spawnFriendlies = boolean2;
    }
    
    public String getChunkDebugData(final ChunkPos bhd) {
        return this.chunkMap.getChunkDebugData(bhd);
    }
    
    public DimensionDataStorage getDataStorage() {
        return this.dataStorage;
    }
    
    public PoiManager getPoiManager() {
        return this.chunkMap.getPoiManager();
    }
    
    static {
        MAGIC_NUMBER = (int)Math.pow(17.0, 2.0);
        CHUNK_STATUSES = ChunkStatus.getStatusList();
    }
    
    final class MainThreadExecutor extends BlockableEventLoop<Runnable> {
        private MainThreadExecutor(final Level bhr) {
            super(new StringBuilder().append("Chunk source main thread executor for ").append(Registry.DIMENSION_TYPE.getKey(bhr.getDimension().getType())).toString());
        }
        
        @Override
        protected Runnable wrapRunnable(final Runnable runnable) {
            return runnable;
        }
        
        @Override
        protected boolean shouldRun(final Runnable runnable) {
            return true;
        }
        
        @Override
        protected boolean scheduleExecutables() {
            return true;
        }
        
        @Override
        protected Thread getRunningThread() {
            return ServerChunkCache.this.mainThread;
        }
        
        @Override
        protected boolean pollTask() {
            if (ServerChunkCache.this.runDistanceManagerUpdates()) {
                return true;
            }
            ServerChunkCache.this.lightEngine.tryScheduleUpdate();
            return super.pollTask();
        }
    }
}
