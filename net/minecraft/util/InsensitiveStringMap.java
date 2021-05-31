package net.minecraft.util;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Locale;
import com.google.common.collect.Maps;
import java.util.Map;

public class InsensitiveStringMap<V> implements Map<String, V> {
    private final Map<String, V> map;
    
    public InsensitiveStringMap() {
        this.map = (Map<String, V>)Maps.newLinkedHashMap();
    }
    
    public int size() {
        return this.map.size();
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public boolean containsKey(final Object object) {
        return this.map.containsKey(object.toString().toLowerCase(Locale.ROOT));
    }
    
    public boolean containsValue(final Object object) {
        return this.map.containsValue(object);
    }
    
    public V get(final Object object) {
        return (V)this.map.get(object.toString().toLowerCase(Locale.ROOT));
    }
    
    public V put(final String string, final V object) {
        return (V)this.map.put(string.toLowerCase(Locale.ROOT), object);
    }
    
    public V remove(final Object object) {
        return (V)this.map.remove(object.toString().toLowerCase(Locale.ROOT));
    }
    
    public void putAll(final Map<? extends String, ? extends V> map) {
        for (final Map.Entry<? extends String, ? extends V> entry4 : map.entrySet()) {
            this.put((String)entry4.getKey(), entry4.getValue());
        }
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public Set<String> keySet() {
        return (Set<String>)this.map.keySet();
    }
    
    public Collection<V> values() {
        return (Collection<V>)this.map.values();
    }
    
    public Set<Map.Entry<String, V>> entrySet() {
        return (Set<Map.Entry<String, V>>)this.map.entrySet();
    }
}
