package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBlobConfiguration implements FeatureConfiguration {
    public final BlockState state;
    public final int startRadius;
    
    public BlockBlobConfiguration(final BlockState bvt, final int integer) {
        this.state = bvt;
        this.startRadius = integer;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("state"), BlockState.<T>serialize(dynamicOps, this.state).getValue(), dynamicOps.createString("start_radius"), dynamicOps.createInt(this.startRadius))));
    }
    
    public static <T> BlockBlobConfiguration deserialize(final Dynamic<T> dynamic) {
        final BlockState bvt2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        final int integer3 = dynamic.get("start_radius").asInt(0);
        return new BlockBlobConfiguration(bvt2, integer3);
    }
}
