package net.minecraft.world.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;

public class DoubleHighBlockItem extends BlockItem {
    public DoubleHighBlockItem(final Block bmv, final Properties a) {
        super(bmv, a);
    }
    
    @Override
    protected boolean placeBlock(final BlockPlaceContext ban, final BlockState bvt) {
        ban.getLevel().setBlock(ban.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
        return super.placeBlock(ban, bvt);
    }
}
