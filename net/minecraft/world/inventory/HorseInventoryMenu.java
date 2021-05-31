package net.minecraft.world.inventory;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.Container;

public class HorseInventoryMenu extends AbstractContainerMenu {
    private final Container horseContainer;
    private final AbstractHorse horse;
    
    public HorseInventoryMenu(final int integer, final Inventory awf, final Container ahc, final AbstractHorse asb) {
        super(null, integer);
        this.horseContainer = ahc;
        this.horse = asb;
        final int integer2 = 3;
        ahc.startOpen(awf.player);
        final int integer3 = -18;
        this.addSlot(new Slot(ahc, 0, 8, 18) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.getItem() == Items.SADDLE && !this.hasItem() && asb.canBeSaddled();
            }
            
            @Override
            public boolean isActive() {
                return asb.canBeSaddled();
            }
        });
        this.addSlot(new Slot(ahc, 1, 8, 36) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return asb.isArmor(bcj);
            }
            
            @Override
            public boolean isActive() {
                return asb.wearsArmor();
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        if (asb instanceof AbstractChestedHorse && ((AbstractChestedHorse)asb).hasChest()) {
            for (int integer4 = 0; integer4 < 3; ++integer4) {
                for (int integer5 = 0; integer5 < ((AbstractChestedHorse)asb).getInventoryColumns(); ++integer5) {
                    this.addSlot(new Slot(ahc, 2 + integer5 + integer4 * ((AbstractChestedHorse)asb).getInventoryColumns(), 80 + integer5 * 18, 18 + integer4 * 18));
                }
            }
        }
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(awf, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 102 + integer4 * 18 - 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(awf, integer4, 8 + integer4 * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.horseContainer.stillValid(awg) && this.horse.isAlive() && this.horse.distanceTo(awg) < 8.0f;
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer < this.horseContainer.getContainerSize()) {
                if (!this.moveItemStackTo(bcj5, this.horseContainer.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(1).mayPlace(bcj5) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(bcj5, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(0).mayPlace(bcj5)) {
                if (!this.moveItemStackTo(bcj5, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.horseContainer.getContainerSize() <= 2 || !this.moveItemStackTo(bcj5, 2, this.horseContainer.getContainerSize(), false)) {
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
        this.horseContainer.stopOpen(awg);
    }
}
