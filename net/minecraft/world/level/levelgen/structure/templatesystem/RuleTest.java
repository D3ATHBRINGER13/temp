package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RuleTest {
    public abstract boolean test(final BlockState bvt, final Random random);
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(this.getDynamic((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue(), dynamicOps.createString("predicate_type"), dynamicOps.createString(Registry.RULE_TEST.getKey(this.getType()).toString())));
    }
    
    protected abstract RuleTestType getType();
    
    protected abstract <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps);
}
