package net.minecraft.world.level.levelgen.feature;

import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.block.Block;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Blocks;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.state.BlockState;

public class OreConfiguration implements FeatureConfiguration {
    public final Predicates target;
    public final int size;
    public final BlockState state;
    
    public OreConfiguration(final Predicates a, final BlockState bvt, final int integer) {
        this.size = integer;
        this.state = bvt;
        this.target = a;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("size"), dynamicOps.createInt(this.size), dynamicOps.createString("target"), dynamicOps.createString(this.target.getName()), dynamicOps.createString("state"), BlockState.<T>serialize(dynamicOps, this.state).getValue())));
    }
    
    public static OreConfiguration deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("size").asInt(0);
        final Predicates a3 = Predicates.byName(dynamic.get("target").asString(""));
        final BlockState bvt4 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
        return new OreConfiguration(a3, bvt4, integer2);
    }
    
    public enum Predicates {
        NATURAL_STONE("natural_stone", (Predicate<BlockState>)(bvt -> {
            if (bvt != null) {
                final Block bmv2 = bvt.getBlock();
                return bmv2 == Blocks.STONE || bmv2 == Blocks.GRANITE || bmv2 == Blocks.DIORITE || bmv2 == Blocks.ANDESITE;
            }
            return false;
        })), 
        NETHERRACK("netherrack", (Predicate<BlockState>)new BlockPredicate(Blocks.NETHERRACK));
        
        private static final Map<String, Predicates> BY_NAME;
        private final String name;
        private final Predicate<BlockState> predicate;
        
        private Predicates(final String string3, final Predicate<BlockState> predicate) {
            this.name = string3;
            this.predicate = predicate;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Predicates byName(final String string) {
            return (Predicates)Predicates.BY_NAME.get(string);
        }
        
        public Predicate<BlockState> getPredicate() {
            return this.predicate;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Predicates::getName, a -> a));
        }
    }
}
