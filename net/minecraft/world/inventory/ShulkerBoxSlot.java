package net.minecraft.world.inventory;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;

public class ShulkerBoxSlot extends Slot {
    public ShulkerBoxSlot(final Container ahc, final int integer2, final int integer3, final int integer4) {
        super(ahc, integer2, integer3, integer4);
    }
    
    @Override
    public boolean mayPlace(final ItemStack bcj) {
        return !(Block.byItem(bcj.getItem()) instanceof ShulkerBoxBlock);
    }
}
