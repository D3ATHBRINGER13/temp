package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockStateMatchTest extends RuleTest {
    private final BlockState blockState;
    private final float probability;
    
    public RandomBlockStateMatchTest(final BlockState bvt, final float float2) {
        this.blockState = bvt;
        this.probability = float2;
    }
    
    public <T> RandomBlockStateMatchTest(final Dynamic<T> dynamic) {
        this(BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("blockstate").orElseEmptyMap()), dynamic.get("probability").asFloat(1.0f));
    }
    
    @Override
    public boolean test(final BlockState bvt, final Random random) {
        return bvt == this.blockState && random.nextFloat() < this.probability;
    }
    
    @Override
    protected RuleTestType getType() {
        return RuleTestType.RANDOM_BLOCKSTATE_TEST;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.<T>serialize(dynamicOps, this.blockState).getValue(), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
}
