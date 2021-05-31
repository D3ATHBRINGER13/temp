package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatchTest extends RuleTest {
    private final BlockState blockState;
    
    public BlockStateMatchTest(final BlockState bvt) {
        this.blockState = bvt;
    }
    
    public <T> BlockStateMatchTest(final Dynamic<T> dynamic) {
        this(BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic.get("blockstate").orElseEmptyMap()));
    }
    
    @Override
    public boolean test(final BlockState bvt, final Random random) {
        return bvt == this.blockState;
    }
    
    @Override
    protected RuleTestType getType() {
        return RuleTestType.BLOCKSTATE_TEST;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.<T>serialize(dynamicOps, this.blockState).getValue())));
    }
}
