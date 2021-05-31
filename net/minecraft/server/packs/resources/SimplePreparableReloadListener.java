package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
    public final CompletableFuture<Void> reload(final PreparationBarrier a, final ResourceManager xi, final ProfilerFiller agn3, final ProfilerFiller agn4, final Executor executor5, final Executor executor6) {
        return (CompletableFuture<Void>)CompletableFuture.supplyAsync(() -> this.prepare(xi, agn3), executor5).thenCompose(a::wait).thenAcceptAsync(object -> this.apply(object, xi, agn4), executor6);
    }
    
    protected abstract T prepare(final ResourceManager xi, final ProfilerFiller agn);
    
    protected abstract void apply(final T object, final ResourceManager xi, final ProfilerFiller agn);
}
