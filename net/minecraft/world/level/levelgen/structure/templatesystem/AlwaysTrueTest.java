package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class AlwaysTrueTest extends RuleTest {
    public static final AlwaysTrueTest INSTANCE;
    
    private AlwaysTrueTest() {
    }
    
    @Override
    public boolean test(final BlockState bvt, final Random random) {
        return true;
    }
    
    @Override
    protected RuleTestType getType() {
        return RuleTestType.ALWAYS_TRUE_TEST;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    static {
        INSTANCE = new AlwaysTrueTest();
    }
}
