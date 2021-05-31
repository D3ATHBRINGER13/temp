package net.minecraft.world.item;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;

public class BedItem extends BlockItem {
    public BedItem(final Block bmv, final Properties a) {
        super(bmv, a);
    }
    
    @Override
    protected boolean placeBlock(final BlockPlaceContext ban, final BlockState bvt) {
        return ban.getLevel().setBlock(ban.getClickedPos(), bvt, 26);
    }
}
