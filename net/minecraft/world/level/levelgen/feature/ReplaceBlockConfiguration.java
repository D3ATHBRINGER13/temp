package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.state.BlockState;

public class ReplaceBlockConfiguration implements FeatureConfiguration {
    public final BlockState target;
    public final BlockState state;
    
    public ReplaceBlockConfiguration(final BlockState bvt1, final BlockState bvt2) {
        this.target = bvt1;
        this.state = bvt2;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("target"), BlockState.<T>serialize(dynamicOps, this.target).getValue(), dynamicOps.createString("state"), BlockState.<T>serialize(dynamicOps, this.state).getValue())));
    }
    
    public static <T> ReplaceBlockConfiguration deserialize(final Dynamic<T> dynamic) {
        final BlockState bvt2 = (BlockState)dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        final BlockState bvt3 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        return new ReplaceBlockConfiguration(bvt2, bvt3);
    }
}
