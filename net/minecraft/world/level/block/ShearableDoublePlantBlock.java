package net.minecraft.world.level.block;

import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ShearableDoublePlantBlock extends DoublePlantBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF;
    
    public ShearableDoublePlantBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        final boolean boolean4 = super.canBeReplaced(bvt, ban);
        return (!boolean4 || ban.getItemInHand().getItem() != this.asItem()) && boolean4;
    }
    
    static {
        HALF = DoublePlantBlock.HALF;
    }
}
