package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ImmutableList;

public class RuleProcessor extends StructureProcessor {
    private final ImmutableList<ProcessorRule> rules;
    
    public RuleProcessor(final List<ProcessorRule> list) {
        this.rules = (ImmutableList<ProcessorRule>)ImmutableList.copyOf((Collection)list);
    }
    
    public RuleProcessor(final Dynamic<?> dynamic) {
        this((List<ProcessorRule>)dynamic.get("rules").asList(ProcessorRule::deserialize));
    }
    
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq) {
        final Random random7 = new Random(Mth.getSeed(b4.pos));
        final BlockState bvt8 = bhu.getBlockState(b4.pos);
        for (final ProcessorRule cjj10 : this.rules) {
            if (cjj10.test(b4.state, bvt8, random7)) {
                return new StructureTemplate.StructureBlockInfo(b4.pos, cjj10.getOutputState(), cjj10.getOutputTag());
            }
        }
        return b4;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.RULE;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("rules"), dynamicOps.createList(this.rules.stream().map(cjj -> cjj.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue())))));
    }
}
