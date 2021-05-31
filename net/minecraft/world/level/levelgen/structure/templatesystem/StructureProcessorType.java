package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;

public interface StructureProcessorType extends Deserializer<StructureProcessor> {
    public static final StructureProcessorType BLOCK_IGNORE = register("block_ignore", BlockIgnoreProcessor::new);
    public static final StructureProcessorType BLOCK_ROT = register("block_rot", BlockRotProcessor::new);
    public static final StructureProcessorType GRAVITY = register("gravity", GravityProcessor::new);
    public static final StructureProcessorType JIGSAW_REPLACEMENT = register("jigsaw_replacement", dynamic -> JigsawReplacementProcessor.INSTANCE);
    public static final StructureProcessorType RULE = register("rule", RuleProcessor::new);
    public static final StructureProcessorType NOP = register("nop", dynamic -> NopProcessor.INSTANCE);
    
    default StructureProcessorType register(final String string, final StructureProcessorType cjs) {
        return Registry.<StructureProcessorType>register(Registry.STRUCTURE_PROCESSOR, string, cjs);
    }
}
