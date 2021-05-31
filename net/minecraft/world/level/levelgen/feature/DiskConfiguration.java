package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;

public class DiskConfiguration implements FeatureConfiguration {
    public final BlockState state;
    public final int radius;
    public final int ySize;
    public final List<BlockState> targets;
    
    public DiskConfiguration(final BlockState bvt, final int integer2, final int integer3, final List<BlockState> list) {
        this.state = bvt;
        this.radius = integer2;
        this.ySize = integer3;
        this.targets = list;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("state"), BlockState.<T>serialize(dynamicOps, this.state).getValue(), dynamicOps.createString("radius"), dynamicOps.createInt(this.radius), dynamicOps.createString("y_size"), dynamicOps.createInt(this.ySize), dynamicOps.createString("targets"), dynamicOps.createList(this.targets.stream().map(bvt -> BlockState.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps, bvt).getValue())))));
    }
    
    public static <T> DiskConfiguration deserialize(final Dynamic<T> dynamic) {
        final BlockState bvt2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        final int integer3 = dynamic.get("radius").asInt(0);
        final int integer4 = dynamic.get("y_size").asInt(0);
        final List<BlockState> list5 = (List<BlockState>)dynamic.get("targets").asList(BlockState::deserialize);
        return new DiskConfiguration(bvt2, integer3, integer4, list5);
    }
}
