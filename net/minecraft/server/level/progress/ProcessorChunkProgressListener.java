package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.ChunkPos;
import java.util.concurrent.Executor;
import net.minecraft.util.thread.ProcessorMailbox;

public class ProcessorChunkProgressListener implements ChunkProgressListener {
    private final ChunkProgressListener delegate;
    private final ProcessorMailbox<Runnable> mailbox;
    
    public ProcessorChunkProgressListener(final ChunkProgressListener vt, final Executor executor) {
        this.delegate = vt;
        this.mailbox = ProcessorMailbox.create(executor, "progressListener");
    }
    
    public void updateSpawnPos(final ChunkPos bhd) {
        this.mailbox.tell(() -> this.delegate.updateSpawnPos(bhd));
    }
    
    public void onStatusChange(final ChunkPos bhd, @Nullable final ChunkStatus bxm) {
        this.mailbox.tell(() -> this.delegate.onStatusChange(bhd, bxm));
    }
    
    public void stop() {
        this.mailbox.tell(this.delegate::stop);
    }
}
