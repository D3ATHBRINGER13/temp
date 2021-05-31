package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Deserializer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ProcessorRule {
    private final RuleTest inputPredicate;
    private final RuleTest locPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag outputTag;
    
    public ProcessorRule(final RuleTest cjn1, final RuleTest cjn2, final BlockState bvt) {
        this(cjn1, cjn2, bvt, null);
    }
    
    public ProcessorRule(final RuleTest cjn1, final RuleTest cjn2, final BlockState bvt, @Nullable final CompoundTag id) {
        this.inputPredicate = cjn1;
        this.locPredicate = cjn2;
        this.outputState = bvt;
        this.outputTag = id;
    }
    
    public boolean test(final BlockState bvt1, final BlockState bvt2, final Random random) {
        return this.inputPredicate.test(bvt1, random) && this.locPredicate.test(bvt2, random);
    }
    
    public BlockState getOutputState() {
        return this.outputState;
    }
    
    @Nullable
    public CompoundTag getOutputTag() {
        return this.outputTag;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("input_predicate"), this.inputPredicate.<T>serialize(dynamicOps).getValue(), dynamicOps.createString("location_predicate"), this.locPredicate.<T>serialize(dynamicOps).getValue(), dynamicOps.createString("output_state"), BlockState.<T>serialize(dynamicOps, this.outputState).getValue()));
        if (this.outputTag == null) {
            return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object3);
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(object3, dynamicOps.createString("output_nbt"), new Dynamic((DynamicOps)NbtOps.INSTANCE, this.outputTag).convert((DynamicOps)dynamicOps).getValue()));
    }
    
    public static <T> ProcessorRule deserialize(final Dynamic<T> dynamic) {
        final Dynamic<T> dynamic2 = (Dynamic<T>)dynamic.get("input_predicate").orElseEmptyMap();
        final Dynamic<T> dynamic3 = (Dynamic<T>)dynamic.get("location_predicate").orElseEmptyMap();
        final RuleTest cjn4 = Deserializer.<T, AlwaysTrueTest, RuleTestType>deserialize(dynamic2, Registry.RULE_TEST, "predicate_type", AlwaysTrueTest.INSTANCE);
        final RuleTest cjn5 = Deserializer.<T, AlwaysTrueTest, RuleTestType>deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueTest.INSTANCE);
        final BlockState bvt6 = BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("output_state").orElseEmptyMap());
        final CompoundTag id7 = (CompoundTag)dynamic.get("output_nbt").map(dynamic -> (Tag)dynamic.convert((DynamicOps)NbtOps.INSTANCE).getValue()).orElse(null);
        return new ProcessorRule(cjn4, cjn5, bvt6, id7);
    }
}
