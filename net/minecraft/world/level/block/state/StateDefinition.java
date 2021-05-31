package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.MapFiller;
import javax.annotation.Nullable;
import java.util.stream.Collectors;
import com.google.common.base.MoreObjects;
import java.util.Collection;
import java.util.Iterator;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.Collections;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.properties.Property;
import com.google.common.collect.ImmutableSortedMap;
import java.util.regex.Pattern;

public class StateDefinition<O, S extends StateHolder<S>> {
    private static final Pattern NAME_PATTERN;
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> propertiesByName;
    private final ImmutableList<S> states;
    
    protected <A extends AbstractStateHolder<O, S>> StateDefinition(final O object, final Factory<O, S, A> b, final Map<String, Property<?>> map) {
        this.owner = object;
        this.propertiesByName = (ImmutableSortedMap<String, Property<?>>)ImmutableSortedMap.copyOf((Map)map);
        final Map<Map<Property<?>, Comparable<?>>, A> map2 = (Map<Map<Property<?>, Comparable<?>>, A>)Maps.newLinkedHashMap();
        final List<A> list6 = (List<A>)Lists.newArrayList();
        Stream<List<Comparable<?>>> stream7 = (Stream<List<Comparable<?>>>)Stream.of(Collections.emptyList());
        for (final Property<?> bww9 : this.propertiesByName.values()) {
            stream7 = (Stream<List<Comparable<?>>>)stream7.flatMap(list -> bww9.getPossibleValues().stream().map(comparable -> {
                final List<Comparable<?>> list2 = (List<Comparable<?>>)Lists.newArrayList((Iterable)list);
                list2.add(comparable);
                return list2;
            }));
        }
        stream7.forEach(list5 -> {
            final Map<Property<?>, Comparable<?>> map2 = MapFiller.<Property<?>, Comparable<?>>linkedHashMapFrom((java.lang.Iterable<Property<?>>)this.propertiesByName.values(), (java.lang.Iterable<Comparable<?>>)list5);
            final AbstractStateHolder bvs8 = b.create(object, ImmutableMap.copyOf((Map)map2));
            map2.put(map2, bvs8);
            list6.add(bvs8);
        });
        for (final A bvs9 : list6) {
            ((AbstractStateHolder<O, S>)bvs9).populateNeighbours((java.util.Map<Map<Property<?>, Comparable<?>>, S>)map2);
        }
        this.states = (ImmutableList<S>)ImmutableList.copyOf((Iterable)list6);
    }
    
    public ImmutableList<S> getPossibleStates() {
        return this.states;
    }
    
    public S any() {
        return (S)this.states.get(0);
    }
    
    public O getOwner() {
        return this.owner;
    }
    
    public Collection<Property<?>> getProperties() {
        return (Collection<Property<?>>)this.propertiesByName.values();
    }
    
    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }
    
    @Nullable
    public Property<?> getProperty(final String string) {
        return this.propertiesByName.get(string);
    }
    
    static {
        NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    }
    
    public static class Builder<O, S extends StateHolder<S>> {
        private final O owner;
        private final Map<String, Property<?>> properties;
        
        public Builder(final O object) {
            this.properties = (Map<String, Property<?>>)Maps.newHashMap();
            this.owner = object;
        }
        
        public Builder<O, S> add(final Property<?>... arr) {
            for (final Property<?> bww6 : arr) {
                this.validateProperty(bww6);
                this.properties.put(bww6.getName(), bww6);
            }
            return this;
        }
        
        private <T extends Comparable<T>> void validateProperty(final Property<T> bww) {
            final String string3 = bww.getName();
            if (!StateDefinition.NAME_PATTERN.matcher((CharSequence)string3).matches()) {
                throw new IllegalArgumentException(new StringBuilder().append(this.owner).append(" has invalidly named property: ").append(string3).toString());
            }
            final Collection<T> collection4 = bww.getPossibleValues();
            if (collection4.size() <= 1) {
                throw new IllegalArgumentException(new StringBuilder().append(this.owner).append(" attempted use property ").append(string3).append(" with <= 1 possible values").toString());
            }
            for (final T comparable6 : collection4) {
                final String string4 = bww.getName(comparable6);
                if (!StateDefinition.NAME_PATTERN.matcher((CharSequence)string4).matches()) {
                    throw new IllegalArgumentException(new StringBuilder().append(this.owner).append(" has property: ").append(string3).append(" with invalidly named value: ").append(string4).toString());
                }
            }
            if (this.properties.containsKey(string3)) {
                throw new IllegalArgumentException(new StringBuilder().append(this.owner).append(" has duplicate property: ").append(string3).toString());
            }
        }
        
        public <A extends AbstractStateHolder<O, S>> StateDefinition<O, S> create(final Factory<O, S, A> b) {
            return new StateDefinition<O, S>(this.owner, (Factory<O, S, A>)b, this.properties);
        }
    }
    
    public interface Factory<O, S extends StateHolder<S>, A extends AbstractStateHolder<O, S>> {
        A create(final O object, final ImmutableMap<Property<?>, Comparable<?>> immutableMap);
    }
}
