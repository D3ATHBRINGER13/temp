package net.minecraft.core;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Serializable;

public final class SerializableLong implements Serializable {
    private final long value;
    
    private SerializableLong(final long long1) {
        this.value = long1;
    }
    
    public long value() {
        return this.value;
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createLong(this.value);
    }
    
    public static SerializableLong of(final Dynamic<?> dynamic) {
        return new SerializableLong(dynamic.asNumber((Number)0).longValue());
    }
    
    public static SerializableLong of(final long long1) {
        return new SerializableLong(long1);
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final SerializableLong fq3 = (SerializableLong)object;
        return this.value == fq3.value;
    }
    
    public int hashCode() {
        return Long.hashCode(this.value);
    }
    
    public String toString() {
        return Long.toString(this.value);
    }
}
