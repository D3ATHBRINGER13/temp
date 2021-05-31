package net.minecraft.server.packs.repository;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.function.Function;
import com.google.common.base.Functions;
import java.util.Objects;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackRepository<T extends UnopenedPack> implements AutoCloseable {
    private final Set<RepositorySource> sources;
    private final Map<String, T> available;
    private final List<T> selected;
    private final UnopenedPack.UnopenedPackConstructor<T> constructor;
    
    public PackRepository(final UnopenedPack.UnopenedPackConstructor<T> b) {
        this.sources = (Set<RepositorySource>)Sets.newHashSet();
        this.available = (Map<String, T>)Maps.newLinkedHashMap();
        this.selected = (List<T>)Lists.newLinkedList();
        this.constructor = b;
    }
    
    public void reload() {
        this.close();
        final Set<String> set2 = (Set<String>)this.selected.stream().map(UnopenedPack::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        this.available.clear();
        this.selected.clear();
        for (final RepositorySource wy4 : this.sources) {
            wy4.<T>loadPacks(this.available, this.constructor);
        }
        this.sortAvailable();
        this.selected.addAll((Collection)set2.stream().map(this.available::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
        for (final T xa4 : this.available.values()) {
            if (xa4.isRequired() && !this.selected.contains(xa4)) {
                xa4.getDefaultPosition().<T, UnopenedPack>insert(this.selected, xa4, (java.util.function.Function<T, UnopenedPack>)Functions.identity(), false);
            }
        }
    }
    
    private void sortAvailable() {
        final List<Map.Entry<String, T>> list2 = (List<Map.Entry<String, T>>)Lists.newArrayList((Iterable)this.available.entrySet());
        this.available.clear();
        list2.stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
            final UnopenedPack unopenedPack = (UnopenedPack)this.available.put(entry.getKey(), entry.getValue());
        });
    }
    
    public void setSelected(final Collection<T> collection) {
        this.selected.clear();
        this.selected.addAll((Collection)collection);
        for (final T xa4 : this.available.values()) {
            if (xa4.isRequired() && !this.selected.contains(xa4)) {
                xa4.getDefaultPosition().<T, UnopenedPack>insert(this.selected, xa4, (java.util.function.Function<T, UnopenedPack>)Functions.identity(), false);
            }
        }
    }
    
    public Collection<T> getAvailable() {
        return (Collection<T>)this.available.values();
    }
    
    public Collection<T> getUnselected() {
        final Collection<T> collection2 = (Collection<T>)Lists.newArrayList((Iterable)this.available.values());
        collection2.removeAll((Collection)this.selected);
        return collection2;
    }
    
    public Collection<T> getSelected() {
        return (Collection<T>)this.selected;
    }
    
    @Nullable
    public T getPack(final String string) {
        return (T)this.available.get(string);
    }
    
    public void addSource(final RepositorySource wy) {
        this.sources.add(wy);
    }
    
    public void close() {
        this.available.values().forEach(UnopenedPack::close);
    }
}
