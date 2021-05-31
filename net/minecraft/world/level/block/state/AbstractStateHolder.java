package net.minecraft.world.level.block.state;

import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractStateHolder<O, S> implements StateHolder<S> {
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION;
    protected final O owner;
    private final ImmutableMap<Property<?>, Comparable<?>> values;
    private final int hashCode;
    private Table<Property<?>, Comparable<?>, S> neighbours;
    
    protected AbstractStateHolder(final O object, final ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        this.owner = object;
        this.values = immutableMap;
        this.hashCode = immutableMap.hashCode();
    }
    
    public <T extends Comparable<T>> S cycle(final Property<T> bww) {
        return this.<T, Comparable>setValue(bww, (Comparable)AbstractStateHolder.<V>findNextInCollection((java.util.Collection<V>)bww.getPossibleValues(), (V)this.<T>getValue((Property<T>)bww)));
    }
    
    protected static <T> T findNextInCollection(final Collection<T> collection, final T object) {
        final Iterator<T> iterator3 = (Iterator<T>)collection.iterator();
        while (iterator3.hasNext()) {
            if (iterator3.next().equals(object)) {
                if (iterator3.hasNext()) {
                    return (T)iterator3.next();
                }
                return (T)collection.iterator().next();
            }
        }
        return (T)iterator3.next();
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.owner);
        if (!this.getValues().isEmpty()) {
            stringBuilder2.append('[');
            stringBuilder2.append((String)this.getValues().entrySet().stream().map((Function)AbstractStateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
            stringBuilder2.append(']');
        }
        return stringBuilder2.toString();
    }
    
    public Collection<Property<?>> getProperties() {
        return (Collection<Property<?>>)Collections.unmodifiableCollection((Collection)this.values.keySet());
    }
    
    public <T extends Comparable<T>> boolean hasProperty(final Property<T> bww) {
        return this.values.containsKey(bww);
    }
    
    public <T extends Comparable<T>> T getValue(final Property<T> bww) {
        final Comparable<?> comparable3 = this.values.get(bww);
        if (comparable3 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Cannot get property ").append(bww).append(" as it does not exist in ").append(this.owner).toString());
        }
        return (T)bww.getValueClass().cast(comparable3);
    }
    
    public <T extends Comparable<T>, V extends T> S setValue(final Property<T> bww, final V comparable) {
        final Comparable<?> comparable2 = this.values.get(bww);
        if (comparable2 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Cannot set property ").append(bww).append(" as it does not exist in ").append(this.owner).toString());
        }
        if (comparable2 == comparable) {
            return (S)this;
        }
        final S object5 = (S)this.neighbours.get(bww, comparable);
        if (object5 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Cannot set property ").append(bww).append(" to ").append(comparable).append(" on ").append(this.owner).append(", it is not an allowed value").toString());
        }
        return object5;
    }
    
    public void populateNeighbours(final Map<Map<Property<?>, Comparable<?>>, S> map) {
        if (this.neighbours != null) {
            throw new IllegalStateException();
        }
        final Table<Property<?>, Comparable<?>, S> table3 = (Table<Property<?>, Comparable<?>, S>)HashBasedTable.create();
        for (final Map.Entry<Property<?>, Comparable<?>> entry5 : this.values.entrySet()) {
            final Property<?> bww6 = entry5.getKey();
            for (final Comparable<?> comparable8 : bww6.getPossibleValues()) {
                if (comparable8 != entry5.getValue()) {
                    table3.put(bww6, comparable8, map.get(this.makeNeighbourValues(bww6, comparable8)));
                }
            }
        }
        this.neighbours = (Table<Property<?>, Comparable<?>, S>)(table3.isEmpty() ? table3 : ArrayTable.create((Table)table3));
    }
    
    private Map<Property<?>, Comparable<?>> makeNeighbourValues(final Property<?> bww, final Comparable<?> comparable) {
        final Map<Property<?>, Comparable<?>> map4 = (Map<Property<?>, Comparable<?>>)Maps.newHashMap((Map)this.values);
        map4.put(bww, comparable);
        return map4;
    }
    
    public ImmutableMap<Property<?>, Comparable<?>> getValues() {
        return this.values;
    }
    
    public boolean equals(final Object object) {
        return this == object;
    }
    
    public int hashCode() {
        return this.hashCode;
    }
    
    static {
        PROPERTY_ENTRY_TO_STRING_FUNCTION = (Function)new Function<Map.Entry<Property<?>, Comparable<?>>, String>() {
            public String apply(@Nullable final Map.Entry<Property<?>, Comparable<?>> entry) {
                if (entry == null) {
                    return "<NULL>";
                }
                final Property<?> bww3 = entry.getKey();
                return bww3.getName() + "=" + this.getName(bww3, entry.getValue());
            }
            
            private <T extends Comparable<T>> String getName(final Property<T> bww, final Comparable<?> comparable) {
                return bww.getName((T)comparable);
            }
        };
    }
}
