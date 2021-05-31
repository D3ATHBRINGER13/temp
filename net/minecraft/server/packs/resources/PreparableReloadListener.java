package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public interface PreparableReloadListener {
    CompletableFuture<Void> reload(final PreparationBarrier a, final ResourceManager xi, final ProfilerFiller agn3, final ProfilerFiller agn4, final Executor executor5, final Executor executor6);
    
    public interface PreparationBarrier {
         <T> CompletableFuture<T> wait(final T object);
    }
}
