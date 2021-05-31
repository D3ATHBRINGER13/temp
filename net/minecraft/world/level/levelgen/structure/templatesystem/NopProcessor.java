package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class NopProcessor extends StructureProcessor {
    public static final NopProcessor INSTANCE;
    
    private NopProcessor() {
    }
    
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq) {
        return b4;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.NOP;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    static {
        INSTANCE = new NopProcessor();
    }
}
