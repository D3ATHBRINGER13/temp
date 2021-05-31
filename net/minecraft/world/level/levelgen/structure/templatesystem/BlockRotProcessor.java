package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import com.mojang.datafixers.Dynamic;

public class BlockRotProcessor extends StructureProcessor {
    private final float integrity;
    
    public BlockRotProcessor(final float float1) {
        this.integrity = float1;
    }
    
    public BlockRotProcessor(final Dynamic<?> dynamic) {
        this(dynamic.get("integrity").asFloat(1.0f));
    }
    
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq) {
        final Random random7 = cjq.getRandom(b4.pos);
        if (this.integrity >= 1.0f || random7.nextFloat() <= this.integrity) {
            return b4;
        }
        return null;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("integrity"), dynamicOps.createFloat(this.integrity))));
    }
}
