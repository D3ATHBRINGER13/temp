package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.block.Block;

public class RandomBlockMatchTest extends RuleTest {
    private final Block block;
    private final float probability;
    
    public RandomBlockMatchTest(final Block bmv, final float float2) {
        this.block = bmv;
        this.probability = float2;
    }
    
    public <T> RandomBlockMatchTest(final Dynamic<T> dynamic) {
        this(Registry.BLOCK.get(new ResourceLocation(dynamic.get("block").asString(""))), dynamic.get("probability").asFloat(1.0f));
    }
    
    @Override
    public boolean test(final BlockState bvt, final Random random) {
        return bvt.getBlock() == this.block && random.nextFloat() < this.probability;
    }
    
    @Override
    protected RuleTestType getType() {
        return RuleTestType.RANDOM_BLOCK_TEST;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getKey(this.block).toString()), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }
}
