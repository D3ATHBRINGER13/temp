package net.minecraft.server.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.chunk.LevelChunkSection;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.chunk.LightChunkGetter;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.thread.ProcessorHandle;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.thread.ProcessorMailbox;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ThreadedLevelLightEngine extends LevelLightEngine implements AutoCloseable {
    private static final Logger LOGGER;
    private final ProcessorMailbox<Runnable> taskMailbox;
    private final ObjectList<Pair<TaskType, Runnable>> lightTasks;
    private final ChunkMap chunkMap;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorterMailbox;
    private volatile int taskPerBatch;
    private final AtomicBoolean scheduled;
    
    public ThreadedLevelLightEngine(final LightChunkGetter bxv, final ChunkMap uw, final boolean boolean3, final ProcessorMailbox<Runnable> agt, final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> ags) {
        super(bxv, true, boolean3);
        this.lightTasks = (ObjectList<Pair<TaskType, Runnable>>)new ObjectArrayList();
        this.taskPerBatch = 5;
        this.scheduled = new AtomicBoolean();
        this.chunkMap = uw;
        this.sorterMailbox = ags;
        this.taskMailbox = agt;
    }
    
    public void close() {
    }
    
    @Override
    public int runUpdates(final int integer, final boolean boolean2, final boolean boolean3) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }
    
    @Override
    public void onBlockEmissionIncrease(final BlockPos ew, final int integer) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }
    
    @Override
    public void checkBlock(final BlockPos ew) {
        final BlockPos ew2 = ew.immutable();
        this.addTask(ew.getX() >> 4, ew.getZ() >> 4, TaskType.POST_UPDATE, Util.name(() -> super.checkBlock(ew2), (Supplier<String>)(() -> new StringBuilder().append("checkBlock ").append(ew2).toString())));
    }
    
    protected void updateChunkStatus(final ChunkPos bhd) {
        this.addTask(bhd.x, bhd.z, () -> 0, TaskType.PRE_UPDATE, Util.name(() -> {
            super.retainData(bhd, false);
            super.enableLightSources(bhd, false);
            for (int integer3 = -1; integer3 < 17; ++integer3) {
                super.queueSectionData(LightLayer.BLOCK, SectionPos.of(bhd, integer3), null);
                super.queueSectionData(LightLayer.SKY, SectionPos.of(bhd, integer3), null);
            }
            for (int integer3 = 0; integer3 < 16; ++integer3) {
                super.updateSectionStatus(SectionPos.of(bhd, integer3), true);
            }
        }, (Supplier<String>)(() -> new StringBuilder().append("updateChunkStatus ").append(bhd).append(" ").append(true).toString())));
    }
    
    @Override
    public void updateSectionStatus(final SectionPos fp, final boolean boolean2) {
        this.addTask(fp.x(), fp.z(), () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.updateSectionStatus(fp, boolean2), (Supplier<String>)(() -> new StringBuilder().append("updateSectionStatus ").append(fp).append(" ").append(boolean2).toString())));
    }
    
    @Override
    public void enableLightSources(final ChunkPos bhd, final boolean boolean2) {
        this.addTask(bhd.x, bhd.z, TaskType.PRE_UPDATE, Util.name(() -> super.enableLightSources(bhd, boolean2), (Supplier<String>)(() -> new StringBuilder().append("enableLight ").append(bhd).append(" ").append(boolean2).toString())));
    }
    
    @Override
    public void queueSectionData(final LightLayer bia, final SectionPos fp, @Nullable final DataLayer bxn) {
        this.addTask(fp.x(), fp.z(), () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.queueSectionData(bia, fp, bxn), (Supplier<String>)(() -> new StringBuilder().append("queueData ").append(fp).toString())));
    }
    
    private void addTask(final int integer1, final int integer2, final TaskType a, final Runnable runnable) {
        this.addTask(integer1, integer2, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(integer1, integer2)), a, runnable);
    }
    
    private void addTask(final int integer1, final int integer2, final IntSupplier intSupplier, final TaskType a, final Runnable runnable) {
        this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
            this.lightTasks.add(Pair.of((Object)a, (Object)runnable));
            if (this.lightTasks.size() >= this.taskPerBatch) {
                this.runUpdate();
            }
        }, ChunkPos.asLong(integer1, integer2), intSupplier));
    }
    
    @Override
    public void retainData(final ChunkPos bhd, final boolean boolean2) {
        this.addTask(bhd.x, bhd.z, () -> 0, TaskType.PRE_UPDATE, Util.name(() -> super.retainData(bhd, boolean2), (Supplier<String>)(() -> new StringBuilder().append("retainData ").append(bhd).toString())));
    }
    
    public CompletableFuture<ChunkAccess> lightChunk(final ChunkAccess bxh, final boolean boolean2) {
        final ChunkPos bhd4 = bxh.getPos();
        bxh.setLightCorrect(false);
        this.addTask(bhd4.x, bhd4.z, TaskType.PRE_UPDATE, Util.name(() -> {
            final LevelChunkSection[] arr5 = bxh.getSections();
            for (int integer6 = 0; integer6 < 16; ++integer6) {
                final LevelChunkSection bxu7 = arr5[integer6];
                if (!LevelChunkSection.isEmpty(bxu7)) {
                    super.updateSectionStatus(SectionPos.of(bhd4, integer6), false);
                }
            }
            super.enableLightSources(bhd4, true);
            if (!boolean2) {
                bxh.getLights().forEach(ew -> super.onBlockEmissionIncrease(ew, bxh.getLightEmission(ew)));
            }
            this.chunkMap.releaseLightTicket(bhd4);
        }, (Supplier<String>)(() -> new StringBuilder().append("lightChunk ").append(bhd4).append(" ").append(boolean2).toString())));
        return (CompletableFuture<ChunkAccess>)CompletableFuture.supplyAsync(() -> {
            bxh.setLightCorrect(true);
            super.retainData(bhd4, false);
            return bxh;
        }, runnable -> this.addTask(bhd4.x, bhd4.z, TaskType.POST_UPDATE, runnable));
    }
    
    public void tryScheduleUpdate() {
        if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
            this.taskMailbox.tell(() -> {
                this.runUpdate();
                this.scheduled.set(false);
            });
        }
    }
    
    private void runUpdate() {
        int integer2;
        ObjectListIterator<Pair<TaskType, Runnable>> objectListIterator3;
        int integer3;
        Pair<TaskType, Runnable> pair5;
        for (integer2 = Math.min(this.lightTasks.size(), this.taskPerBatch), objectListIterator3 = (ObjectListIterator<Pair<TaskType, Runnable>>)this.lightTasks.iterator(), integer3 = 0; objectListIterator3.hasNext() && integer3 < integer2; ++integer3) {
            pair5 = (Pair<TaskType, Runnable>)objectListIterator3.next();
            if (pair5.getFirst() == TaskType.PRE_UPDATE) {
                ((Runnable)pair5.getSecond()).run();
            }
        }
        objectListIterator3.back(integer3);
        super.runUpdates(Integer.MAX_VALUE, true, true);
        for (integer3 = 0; objectListIterator3.hasNext() && integer3 < integer2; ++integer3) {
            pair5 = (Pair<TaskType, Runnable>)objectListIterator3.next();
            if (pair5.getFirst() == TaskType.POST_UPDATE) {
                ((Runnable)pair5.getSecond()).run();
            }
            objectListIterator3.remove();
        }
    }
    
    public void setTaskPerBatch(final int integer) {
        this.taskPerBatch = integer;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    enum TaskType {
        PRE_UPDATE, 
        POST_UPDATE;
    }
}
