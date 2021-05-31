package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.level.block.Block;
import com.google.common.collect.ImmutableList;

public class BlockIgnoreProcessor extends StructureProcessor {
    public static final BlockIgnoreProcessor STRUCTURE_BLOCK;
    public static final BlockIgnoreProcessor AIR;
    public static final BlockIgnoreProcessor STRUCTURE_AND_AIR;
    private final ImmutableList<Block> toIgnore;
    
    public BlockIgnoreProcessor(final List<Block> list) {
        this.toIgnore = (ImmutableList<Block>)ImmutableList.copyOf((Collection)list);
    }
    
    public BlockIgnoreProcessor(final Dynamic<?> dynamic) {
        this((List<Block>)dynamic.get("blocks").asList(dynamic -> BlockState.deserialize((com.mojang.datafixers.Dynamic<Object>)dynamic).getBlock()));
    }
    
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq) {
        if (this.toIgnore.contains(b4.state.getBlock())) {
            return null;
        }
        return b4;
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("blocks"), dynamicOps.createList(this.toIgnore.stream().map(bmv -> BlockState.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps, bmv.defaultBlockState()).getValue())))));
    }
    
    static {
        STRUCTURE_BLOCK = new BlockIgnoreProcessor((List<Block>)ImmutableList.of(Blocks.STRUCTURE_BLOCK));
        AIR = new BlockIgnoreProcessor((List<Block>)ImmutableList.of(Blocks.AIR));
        STRUCTURE_AND_AIR = new BlockIgnoreProcessor((List<Block>)ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
    }
}
