package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface DecoratorConfiguration {
    public static final NoneDecoratorConfiguration NONE = new NoneDecoratorConfiguration();
    
     <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps);
}
