package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;

public interface RuleTestType extends Deserializer<RuleTest> {
    public static final RuleTestType ALWAYS_TRUE_TEST = register("always_true", dynamic -> AlwaysTrueTest.INSTANCE);
    public static final RuleTestType BLOCK_TEST = register("block_match", BlockMatchTest::new);
    public static final RuleTestType BLOCKSTATE_TEST = register("blockstate_match", BlockStateMatchTest::new);
    public static final RuleTestType TAG_TEST = register("tag_match", TagMatchTest::new);
    public static final RuleTestType RANDOM_BLOCK_TEST = register("random_block_match", RandomBlockMatchTest::new);
    public static final RuleTestType RANDOM_BLOCKSTATE_TEST = register("random_blockstate_match", RandomBlockStateMatchTest::new);
    
    default RuleTestType register(final String string, final RuleTestType cjo) {
        return Registry.<RuleTestType>register(Registry.RULE_TEST, string, cjo);
    }
}
