package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.Tag;

public class TagMatchTest extends RuleTest {
    private final Tag<Block> tag;
    
    public TagMatchTest(final Tag<Block> zg) {
        this.tag = zg;
    }
    
    public <T> TagMatchTest(final Dynamic<T> dynamic) {
        this(BlockTags.getAllTags().getTag(new ResourceLocation(dynamic.get("tag").asString(""))));
    }
    
    @Override
    public boolean test(final BlockState bvt, final Random random) {
        return bvt.is(this.tag);
    }
    
    @Override
    protected RuleTestType getType() {
        return RuleTestType.TAG_TEST;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("tag"), dynamicOps.createString(this.tag.getId().toString()))));
    }
}
