package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class ShulkerBoxMenu extends AbstractContainerMenu {
    private final Container container;
    
    public ShulkerBoxMenu(final int integer, final Inventory awf) {
        this(integer, awf, new SimpleContainer(27));
    }
    
    public ShulkerBoxMenu(final int integer, final Inventory awf, final Container ahc) {
        super(MenuType.SHULKER_BOX, integer);
        AbstractContainerMenu.checkContainerSize(ahc, 27);
        (this.container = ahc).startOpen(awf.player);
        final int integer2 = 3;
        final int integer3 = 9;
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new ShulkerBoxSlot(ahc, integer5 + integer4 * 9, 8 + integer5 * 18, 18 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(awf, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(awf, integer4, 8 + integer4 * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.container.stillValid(awg);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(bcj5, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 0, this.container.getContainerSize(), false)) {
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
        this.container.stopOpen(awg);
    }
}
