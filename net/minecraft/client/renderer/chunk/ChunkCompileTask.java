package net.minecraft.client.renderer.chunk;

import com.google.common.primitives.Doubles;
import java.util.Iterator;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkCompileTask implements Comparable<ChunkCompileTask> {
    private final RenderChunk chunk;
    private final ReentrantLock lock;
    private final List<Runnable> cancelListeners;
    private final Type type;
    private final double distAtCreation;
    @Nullable
    private RenderChunkRegion region;
    private ChunkBufferBuilderPack builders;
    private CompiledChunk compiledChunk;
    private Status status;
    private boolean isCancelled;
    
    public ChunkCompileTask(final RenderChunk dpy, final Type b, final double double3, @Nullable final RenderChunkRegion dqa) {
        this.lock = new ReentrantLock();
        this.cancelListeners = (List<Runnable>)Lists.newArrayList();
        this.status = Status.PENDING;
        this.chunk = dpy;
        this.type = b;
        this.distAtCreation = double3;
        this.region = dqa;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public RenderChunk getChunk() {
        return this.chunk;
    }
    
    @Nullable
    public RenderChunkRegion takeRegion() {
        final RenderChunkRegion dqa2 = this.region;
        this.region = null;
        return dqa2;
    }
    
    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }
    
    public void setCompiledChunk(final CompiledChunk dpw) {
        this.compiledChunk = dpw;
    }
    
    public ChunkBufferBuilderPack getBuilders() {
        return this.builders;
    }
    
    public void setBuilders(final ChunkBufferBuilderPack dmv) {
        this.builders = dmv;
    }
    
    public void setStatus(final Status a) {
        this.lock.lock();
        try {
            this.status = a;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void cancel() {
        this.lock.lock();
        try {
            this.region = null;
            if (this.type == Type.REBUILD_CHUNK && this.status != Status.DONE) {
                this.chunk.setDirty(false);
            }
            this.isCancelled = true;
            this.status = Status.DONE;
            for (final Runnable runnable3 : this.cancelListeners) {
                runnable3.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addCancelListener(final Runnable runnable) {
        this.lock.lock();
        try {
            this.cancelListeners.add(runnable);
            if (this.isCancelled) {
                runnable.run();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public ReentrantLock getStatusLock() {
        return this.lock;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean wasCancelled() {
        return this.isCancelled;
    }
    
    public int compareTo(final ChunkCompileTask dpt) {
        return Doubles.compare(this.distAtCreation, dpt.distAtCreation);
    }
    
    public double getDistAtCreation() {
        return this.distAtCreation;
    }
    
    public enum Type {
        REBUILD_CHUNK, 
        RESORT_TRANSPARENCY;
    }
    
    public enum Status {
        PENDING, 
        COMPILING, 
        UPLOADING, 
        DONE;
    }
}
