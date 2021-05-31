package net.minecraft.server.packs.resources;

import net.minecraft.util.Unit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
    default CompletableFuture<Void> reload(final PreparationBarrier a, final ResourceManager xi, final ProfilerFiller agn3, final ProfilerFiller agn4, final Executor executor5, final Executor executor6) {
        return (CompletableFuture<Void>)a.<Unit>wait(Unit.INSTANCE).thenRunAsync(() -> this.onResourceManagerReload(xi), executor6);
    }
    
    void onResourceManagerReload(final ResourceManager xi);
}
