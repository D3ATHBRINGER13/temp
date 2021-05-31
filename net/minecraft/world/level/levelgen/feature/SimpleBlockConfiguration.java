package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBlockConfiguration implements FeatureConfiguration {
    protected final BlockState toPlace;
    protected final List<BlockState> placeOn;
    protected final List<BlockState> placeIn;
    protected final List<BlockState> placeUnder;
    
    public SimpleBlockConfiguration(final BlockState bvt, final List<BlockState> list2, final List<BlockState> list3, final List<BlockState> list4) {
        this.toPlace = bvt;
        this.placeOn = list2;
        this.placeIn = list3;
        this.placeUnder = list4;
    }
    
    public SimpleBlockConfiguration(final BlockState bvt, final BlockState[] arr2, final BlockState[] arr3, final BlockState[] arr4) {
        this(bvt, (List<BlockState>)Lists.newArrayList((Object[])arr2), (List<BlockState>)Lists.newArrayList((Object[])arr3), (List<BlockState>)Lists.newArrayList((Object[])arr4));
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)BlockState.<T>serialize(dynamicOps, this.toPlace).getValue();
        final T object4 = (T)dynamicOps.createList(this.placeOn.stream().map(bvt -> BlockState.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps, bvt).getValue()));
        final T object5 = (T)dynamicOps.createList(this.placeIn.stream().map(bvt -> BlockState.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps, bvt).getValue()));
        final T object6 = (T)dynamicOps.createList(this.placeUnder.stream().map(bvt -> BlockState.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps, bvt).getValue()));
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("to_place"), object3, dynamicOps.createString("place_on"), object4, dynamicOps.createString("place_in"), object5, dynamicOps.createString("place_under"), object6)));
    }
    
    public static <T> SimpleBlockConfiguration deserialize(final Dynamic<T> dynamic) {
        final BlockState bvt2 = (BlockState)dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        final List<BlockState> list3 = (List<BlockState>)dynamic.get("place_on").asList(BlockState::deserialize);
        final List<BlockState> list4 = (List<BlockState>)dynamic.get("place_in").asList(BlockState::deserialize);
        final List<BlockState> list5 = (List<BlockState>)dynamic.get("place_under").asList(BlockState::deserialize);
        return new SimpleBlockConfiguration(bvt2, list3, list4, list5);
    }
}
