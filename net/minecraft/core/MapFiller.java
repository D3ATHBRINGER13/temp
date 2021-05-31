package net.minecraft.core;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.google.common.collect.Maps;
import java.util.Map;

public class MapFiller {
    public static <K, V> Map<K, V> linkedHashMapFrom(final Iterable<K> iterable1, final Iterable<V> iterable2) {
        return MapFiller.<K, V>from(iterable1, iterable2, (java.util.Map<K, V>)Maps.newLinkedHashMap());
    }
    
    public static <K, V> Map<K, V> from(final Iterable<K> iterable1, final Iterable<V> iterable2, final Map<K, V> map) {
        final Iterator<V> iterator4 = (Iterator<V>)iterable2.iterator();
        for (final K object6 : iterable1) {
            map.put(object6, iterator4.next());
        }
        if (iterator4.hasNext()) {
            throw new NoSuchElementException();
        }
        return map;
    }
}
