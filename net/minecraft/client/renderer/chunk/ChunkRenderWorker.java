package net.minecraft.client.renderer.chunk;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.phys.Vec3;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.CancellationException;
import java.util.List;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import net.minecraft.world.level.BlockLayer;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.CrashReport;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
    private static final Logger LOGGER;
    private final ChunkRenderDispatcher dispatcher;
    private final ChunkBufferBuilderPack fixedBuffers;
    private boolean running;
    
    public ChunkRenderWorker(final ChunkRenderDispatcher dpu) {
        this(dpu, null);
    }
    
    public ChunkRenderWorker(final ChunkRenderDispatcher dpu, @Nullable final ChunkBufferBuilderPack dmv) {
        this.running = true;
        this.dispatcher = dpu;
        this.fixedBuffers = dmv;
    }
    
    public void run() {
        while (this.running) {
            try {
                this.doTask(this.dispatcher.takeChunk());
                continue;
            }
            catch (InterruptedException interruptedException2) {
                ChunkRenderWorker.LOGGER.debug("Stopping chunk worker due to interrupt");
                return;
            }
            catch (Throwable throwable2) {
                final CrashReport d3 = CrashReport.forThrowable(throwable2, "Batching chunks");
                Minecraft.getInstance().delayCrash(Minecraft.getInstance().fillReport(d3));
                return;
            }
            break;
        }
    }
    
    void doTask(final ChunkCompileTask dpt) throws InterruptedException {
        dpt.getStatusLock().lock();
        try {
            if (!checkState(dpt, ChunkCompileTask.Status.PENDING)) {
                return;
            }
            if (!dpt.getChunk().hasAllNeighbors()) {
                dpt.cancel();
                return;
            }
            dpt.setStatus(ChunkCompileTask.Status.COMPILING);
        }
        finally {
            dpt.getStatusLock().unlock();
        }
        final ChunkBufferBuilderPack dmv3 = this.takeBuffers();
        dpt.getStatusLock().lock();
        try {
            if (!checkState(dpt, ChunkCompileTask.Status.COMPILING)) {
                this.releaseBuffers(dmv3);
                return;
            }
        }
        finally {
            dpt.getStatusLock().unlock();
        }
        dpt.setBuilders(dmv3);
        final Vec3 csi4 = this.dispatcher.getCameraPosition();
        final float float5 = (float)csi4.x;
        final float float6 = (float)csi4.y;
        final float float7 = (float)csi4.z;
        final ChunkCompileTask.Type b8 = dpt.getType();
        if (b8 == ChunkCompileTask.Type.REBUILD_CHUNK) {
            dpt.getChunk().compile(float5, float6, float7, dpt);
        }
        else if (b8 == ChunkCompileTask.Type.RESORT_TRANSPARENCY) {
            dpt.getChunk().rebuildTransparent(float5, float6, float7, dpt);
        }
        dpt.getStatusLock().lock();
        try {
            if (!checkState(dpt, ChunkCompileTask.Status.COMPILING)) {
                this.releaseBuffers(dmv3);
                return;
            }
            dpt.setStatus(ChunkCompileTask.Status.UPLOADING);
        }
        finally {
            dpt.getStatusLock().unlock();
        }
        final CompiledChunk dpw9 = dpt.getCompiledChunk();
        final List<ListenableFuture<Void>> list10 = (List<ListenableFuture<Void>>)Lists.newArrayList();
        if (b8 == ChunkCompileTask.Type.REBUILD_CHUNK) {
            for (final BlockLayer bhc14 : BlockLayer.values()) {
                if (dpw9.hasLayer(bhc14)) {
                    list10.add(this.dispatcher.uploadChunkLayer(bhc14, dpt.getBuilders().builder(bhc14), dpt.getChunk(), dpw9, dpt.getDistAtCreation()));
                }
            }
        }
        else if (b8 == ChunkCompileTask.Type.RESORT_TRANSPARENCY) {
            list10.add(this.dispatcher.uploadChunkLayer(BlockLayer.TRANSLUCENT, dpt.getBuilders().builder(BlockLayer.TRANSLUCENT), dpt.getChunk(), dpw9, dpt.getDistAtCreation()));
        }
        final ListenableFuture<List<Void>> listenableFuture11 = (ListenableFuture<List<Void>>)Futures.allAsList((Iterable)list10);
        dpt.addCancelListener(() -> listenableFuture11.cancel(false));
        Futures.addCallback((ListenableFuture)listenableFuture11, (FutureCallback)new FutureCallback<List<Void>>() {
            public void onSuccess(@Nullable final List<Void> list) {
                ChunkRenderWorker.this.releaseBuffers(dmv3);
                dpt.getStatusLock().lock();
                try {
                    if (!checkState(dpt, ChunkCompileTask.Status.UPLOADING)) {
                        return;
                    }
                    dpt.setStatus(ChunkCompileTask.Status.DONE);
                }
                finally {
                    dpt.getStatusLock().unlock();
                }
                dpt.getChunk().setCompiledChunk(dpw9);
            }
            
            public void onFailure(final Throwable throwable) {
                ChunkRenderWorker.this.releaseBuffers(dmv3);
                if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                    Minecraft.getInstance().delayCrash(CrashReport.forThrowable(throwable, "Rendering chunk"));
                }
            }
        });
    }
    
    private static boolean checkState(final ChunkCompileTask dpt, final ChunkCompileTask.Status a) {
        if (dpt.getStatus() != a) {
            if (!dpt.wasCancelled()) {
                ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be {}; ignoring task", dpt.getStatus(), a);
            }
            return false;
        }
        return true;
    }
    
    private ChunkBufferBuilderPack takeBuffers() throws InterruptedException {
        return (this.fixedBuffers != null) ? this.fixedBuffers : this.dispatcher.takeChunkBufferBuilder();
    }
    
    private void releaseBuffers(final ChunkBufferBuilderPack dmv) {
        if (dmv != this.fixedBuffers) {
            this.dispatcher.releaseChunkBufferBuilder(dmv);
        }
    }
    
    public void stop() {
        this.running = false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
