package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import com.mojang.brigadier.StringReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class JigsawReplacementProcessor extends StructureProcessor {
    public static final JigsawReplacementProcessor INSTANCE;
    
    private JigsawReplacementProcessor() {
    }
    
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(final LevelReader bhu, final BlockPos ew, final StructureTemplate.StructureBlockInfo b3, final StructureTemplate.StructureBlockInfo b4, final StructurePlaceSettings cjq) {
        final Block bmv7 = b4.state.getBlock();
        if (bmv7 != Blocks.JIGSAW_BLOCK) {
            return b4;
        }
        final String string8 = b4.nbt.getString("final_state");
        final BlockStateParser dh9 = new BlockStateParser(new StringReader(string8), false);
        try {
            dh9.parse(true);
        }
        catch (CommandSyntaxException commandSyntaxException10) {
            throw new RuntimeException((Throwable)commandSyntaxException10);
        }
        if (dh9.getState().getBlock() == Blocks.STRUCTURE_VOID) {
            return null;
        }
        return new StructureTemplate.StructureBlockInfo(b4.pos, dh9.getState(), null);
    }
    
    @Override
    protected StructureProcessorType getType() {
        return StructureProcessorType.JIGSAW_REPLACEMENT;
    }
    
    @Override
    protected <T> Dynamic<T> getDynamic(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    static {
        INSTANCE = new JigsawReplacementProcessor();
    }
}
