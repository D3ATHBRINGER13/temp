package net.minecraft.server.level;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.util.Mth;
import java.util.concurrent.CompletionStage;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.game.ClientboundChunkBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.world.level.LightLayer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.world.level.chunk.ChunkStatus;
import java.util.List;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.chunk.ChunkAccess;
import com.mojang.datafixers.util.Either;

public class ChunkHolder {
    public static final Either<ChunkAccess, ChunkLoadingFailure> UNLOADED_CHUNK;
    public static final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> UNLOADED_CHUNK_FUTURE;
    public static final Either<LevelChunk, ChunkLoadingFailure> UNLOADED_LEVEL_CHUNK;
    private static final CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> UNLOADED_LEVEL_CHUNK_FUTURE;
    private static final List<ChunkStatus> CHUNK_STATUSES;
    private static final FullChunkStatus[] FULL_CHUNK_STATUSES;
    private final AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> futures;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> fullChunkFuture;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> tickingChunkFuture;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> entityTickingChunkFuture;
    private CompletableFuture<ChunkAccess> chunkToSave;
    private int oldTicketLevel;
    private int ticketLevel;
    private int queueLevel;
    private final ChunkPos pos;
    private final short[] changedBlocks;
    private int changes;
    private int changedSectionFilter;
    private int sectionsToForceSendLightFor;
    private int blockChangedLightSectionFilter;
    private int skyChangedLightSectionFilter;
    private final LevelLightEngine lightEngine;
    private final LevelChangeListener onLevelChange;
    private final PlayerProvider playerProvider;
    private boolean wasAccessibleSinceLastSave;
    
    public ChunkHolder(final ChunkPos bhd, final int integer, final LevelLightEngine clb, final LevelChangeListener c, final PlayerProvider d) {
        this.futures = (AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>>)new AtomicReferenceArray(ChunkHolder.CHUNK_STATUSES.size());
        this.fullChunkFuture = ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE;
        this.tickingChunkFuture = ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE;
        this.entityTickingChunkFuture = ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE;
        this.chunkToSave = (CompletableFuture<ChunkAccess>)CompletableFuture.completedFuture(null);
        this.changedBlocks = new short[64];
        this.pos = bhd;
        this.lightEngine = clb;
        this.onLevelChange = c;
        this.playerProvider = d;
        this.oldTicketLevel = ChunkMap.MAX_CHUNK_DISTANCE + 1;
        this.ticketLevel = this.oldTicketLevel;
        this.queueLevel = this.oldTicketLevel;
        this.setTicketLevel(integer);
    }
    
    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getFutureIfPresentUnchecked(final ChunkStatus bxm) {
        final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture3 = (CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>)this.futures.get(bxm.getIndex());
        return (completableFuture3 == null) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : completableFuture3;
    }
    
    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getFutureIfPresent(final ChunkStatus bxm) {
        if (getStatus(this.ticketLevel).isOrAfter(bxm)) {
            return this.getFutureIfPresentUnchecked(bxm);
        }
        return ChunkHolder.UNLOADED_CHUNK_FUTURE;
    }
    
    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getTickingChunkFuture() {
        return this.tickingChunkFuture;
    }
    
    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getEntityTickingChunkFuture() {
        return this.entityTickingChunkFuture;
    }
    
    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getFullChunkFuture() {
        return this.fullChunkFuture;
    }
    
    @Nullable
    public LevelChunk getTickingChunk() {
        final CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> completableFuture2 = this.getTickingChunkFuture();
        final Either<LevelChunk, ChunkLoadingFailure> either3 = (Either<LevelChunk, ChunkLoadingFailure>)completableFuture2.getNow(null);
        if (either3 == null) {
            return null;
        }
        return (LevelChunk)either3.left().orElse(null);
    }
    
    @Nullable
    public ChunkStatus getLastAvailableStatus() {
        for (int integer2 = ChunkHolder.CHUNK_STATUSES.size() - 1; integer2 >= 0; --integer2) {
            final ChunkStatus bxm3 = (ChunkStatus)ChunkHolder.CHUNK_STATUSES.get(integer2);
            final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture4 = this.getFutureIfPresentUnchecked(bxm3);
            if (((Either)completableFuture4.getNow(ChunkHolder.UNLOADED_CHUNK)).left().isPresent()) {
                return bxm3;
            }
        }
        return null;
    }
    
    @Nullable
    public ChunkAccess getLastAvailable() {
        for (int integer2 = ChunkHolder.CHUNK_STATUSES.size() - 1; integer2 >= 0; --integer2) {
            final ChunkStatus bxm3 = (ChunkStatus)ChunkHolder.CHUNK_STATUSES.get(integer2);
            final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture4 = this.getFutureIfPresentUnchecked(bxm3);
            if (!completableFuture4.isCompletedExceptionally()) {
                final Optional<ChunkAccess> optional5 = (Optional<ChunkAccess>)((Either)completableFuture4.getNow(ChunkHolder.UNLOADED_CHUNK)).left();
                if (optional5.isPresent()) {
                    return (ChunkAccess)optional5.get();
                }
            }
        }
        return null;
    }
    
    public CompletableFuture<ChunkAccess> getChunkToSave() {
        return this.chunkToSave;
    }
    
    public void blockChanged(final int integer1, final int integer2, final int integer3) {
        final LevelChunk bxt5 = this.getTickingChunk();
        if (bxt5 == null) {
            return;
        }
        this.changedSectionFilter |= 1 << (integer2 >> 4);
        if (this.changes < 64) {
            final short short6 = (short)(integer1 << 12 | integer3 << 8 | integer2);
            for (int integer4 = 0; integer4 < this.changes; ++integer4) {
                if (this.changedBlocks[integer4] == short6) {
                    return;
                }
            }
            this.changedBlocks[this.changes++] = short6;
        }
    }
    
    public void sectionLightChanged(final LightLayer bia, final int integer) {
        final LevelChunk bxt4 = this.getTickingChunk();
        if (bxt4 == null) {
            return;
        }
        bxt4.setUnsaved(true);
        if (bia == LightLayer.SKY) {
            this.skyChangedLightSectionFilter |= 1 << integer + 1;
        }
        else {
            this.blockChangedLightSectionFilter |= 1 << integer + 1;
        }
    }
    
    public void broadcastChanges(final LevelChunk bxt) {
        if (this.changes == 0 && this.skyChangedLightSectionFilter == 0 && this.blockChangedLightSectionFilter == 0) {
            return;
        }
        final Level bhr3 = bxt.getLevel();
        if (this.changes == 64) {
            this.sectionsToForceSendLightFor = -1;
        }
        if (this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
            this.broadcast(new ClientboundLightUpdatePacket(bxt.getPos(), this.lightEngine, this.skyChangedLightSectionFilter & ~this.sectionsToForceSendLightFor, this.blockChangedLightSectionFilter & ~this.sectionsToForceSendLightFor), true);
            final int integer4 = this.skyChangedLightSectionFilter & this.sectionsToForceSendLightFor;
            final int integer5 = this.blockChangedLightSectionFilter & this.sectionsToForceSendLightFor;
            if (integer4 != 0 || integer5 != 0) {
                this.broadcast(new ClientboundLightUpdatePacket(bxt.getPos(), this.lightEngine, integer4, integer5), false);
            }
            this.skyChangedLightSectionFilter = 0;
            this.blockChangedLightSectionFilter = 0;
            this.sectionsToForceSendLightFor &= ~(this.skyChangedLightSectionFilter & this.blockChangedLightSectionFilter);
        }
        if (this.changes == 1) {
            final int integer4 = (this.changedBlocks[0] >> 12 & 0xF) + this.pos.x * 16;
            final int integer5 = this.changedBlocks[0] & 0xFF;
            final int integer6 = (this.changedBlocks[0] >> 8 & 0xF) + this.pos.z * 16;
            final BlockPos ew7 = new BlockPos(integer4, integer5, integer6);
            this.broadcast(new ClientboundBlockUpdatePacket(bhr3, ew7), false);
            if (bhr3.getBlockState(ew7).getBlock().isEntityBlock()) {
                this.broadcastBlockEntity(bhr3, ew7);
            }
        }
        else if (this.changes == 64) {
            this.broadcast(new ClientboundLevelChunkPacket(bxt, this.changedSectionFilter), false);
        }
        else if (this.changes != 0) {
            this.broadcast(new ClientboundChunkBlocksUpdatePacket(this.changes, this.changedBlocks, bxt), false);
            for (int integer4 = 0; integer4 < this.changes; ++integer4) {
                final int integer5 = (this.changedBlocks[integer4] >> 12 & 0xF) + this.pos.x * 16;
                final int integer6 = this.changedBlocks[integer4] & 0xFF;
                final int integer7 = (this.changedBlocks[integer4] >> 8 & 0xF) + this.pos.z * 16;
                final BlockPos ew8 = new BlockPos(integer5, integer6, integer7);
                if (bhr3.getBlockState(ew8).getBlock().isEntityBlock()) {
                    this.broadcastBlockEntity(bhr3, ew8);
                }
            }
        }
        this.changes = 0;
        this.changedSectionFilter = 0;
    }
    
    private void broadcastBlockEntity(final Level bhr, final BlockPos ew) {
        final BlockEntity btw4 = bhr.getBlockEntity(ew);
        if (btw4 != null) {
            final ClientboundBlockEntityDataPacket kq5 = btw4.getUpdatePacket();
            if (kq5 != null) {
                this.broadcast(kq5, false);
            }
        }
    }
    
    private void broadcast(final Packet<?> kc, final boolean boolean2) {
        this.playerProvider.getPlayers(this.pos, boolean2).forEach(vl -> vl.connection.send(kc));
    }
    
    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getOrScheduleFuture(final ChunkStatus bxm, final ChunkMap uw) {
        final int integer4 = bxm.getIndex();
        final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture5 = (CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>)this.futures.get(integer4);
        if (completableFuture5 != null) {
            final Either<ChunkAccess, ChunkLoadingFailure> either6 = (Either<ChunkAccess, ChunkLoadingFailure>)completableFuture5.getNow(null);
            if (either6 == null || either6.left().isPresent()) {
                return completableFuture5;
            }
        }
        if (getStatus(this.ticketLevel).isOrAfter(bxm)) {
            final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture6 = uw.schedule(this, bxm);
            this.updateChunkToSave(completableFuture6);
            this.futures.set(integer4, completableFuture6);
            return completableFuture6;
        }
        return (completableFuture5 == null) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : completableFuture5;
    }
    
    private void updateChunkToSave(final CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>> completableFuture) {
        this.chunkToSave = (CompletableFuture<ChunkAccess>)this.chunkToSave.thenCombine((CompletionStage)completableFuture, (bxh, either) -> (ChunkAccess)either.map(bxh -> bxh, a -> bxh));
    }
    
    public FullChunkStatus getFullStatus() {
        return getFullChunkStatus(this.ticketLevel);
    }
    
    public ChunkPos getPos() {
        return this.pos;
    }
    
    public int getTicketLevel() {
        return this.ticketLevel;
    }
    
    public int getQueueLevel() {
        return this.queueLevel;
    }
    
    private void setQueueLevel(final int integer) {
        this.queueLevel = integer;
    }
    
    public void setTicketLevel(final int integer) {
        this.ticketLevel = integer;
    }
    
    protected void updateFutures(final ChunkMap uw) {
        final ChunkStatus bxm3 = getStatus(this.oldTicketLevel);
        final ChunkStatus bxm4 = getStatus(this.ticketLevel);
        final boolean boolean5 = this.oldTicketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
        final boolean boolean6 = this.ticketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
        final FullChunkStatus b7 = getFullChunkStatus(this.oldTicketLevel);
        final FullChunkStatus b8 = getFullChunkStatus(this.ticketLevel);
        if (boolean5) {
            final Either<ChunkAccess, ChunkLoadingFailure> either9 = (Either<ChunkAccess, ChunkLoadingFailure>)Either.right(new ChunkLoadingFailure() {
                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
                }
            });
            for (int integer10 = boolean6 ? (bxm4.getIndex() + 1) : 0; integer10 <= bxm3.getIndex(); ++integer10) {
                final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture11 = (CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>)this.futures.get(integer10);
                if (completableFuture11 != null) {
                    completableFuture11.complete(either9);
                }
                else {
                    this.futures.set(integer10, CompletableFuture.completedFuture((Object)either9));
                }
            }
        }
        final boolean boolean7 = b7.isOrAfter(FullChunkStatus.BORDER);
        final boolean boolean8 = b8.isOrAfter(FullChunkStatus.BORDER);
        this.wasAccessibleSinceLastSave |= boolean8;
        if (!boolean7 && boolean8) {
            this.updateChunkToSave(this.fullChunkFuture = uw.unpackTicks(this));
        }
        if (boolean7 && !boolean8) {
            final CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> completableFuture12 = this.fullChunkFuture;
            this.fullChunkFuture = ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE;
            this.updateChunkToSave(completableFuture12.thenApply(either -> either.ifLeft(uw::packTicks)));
        }
        final boolean boolean9 = b7.isOrAfter(FullChunkStatus.TICKING);
        final boolean boolean10 = b8.isOrAfter(FullChunkStatus.TICKING);
        if (!boolean9 && boolean10) {
            this.updateChunkToSave(this.tickingChunkFuture = uw.postProcess(this));
        }
        if (boolean9 && !boolean10) {
            this.tickingChunkFuture.complete(ChunkHolder.UNLOADED_LEVEL_CHUNK);
            this.tickingChunkFuture = ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        final boolean boolean11 = b7.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        final boolean boolean12 = b8.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        if (!boolean11 && boolean12) {
            if (this.entityTickingChunkFuture != ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE) {
                throw new IllegalStateException();
            }
            this.updateChunkToSave(this.entityTickingChunkFuture = uw.getEntityTickingRangeFuture(this.pos));
        }
        if (boolean11 && !boolean12) {
            this.entityTickingChunkFuture.complete(ChunkHolder.UNLOADED_LEVEL_CHUNK);
            this.entityTickingChunkFuture = ChunkHolder.UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
        this.oldTicketLevel = this.ticketLevel;
    }
    
    public static ChunkStatus getStatus(final int integer) {
        if (integer < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.getStatus(integer - 33);
    }
    
    public static FullChunkStatus getFullChunkStatus(final int integer) {
        return ChunkHolder.FULL_CHUNK_STATUSES[Mth.clamp(33 - integer + 1, 0, ChunkHolder.FULL_CHUNK_STATUSES.length - 1)];
    }
    
    public boolean wasAccessibleSinceLastSave() {
        return this.wasAccessibleSinceLastSave;
    }
    
    public void refreshAccessibility() {
        this.wasAccessibleSinceLastSave = getFullChunkStatus(this.ticketLevel).isOrAfter(FullChunkStatus.BORDER);
    }
    
    public void replaceProtoChunk(final ImposterProtoChunk bxs) {
        for (int integer3 = 0; integer3 < this.futures.length(); ++integer3) {
            final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture4 = (CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>)this.futures.get(integer3);
            if (completableFuture4 != null) {
                final Optional<ChunkAccess> optional5 = (Optional<ChunkAccess>)((Either)completableFuture4.getNow(ChunkHolder.UNLOADED_CHUNK)).left();
                if (optional5.isPresent()) {
                    if (optional5.get() instanceof ProtoChunk) {
                        this.futures.set(integer3, CompletableFuture.completedFuture((Object)Either.left((Object)bxs)));
                    }
                }
            }
        }
        this.updateChunkToSave(CompletableFuture.completedFuture(Either.left((Object)bxs.getWrapped())));
    }
    
    static {
        UNLOADED_CHUNK = Either.right(ChunkLoadingFailure.UNLOADED);
        UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(ChunkHolder.UNLOADED_CHUNK);
        UNLOADED_LEVEL_CHUNK = Either.right(ChunkLoadingFailure.UNLOADED);
        UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(ChunkHolder.UNLOADED_LEVEL_CHUNK);
        CHUNK_STATUSES = ChunkStatus.getStatusList();
        FULL_CHUNK_STATUSES = FullChunkStatus.values();
    }
    
    public enum FullChunkStatus {
        INACCESSIBLE, 
        BORDER, 
        TICKING, 
        ENTITY_TICKING;
        
        public boolean isOrAfter(final FullChunkStatus b) {
            return this.ordinal() >= b.ordinal();
        }
    }
    
    public interface ChunkLoadingFailure {
        public static final ChunkLoadingFailure UNLOADED = new ChunkLoadingFailure() {
            public String toString() {
                return "UNLOADED";
            }
        };
    }
    
    public interface PlayerProvider {
        Stream<ServerPlayer> getPlayers(final ChunkPos bhd, final boolean boolean2);
    }
    
    public interface LevelChangeListener {
        void onLevelChange(final ChunkPos bhd, final IntSupplier intSupplier, final int integer, final IntConsumer intConsumer);
    }
}
