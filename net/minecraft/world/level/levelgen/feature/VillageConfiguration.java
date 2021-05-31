package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.resources.ResourceLocation;

public class VillageConfiguration implements FeatureConfiguration {
    public final ResourceLocation startPool;
    public final int size;
    
    public VillageConfiguration(final String string, final int integer) {
        this.startPool = new ResourceLocation(string);
        this.size = integer;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("start_pool"), dynamicOps.createString(this.startPool.toString()), dynamicOps.createString("size"), dynamicOps.createInt(this.size))));
    }
    
    public static <T> VillageConfiguration deserialize(final Dynamic<T> dynamic) {
        final String string2 = dynamic.get("start_pool").asString("");
        final int integer3 = dynamic.get("size").asInt(6);
        return new VillageConfiguration(string2, integer3);
    }
}
