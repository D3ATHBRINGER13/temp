package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class HopperMenu extends AbstractContainerMenu {
    private final Container hopper;
    
    public HopperMenu(final int integer, final Inventory awf) {
        this(integer, awf, new SimpleContainer(5));
    }
    
    public HopperMenu(final int integer, final Inventory awf, final Container ahc) {
        super(MenuType.HOPPER, integer);
        AbstractContainerMenu.checkContainerSize(this.hopper = ahc, 5);
        ahc.startOpen(awf.player);
        final int integer2 = 51;
        for (int integer3 = 0; integer3 < 5; ++integer3) {
            this.addSlot(new Slot(ahc, integer3, 44 + integer3 * 18, 20));
        }
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            for (int integer4 = 0; integer4 < 9; ++integer4) {
                this.addSlot(new Slot(awf, integer4 + integer3 * 9 + 9, 8 + integer4 * 18, integer3 * 18 + 51));
            }
        }
        for (int integer3 = 0; integer3 < 9; ++integer3) {
            this.addSlot(new Slot(awf, integer3, 8 + integer3 * 18, 109));
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.hopper.stillValid(awg);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer < this.hopper.getContainerSize()) {
                if (!this.moveItemStackTo(bcj5, this.hopper.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 0, this.hopper.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (bcj5.isEmpty()) {
                azx5.set(ItemStack.EMPTY);
            }
            else {
                azx5.setChanged();
            }
        }
        return bcj4;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.hopper.stopOpen(awg);
    }
}
