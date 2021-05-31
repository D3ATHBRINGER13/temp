package net.minecraft.client.renderer.chunk;

import com.google.common.primitives.Doubles;
import net.minecraft.DefaultUncaughtExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.Futures;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.Minecraft;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.Util;
import java.util.Collection;
import com.google.common.collect.Queues;
import com.google.common.collect.Lists;
import net.minecraft.world.phys.Vec3;
import java.util.Queue;
import com.mojang.blaze3d.vertex.VertexBufferUploader;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.Logger;

public class ChunkRenderDispatcher {
    private static final Logger LOGGER;
    private static final ThreadFactory THREAD_FACTORY;
    private final int bufferCount;
    private final List<Thread> threads;
    private final List<ChunkRenderWorker> workers;
    private final PriorityBlockingQueue<ChunkCompileTask> chunksToBatch;
    private final BlockingQueue<ChunkBufferBuilderPack> availableChunkBuffers;
    private final BufferUploader uploader;
    private final VertexBufferUploader vboUploader;
    private final Queue<PendingUpload> pendingUploads;
    private final ChunkRenderWorker localWorker;
    private Vec3 camera;
    
    public ChunkRenderDispatcher(final boolean boolean1) {
        this.threads = (List<Thread>)Lists.newArrayList();
        this.workers = (List<ChunkRenderWorker>)Lists.newArrayList();
        this.chunksToBatch = (PriorityBlockingQueue<ChunkCompileTask>)Queues.newPriorityBlockingQueue();
        this.uploader = new BufferUploader();
        this.vboUploader = new VertexBufferUploader();
        this.pendingUploads = (Queue<PendingUpload>)Queues.newPriorityQueue();
        this.camera = Vec3.ZERO;
        final int integer3 = Math.max(1, (int)(Runtime.getRuntime().maxMemory() * 0.3) / 10485760 - 1);
        final int integer4 = Runtime.getRuntime().availableProcessors();
        final int integer5 = boolean1 ? integer4 : Math.min(integer4, 4);
        final int integer6 = Math.max(1, Math.min(integer5 * 2, integer3));
        this.localWorker = new ChunkRenderWorker(this, new ChunkBufferBuilderPack());
        final List<ChunkBufferBuilderPack> list7 = (List<ChunkBufferBuilderPack>)Lists.newArrayListWithExpectedSize(integer6);
        try {
            for (int integer7 = 0; integer7 < integer6; ++integer7) {
                list7.add(new ChunkBufferBuilderPack());
            }
        }
        catch (OutOfMemoryError outOfMemoryError8) {
            ChunkRenderDispatcher.LOGGER.warn("Allocated only {}/{} buffers", list7.size(), integer6);
            for (int integer8 = list7.size() * 2 / 3, integer9 = 0; integer9 < integer8; ++integer9) {
                list7.remove(list7.size() - 1);
            }
            System.gc();
        }
        this.bufferCount = list7.size();
        (this.availableChunkBuffers = (BlockingQueue<ChunkBufferBuilderPack>)Queues.newArrayBlockingQueue(this.bufferCount)).addAll((Collection)list7);
        int integer7 = Math.min(integer5, this.bufferCount);
        if (integer7 > 1) {
            for (int integer8 = 0; integer8 < integer7; ++integer8) {
                final ChunkRenderWorker dpv10 = new ChunkRenderWorker(this);
                final Thread thread11 = ChunkRenderDispatcher.THREAD_FACTORY.newThread((Runnable)dpv10);
                thread11.start();
                this.workers.add(dpv10);
                this.threads.add(thread11);
            }
        }
    }
    
    public String getStats() {
        if (this.threads.isEmpty()) {
            return String.format("pC: %03d, single-threaded", new Object[] { this.chunksToBatch.size() });
        }
        return String.format("pC: %03d, pU: %02d, aB: %02d", new Object[] { this.chunksToBatch.size(), this.pendingUploads.size(), this.availableChunkBuffers.size() });
    }
    
    public void setCamera(final Vec3 csi) {
        this.camera = csi;
    }
    
    public Vec3 getCameraPosition() {
        return this.camera;
    }
    
    public boolean uploadAllPendingUploadsUntil(final long long1) {
        boolean boolean4 = false;
        while (true) {
            boolean boolean5 = false;
            if (this.threads.isEmpty()) {
                final ChunkCompileTask dpt6 = (ChunkCompileTask)this.chunksToBatch.poll();
                if (dpt6 != null) {
                    try {
                        this.localWorker.doTask(dpt6);
                        boolean5 = true;
                    }
                    catch (InterruptedException interruptedException7) {
                        ChunkRenderDispatcher.LOGGER.warn("Skipped task due to interrupt");
                    }
                }
            }
            int integer6 = 0;
            synchronized (this.pendingUploads) {
                while (integer6 < 10) {
                    final PendingUpload a8 = (PendingUpload)this.pendingUploads.poll();
                    if (a8 == null) {
                        break;
                    }
                    if (a8.future.isDone()) {
                        continue;
                    }
                    a8.future.run();
                    boolean5 = true;
                    boolean4 = true;
                    ++integer6;
                }
            }
            if (long1 == 0L) {
                break;
            }
            if (!boolean5) {
                break;
            }
            if (long1 < Util.getNanos()) {
                break;
            }
        }
        return boolean4;
    }
    
    public boolean rebuildChunkAsync(final RenderChunk dpy) {
        dpy.getTaskLock().lock();
        try {
            final ChunkCompileTask dpt3 = dpy.createCompileTask();
            dpt3.addCancelListener(() -> this.chunksToBatch.remove(dpt3));
            final boolean boolean4 = this.chunksToBatch.offer(dpt3);
            if (!boolean4) {
                dpt3.cancel();
            }
            return boolean4;
        }
        finally {
            dpy.getTaskLock().unlock();
        }
    }
    
    public boolean rebuildChunkSync(final RenderChunk dpy) {
        dpy.getTaskLock().lock();
        try {
            final ChunkCompileTask dpt3 = dpy.createCompileTask();
            try {
                this.localWorker.doTask(dpt3);
            }
            catch (InterruptedException ex) {}
            return true;
        }
        finally {
            dpy.getTaskLock().unlock();
        }
    }
    
    public void blockUntilClear() {
        this.clearBatchQueue();
        final List<ChunkBufferBuilderPack> list2 = (List<ChunkBufferBuilderPack>)Lists.newArrayList();
        while (list2.size() != this.bufferCount) {
            this.uploadAllPendingUploadsUntil(Long.MAX_VALUE);
            try {
                list2.add(this.takeChunkBufferBuilder());
            }
            catch (InterruptedException ex) {}
        }
        this.availableChunkBuffers.addAll((Collection)list2);
    }
    
    public void releaseChunkBufferBuilder(final ChunkBufferBuilderPack dmv) {
        this.availableChunkBuffers.add(dmv);
    }
    
    public ChunkBufferBuilderPack takeChunkBufferBuilder() throws InterruptedException {
        return (ChunkBufferBuilderPack)this.availableChunkBuffers.take();
    }
    
    public ChunkCompileTask takeChunk() throws InterruptedException {
        return (ChunkCompileTask)this.chunksToBatch.take();
    }
    
    public boolean resortChunkTransparencyAsync(final RenderChunk dpy) {
        dpy.getTaskLock().lock();
        try {
            final ChunkCompileTask dpt3 = dpy.createTransparencySortTask();
            if (dpt3 != null) {
                dpt3.addCancelListener(() -> this.chunksToBatch.remove(dpt3));
                return this.chunksToBatch.offer(dpt3);
            }
            return true;
        }
        finally {
            dpy.getTaskLock().unlock();
        }
    }
    
    public ListenableFuture<Void> uploadChunkLayer(final BlockLayer bhc, final BufferBuilder cuw, final RenderChunk dpy, final CompiledChunk dpw, final double double5) {
        if (Minecraft.getInstance().isSameThread()) {
            if (GLX.useVbo()) {
                this.uploadChunkLayer(cuw, dpy.getBuffer(bhc.ordinal()));
            }
            else {
                this.compileChunkLayerIntoGlList(cuw, ((ListedRenderChunk)dpy).getGlListId(bhc, dpw));
            }
            cuw.offset(0.0, 0.0, 0.0);
            return (ListenableFuture<Void>)Futures.immediateFuture(null);
        }
        final ListenableFutureTask<Void> listenableFutureTask8 = (ListenableFutureTask<Void>)ListenableFutureTask.create(() -> this.uploadChunkLayer(bhc, cuw, dpy, dpw, double5), null);
        synchronized (this.pendingUploads) {
            this.pendingUploads.add(new PendingUpload(listenableFutureTask8, double5));
        }
        return (ListenableFuture<Void>)listenableFutureTask8;
    }
    
    private void compileChunkLayerIntoGlList(final BufferBuilder cuw, final int integer) {
        GlStateManager.newList(integer, 4864);
        this.uploader.end(cuw);
        GlStateManager.endList();
    }
    
    private void uploadChunkLayer(final BufferBuilder cuw, final VertexBuffer cva) {
        this.vboUploader.setBuffer(cva);
        this.vboUploader.end(cuw);
    }
    
    public void clearBatchQueue() {
        while (!this.chunksToBatch.isEmpty()) {
            final ChunkCompileTask dpt2 = (ChunkCompileTask)this.chunksToBatch.poll();
            if (dpt2 != null) {
                dpt2.cancel();
            }
        }
    }
    
    public boolean isQueueEmpty() {
        return this.chunksToBatch.isEmpty() && this.pendingUploads.isEmpty();
    }
    
    public void dispose() {
        this.clearBatchQueue();
        for (final ChunkRenderWorker dpv3 : this.workers) {
            dpv3.stop();
        }
        for (final Thread thread3 : this.threads) {
            try {
                thread3.interrupt();
                thread3.join();
            }
            catch (InterruptedException interruptedException4) {
                ChunkRenderDispatcher.LOGGER.warn("Interrupted whilst waiting for worker to die", (Throwable)interruptedException4);
            }
        }
        this.availableChunkBuffers.clear();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(ChunkRenderDispatcher.LOGGER)).build();
    }
    
    class PendingUpload implements Comparable<PendingUpload> {
        private final ListenableFutureTask<Void> future;
        private final double dist;
        
        public PendingUpload(final ListenableFutureTask<Void> listenableFutureTask, final double double3) {
            this.future = listenableFutureTask;
            this.dist = double3;
        }
        
        public int compareTo(final PendingUpload a) {
            return Doubles.compare(this.dist, a.dist);
        }
    }
}
