package net.minecraft.world.inventory;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;

public class FurnaceFuelSlot extends Slot {
    private final AbstractFurnaceMenu menu;
    
    public FurnaceFuelSlot(final AbstractFurnaceMenu ayl, final Container ahc, final int integer3, final int integer4, final int integer5) {
        super(ahc, integer3, integer4, integer5);
        this.menu = ayl;
    }
    
    @Override
    public boolean mayPlace(final ItemStack bcj) {
        return this.menu.isFuel(bcj) || isBucket(bcj);
    }
    
    @Override
    public int getMaxStackSize(final ItemStack bcj) {
        return isBucket(bcj) ? 1 : super.getMaxStackSize(bcj);
    }
    
    public static boolean isBucket(final ItemStack bcj) {
        return bcj.getItem() == Items.BUCKET;
    }
}
