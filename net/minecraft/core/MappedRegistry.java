package net.minecraft.core;

import org.apache.logging.log4j.LogManager;
import java.util.Collection;
import java.util.Random;
import java.util.Collections;
import java.util.Set;
import java.util.Optional;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.BiMap;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import org.apache.logging.log4j.Logger;

public class MappedRegistry<T> extends WritableRegistry<T> {
    protected static final Logger LOGGER;
    protected final CrudeIncrementalIntIdentityHashBiMap<T> map;
    protected final BiMap<ResourceLocation, T> storage;
    protected Object[] randomCache;
    private int nextId;
    
    public MappedRegistry() {
        this.map = new CrudeIncrementalIntIdentityHashBiMap<T>(256);
        this.storage = (BiMap<ResourceLocation, T>)HashBiMap.create();
    }
    
    @Override
    public <V extends T> V registerMapping(final int integer, final ResourceLocation qv, final V object) {
        this.map.addMapping(object, integer);
        Validate.notNull(qv);
        Validate.notNull(object);
        this.randomCache = null;
        if (this.storage.containsKey(qv)) {
            MappedRegistry.LOGGER.debug("Adding duplicate key '{}' to registry", qv);
        }
        this.storage.put(qv, object);
        if (this.nextId <= integer) {
            this.nextId = integer + 1;
        }
        return object;
    }
    
    @Override
    public <V extends T> V register(final ResourceLocation qv, final V object) {
        return this.<V>registerMapping(this.nextId, qv, object);
    }
    
    @Nullable
    @Override
    public ResourceLocation getKey(final T object) {
        return (ResourceLocation)this.storage.inverse().get(object);
    }
    
    @Override
    public int getId(@Nullable final T object) {
        return this.map.getId(object);
    }
    
    @Nullable
    public T byId(final int integer) {
        return this.map.byId(integer);
    }
    
    public Iterator<T> iterator() {
        return this.map.iterator();
    }
    
    @Nullable
    @Override
    public T get(@Nullable final ResourceLocation qv) {
        return (T)this.storage.get(qv);
    }
    
    @Override
    public Optional<T> getOptional(@Nullable final ResourceLocation qv) {
        return (Optional<T>)Optional.ofNullable(this.storage.get(qv));
    }
    
    @Override
    public Set<ResourceLocation> keySet() {
        return (Set<ResourceLocation>)Collections.unmodifiableSet(this.storage.keySet());
    }
    
    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }
    
    @Nullable
    @Override
    public T getRandom(final Random random) {
        if (this.randomCache == null) {
            final Collection<?> collection3 = this.storage.values();
            if (collection3.isEmpty()) {
                return null;
            }
            this.randomCache = collection3.toArray(new Object[collection3.size()]);
        }
        return (T)this.randomCache[random.nextInt(this.randomCache.length)];
    }
    
    @Override
    public boolean containsKey(final ResourceLocation qv) {
        return this.storage.containsKey(qv);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
