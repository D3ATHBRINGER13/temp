package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.state.BlockState;

public class BushConfiguration implements FeatureConfiguration {
    public final BlockState state;
    
    public BushConfiguration(final BlockState bvt) {
        this.state = bvt;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("state"), BlockState.<T>serialize(dynamicOps, this.state).getValue())));
    }
    
    public static <T> BushConfiguration deserialize(final Dynamic<T> dynamic) {
        final BlockState bvt2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        return new BushConfiguration(bvt2);
    }
}
