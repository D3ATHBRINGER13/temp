package net.minecraft.server.packs.resources;

import net.minecraft.util.Unit;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.packs.Pack;
import java.util.List;
import java.util.concurrent.Executor;

public interface ReloadableResourceManager extends ResourceManager {
    CompletableFuture<Unit> reload(final Executor executor1, final Executor executor2, final List<Pack> list, final CompletableFuture<Unit> completableFuture);
    
    ReloadInstance createQueuedReload(final Executor executor1, final Executor executor2, final CompletableFuture<Unit> completableFuture);
    
    ReloadInstance createFullReload(final Executor executor1, final Executor executor2, final CompletableFuture<Unit> completableFuture, final List<Pack> list);
    
    void registerReloadListener(final PreparableReloadListener xd);
}
