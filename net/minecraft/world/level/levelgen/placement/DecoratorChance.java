package net.minecraft.world.level.levelgen.placement;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorChance implements DecoratorConfiguration {
    public final int chance;
    
    public DecoratorChance(final int integer) {
        this.chance = integer;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.chance))));
    }
    
    public static DecoratorChance deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("chance").asInt(0);
        return new DecoratorChance(integer2);
    }
}
