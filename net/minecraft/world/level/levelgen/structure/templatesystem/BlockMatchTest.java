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

public class BlockMatchTest extends RuleTest {
    private final Block block;
    
    public BlockMatchTest(final Block bmv) {
        this.block = bmv;
    }
    
    public <T> BlockMatchTest(final Dynamic<T> dynamic) {
        this(Registry.BLOCK.get(new ResourceLocation(dynamic.get("block").asString(""))));
    }
    
    @Override
    public boolean test(final BlockState bvt, final Random random) {
        return bvt.getBlock() == this.block;
    }
    
    @Override
    protected RuleTestType getType() {
        return RuleTestType.BLOCK_TEST;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getKey(this.block).toString()))));
    }
}
