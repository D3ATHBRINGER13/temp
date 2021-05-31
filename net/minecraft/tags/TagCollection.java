package net.minecraft.tags;

import org.apache.logging.log4j.LogManager;
import java.util.function.Consumer;
import net.minecraft.Util;
import java.io.InputStream;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.packs.resources.Resource;
import java.util.function.Predicate;
import com.google.common.collect.Maps;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final int PATH_SUFFIX_LENGTH;
    private Map<ResourceLocation, Tag<T>> tags;
    private final Function<ResourceLocation, Optional<T>> idToValue;
    private final String directory;
    private final boolean ordered;
    private final String name;
    
    public TagCollection(final Function<ResourceLocation, Optional<T>> function, final String string2, final boolean boolean3, final String string4) {
        this.tags = (Map<ResourceLocation, Tag<T>>)ImmutableMap.of();
        this.idToValue = function;
        this.directory = string2;
        this.ordered = boolean3;
        this.name = string4;
    }
    
    @Nullable
    public Tag<T> getTag(final ResourceLocation qv) {
        return (Tag<T>)this.tags.get(qv);
    }
    
    public Tag<T> getTagOrEmpty(final ResourceLocation qv) {
        final Tag<T> zg3 = (Tag<T>)this.tags.get(qv);
        if (zg3 == null) {
            return new Tag<T>(qv);
        }
        return zg3;
    }
    
    public Collection<ResourceLocation> getAvailableTags() {
        return (Collection<ResourceLocation>)this.tags.keySet();
    }
    
    public Collection<ResourceLocation> getMatchingTags(final T object) {
        final List<ResourceLocation> list3 = (List<ResourceLocation>)Lists.newArrayList();
        for (final Map.Entry<ResourceLocation, Tag<T>> entry5 : this.tags.entrySet()) {
            if (((Tag)entry5.getValue()).contains(object)) {
                list3.add(entry5.getKey());
            }
        }
        return (Collection<ResourceLocation>)list3;
    }
    
    public CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>> prepare(final ResourceManager xi, final Executor executor) {
        return (CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>>)CompletableFuture.supplyAsync(() -> {
            final Map<ResourceLocation, Tag.Builder<T>> map3 = (Map<ResourceLocation, Tag.Builder<T>>)Maps.newHashMap();
            for (final ResourceLocation qv5 : xi.listResources(this.directory, (Predicate<String>)(string -> string.endsWith(".json")))) {
                final String string6 = qv5.getPath();
                final ResourceLocation qv6 = new ResourceLocation(qv5.getNamespace(), string6.substring(this.directory.length() + 1, string6.length() - TagCollection.PATH_SUFFIX_LENGTH));
                try {
                    for (final Resource xh9 : xi.getResources(qv5)) {
                        try (final InputStream inputStream10 = xh9.getInputStream();
                             final Reader reader12 = (Reader)new BufferedReader((Reader)new InputStreamReader(inputStream10, StandardCharsets.UTF_8))) {
                            final JsonObject jsonObject14 = GsonHelper.<JsonObject>fromJson(TagCollection.GSON, reader12, JsonObject.class);
                            if (jsonObject14 == null) {
                                TagCollection.LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.name, qv6, qv5, xh9.getSourceName());
                            }
                            else {
                                ((Tag.Builder)map3.computeIfAbsent(qv6, qv -> Util.<Tag.Builder<Object>>make(Tag.Builder.tag(), (java.util.function.Consumer<Tag.Builder<Object>>)(a -> a.keepOrder(this.ordered))))).addFromJson(this.idToValue, jsonObject14);
                            }
                        }
                        catch (IOException ex) {}
                        catch (RuntimeException exception10) {
                            TagCollection.LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.name, qv6, qv5, xh9.getSourceName(), exception10);
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)xh9);
                        }
                    }
                }
                catch (IOException iOException8) {
                    TagCollection.LOGGER.error("Couldn't read {} tag list {} from {}", this.name, qv6, qv5, iOException8);
                }
            }
            return map3;
        }, executor);
    }
    
    public void load(final Map<ResourceLocation, Tag.Builder<T>> map) {
        final Map<ResourceLocation, Tag<T>> map2 = (Map<ResourceLocation, Tag<T>>)Maps.newHashMap();
        while (!map.isEmpty()) {
            boolean boolean4 = false;
            final Iterator<Map.Entry<ResourceLocation, Tag.Builder<T>>> iterator5 = (Iterator<Map.Entry<ResourceLocation, Tag.Builder<T>>>)map.entrySet().iterator();
            while (iterator5.hasNext()) {
                final Map.Entry<ResourceLocation, Tag.Builder<T>> entry6 = (Map.Entry<ResourceLocation, Tag.Builder<T>>)iterator5.next();
                final Tag.Builder<T> a7 = (Tag.Builder<T>)entry6.getValue();
                if (a7.canBuild((java.util.function.Function<ResourceLocation, Tag<T>>)map2::get)) {
                    boolean4 = true;
                    final ResourceLocation qv8 = (ResourceLocation)entry6.getKey();
                    map2.put(qv8, a7.build(qv8));
                    iterator5.remove();
                }
            }
            if (!boolean4) {
                map.forEach((qv, a) -> TagCollection.LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.name, qv));
                break;
            }
        }
        map.forEach((qv, a) -> {
            final Tag tag = (Tag)map2.put(qv, a.build(qv));
        });
        this.replace(map2);
    }
    
    protected void replace(final Map<ResourceLocation, Tag<T>> map) {
        this.tags = (Map<ResourceLocation, Tag<T>>)ImmutableMap.copyOf((Map)map);
    }
    
    public Map<ResourceLocation, Tag<T>> getAllTags() {
        return this.tags;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new Gson();
        PATH_SUFFIX_LENGTH = ".json".length();
    }
}
