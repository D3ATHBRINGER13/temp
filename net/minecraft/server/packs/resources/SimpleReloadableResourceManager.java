package net.minecraft.server.packs.resources;

import org.apache.logging.log4j.LogManager;
import java.util.stream.Collectors;
import java.util.ArrayList;
import net.minecraft.util.Unit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.Collections;
import java.util.Collection;
import java.util.function.Predicate;
import java.io.IOException;
import java.io.FileNotFoundException;
import net.minecraft.resources.ResourceLocation;
import java.util.Iterator;
import net.minecraft.server.packs.Pack;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.packs.PackType;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements ReloadableResourceManager {
    private static final Logger LOGGER;
    private final Map<String, FallbackResourceManager> namespacedPacks;
    private final List<PreparableReloadListener> listeners;
    private final List<PreparableReloadListener> recentlyRegistered;
    private final Set<String> namespaces;
    private final PackType type;
    private final Thread mainThread;
    
    public SimpleReloadableResourceManager(final PackType wm, final Thread thread) {
        this.namespacedPacks = (Map<String, FallbackResourceManager>)Maps.newHashMap();
        this.listeners = (List<PreparableReloadListener>)Lists.newArrayList();
        this.recentlyRegistered = (List<PreparableReloadListener>)Lists.newArrayList();
        this.namespaces = (Set<String>)Sets.newLinkedHashSet();
        this.type = wm;
        this.mainThread = thread;
    }
    
    public void add(final Pack wl) {
        for (final String string4 : wl.getNamespaces(this.type)) {
            this.namespaces.add(string4);
            FallbackResourceManager xc5 = (FallbackResourceManager)this.namespacedPacks.get(string4);
            if (xc5 == null) {
                xc5 = new FallbackResourceManager(this.type);
                this.namespacedPacks.put(string4, xc5);
            }
            xc5.add(wl);
        }
    }
    
    public Set<String> getNamespaces() {
        return this.namespaces;
    }
    
    public Resource getResource(final ResourceLocation qv) throws IOException {
        final ResourceManager xi3 = (ResourceManager)this.namespacedPacks.get(qv.getNamespace());
        if (xi3 != null) {
            return xi3.getResource(qv);
        }
        throw new FileNotFoundException(qv.toString());
    }
    
    public boolean hasResource(final ResourceLocation qv) {
        final ResourceManager xi3 = (ResourceManager)this.namespacedPacks.get(qv.getNamespace());
        return xi3 != null && xi3.hasResource(qv);
    }
    
    public List<Resource> getResources(final ResourceLocation qv) throws IOException {
        final ResourceManager xi3 = (ResourceManager)this.namespacedPacks.get(qv.getNamespace());
        if (xi3 != null) {
            return xi3.getResources(qv);
        }
        throw new FileNotFoundException(qv.toString());
    }
    
    public Collection<ResourceLocation> listResources(final String string, final Predicate<String> predicate) {
        final Set<ResourceLocation> set4 = (Set<ResourceLocation>)Sets.newHashSet();
        for (final FallbackResourceManager xc6 : this.namespacedPacks.values()) {
            set4.addAll((Collection)xc6.listResources(string, predicate));
        }
        final List<ResourceLocation> list5 = (List<ResourceLocation>)Lists.newArrayList((Iterable)set4);
        Collections.sort((List)list5);
        return (Collection<ResourceLocation>)list5;
    }
    
    private void clear() {
        this.namespacedPacks.clear();
        this.namespaces.clear();
    }
    
    public CompletableFuture<Unit> reload(final Executor executor1, final Executor executor2, final List<Pack> list, final CompletableFuture<Unit> completableFuture) {
        final ReloadInstance xf6 = this.createFullReload(executor1, executor2, completableFuture, list);
        return xf6.done();
    }
    
    public void registerReloadListener(final PreparableReloadListener xd) {
        this.listeners.add(xd);
        this.recentlyRegistered.add(xd);
    }
    
    protected ReloadInstance createReload(final Executor executor1, final Executor executor2, final List<PreparableReloadListener> list, final CompletableFuture<Unit> completableFuture) {
        ReloadInstance xf6;
        if (SimpleReloadableResourceManager.LOGGER.isDebugEnabled()) {
            xf6 = new ProfiledReloadInstance(this, (List<PreparableReloadListener>)new ArrayList((Collection)list), executor1, executor2, completableFuture);
        }
        else {
            xf6 = SimpleReloadInstance.of(this, (List<PreparableReloadListener>)new ArrayList((Collection)list), executor1, executor2, completableFuture);
        }
        this.recentlyRegistered.clear();
        return xf6;
    }
    
    public ReloadInstance createQueuedReload(final Executor executor1, final Executor executor2, final CompletableFuture<Unit> completableFuture) {
        return this.createReload(executor1, executor2, this.recentlyRegistered, completableFuture);
    }
    
    public ReloadInstance createFullReload(final Executor executor1, final Executor executor2, final CompletableFuture<Unit> completableFuture, final List<Pack> list) {
        this.clear();
        SimpleReloadableResourceManager.LOGGER.info("Reloading ResourceManager: {}", list.stream().map(Pack::getName).collect(Collectors.joining(", ")));
        for (final Pack wl7 : list) {
            this.add(wl7);
        }
        return this.createReload(executor1, executor2, this.listeners, completableFuture);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
