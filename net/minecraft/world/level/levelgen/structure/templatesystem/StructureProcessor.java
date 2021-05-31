package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public abstract class StructureProcessor {
    @Nullable
    public abstract StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq);
    
    protected abstract StructureProcessorType getType();
    
    protected abstract <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps);
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.mergeInto(this.getDynamic((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue(), dynamicOps.createString("processor_type"), dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getKey(this.getType()).toString())));
    }
}
