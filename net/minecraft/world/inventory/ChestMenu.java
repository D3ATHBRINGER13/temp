package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class ChestMenu extends AbstractContainerMenu {
    private final Container container;
    private final int containerRows;
    
    private ChestMenu(final MenuType<?> azl, final int integer2, final Inventory awf, final int integer4) {
        this(azl, integer2, awf, new SimpleContainer(9 * integer4), integer4);
    }
    
    public static ChestMenu oneRow(final int integer, final Inventory awf) {
        return new ChestMenu(MenuType.GENERIC_9x1, integer, awf, 1);
    }
    
    public static ChestMenu twoRows(final int integer, final Inventory awf) {
        return new ChestMenu(MenuType.GENERIC_9x2, integer, awf, 2);
    }
    
    public static ChestMenu threeRows(final int integer, final Inventory awf) {
        return new ChestMenu(MenuType.GENERIC_9x3, integer, awf, 3);
    }
    
    public static ChestMenu fourRows(final int integer, final Inventory awf) {
        return new ChestMenu(MenuType.GENERIC_9x4, integer, awf, 4);
    }
    
    public static ChestMenu fiveRows(final int integer, final Inventory awf) {
        return new ChestMenu(MenuType.GENERIC_9x5, integer, awf, 5);
    }
    
    public static ChestMenu sixRows(final int integer, final Inventory awf) {
        return new ChestMenu(MenuType.GENERIC_9x6, integer, awf, 6);
    }
    
    public static ChestMenu threeRows(final int integer, final Inventory awf, final Container ahc) {
        return new ChestMenu(MenuType.GENERIC_9x3, integer, awf, ahc, 3);
    }
    
    public static ChestMenu sixRows(final int integer, final Inventory awf, final Container ahc) {
        return new ChestMenu(MenuType.GENERIC_9x6, integer, awf, ahc, 6);
    }
    
    public ChestMenu(final MenuType<?> azl, final int integer2, final Inventory awf, final Container ahc, final int integer5) {
        super(azl, integer2);
        AbstractContainerMenu.checkContainerSize(ahc, integer5 * 9);
        this.container = ahc;
        this.containerRows = integer5;
        ahc.startOpen(awf.player);
        final int integer6 = (this.containerRows - 4) * 18;
        for (int integer7 = 0; integer7 < this.containerRows; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(ahc, integer8 + integer7 * 9, 8 + integer8 * 18, 18 + integer7 * 18));
            }
        }
        for (int integer7 = 0; integer7 < 3; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(awf, integer8 + integer7 * 9 + 9, 8 + integer8 * 18, 103 + integer7 * 18 + integer6));
            }
        }
        for (int integer7 = 0; integer7 < 9; ++integer7) {
            this.addSlot(new Slot(awf, integer7, 8 + integer7 * 18, 161 + integer6));
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
            if (integer < this.containerRows * 9) {
                if (!this.moveItemStackTo(bcj5, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 0, this.containerRows * 9, false)) {
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
    
    public Container getContainer() {
        return this.container;
    }
    
    public int getRowCount() {
        return this.containerRows;
    }
}
