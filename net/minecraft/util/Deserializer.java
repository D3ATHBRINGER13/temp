package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import org.apache.logging.log4j.Logger;

public interface Deserializer<T> {
    public static final Logger LOGGER = LogManager.getLogger();
    
    T deserialize(final Dynamic<?> dynamic);
    
    default <T, V, U extends Deserializer<V>> V deserialize(final Dynamic<T> dynamic, final Registry<U> fn, final String string, final V object) {
        final U zq5 = fn.get(new ResourceLocation(dynamic.get(string).asString("")));
        V object2;
        if (zq5 != null) {
            object2 = zq5.deserialize(dynamic);
        }
        else {
            Deserializer.LOGGER.error("Unknown type {}, replacing with {}", dynamic.get(string).asString(""), object);
            object2 = object;
        }
        return object2;
    }
}
