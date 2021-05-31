package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class DispenserMenu extends AbstractContainerMenu {
    private final Container dispenser;
    
    public DispenserMenu(final int integer, final Inventory awf) {
        this(integer, awf, new SimpleContainer(9));
    }
    
    public DispenserMenu(final int integer, final Inventory awf, final Container ahc) {
        super(MenuType.GENERIC_3x3, integer);
        AbstractContainerMenu.checkContainerSize(ahc, 9);
        (this.dispenser = ahc).startOpen(awf.player);
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 3; ++integer3) {
                this.addSlot(new Slot(ahc, integer3 + integer2 * 3, 62 + integer3 * 18, 17 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(awf, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(awf, integer2, 8 + integer2 * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.dispenser.stillValid(awg);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer < 9) {
                if (!this.moveItemStackTo(bcj5, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (bcj5.isEmpty()) {
                azx5.set(ItemStack.EMPTY);
            }
            else {
                azx5.setChanged();
            }
            if (bcj5.getCount() == bcj4.getCount()) {
                return ItemStack.EMPTY;
            }
            azx5.onTake(awg, bcj5);
        }
        return bcj4;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.dispenser.stopOpen(awg);
    }
}
