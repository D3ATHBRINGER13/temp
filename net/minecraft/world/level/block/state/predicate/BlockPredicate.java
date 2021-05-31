package net.minecraft.world.level.block.state.predicate;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;

public class BlockPredicate implements Predicate<BlockState> {
    private final Block block;
    
    public BlockPredicate(final Block bmv) {
        this.block = bmv;
    }
    
    public static BlockPredicate forBlock(final Block bmv) {
        return new BlockPredicate(bmv);
    }
    
    public boolean test(@Nullable final BlockState bvt) {
        return bvt != null && bvt.getBlock() == this.block;
    }
}
