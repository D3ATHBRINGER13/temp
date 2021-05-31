package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.state.BlockState;

public class LayerConfiguration implements FeatureConfiguration {
    public final int height;
    public final BlockState state;
    
    public LayerConfiguration(final int integer, final BlockState bvt) {
        this.height = integer;
        this.state = bvt;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(this.height), dynamicOps.createString("state"), BlockState.<T>serialize(dynamicOps, this.state).getValue())));
    }
    
    public static <T> LayerConfiguration deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("height").asInt(0);
        final BlockState bvt3 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        return new LayerConfiguration(integer2, bvt3);
    }
}
