package net.minecraft.util;

import com.mojang.datafixers.types.DynamicOps;

public interface Serializable {
     <T> T serialize(final DynamicOps<T> dynamicOps);
}
