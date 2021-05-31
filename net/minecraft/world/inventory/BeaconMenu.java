package net.minecraft.world.inventory;

import net.minecraft.world.item.Item;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;

public class BeaconMenu extends AbstractContainerMenu {
    private final Container beacon;
    private final PaymentSlot paymentSlot;
    private final ContainerLevelAccess access;
    private final ContainerData beaconData;
    
    public BeaconMenu(final int integer, final Container ahc) {
        this(integer, ahc, new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }
    
    public BeaconMenu(final int integer, final Container ahc, final ContainerData ayt, final ContainerLevelAccess ayu) {
        super(MenuType.BEACON, integer);
        this.beacon = new SimpleContainer(1) {
            public boolean canPlaceItem(final int integer, final ItemStack bcj) {
                return bcj.getItem() == Items.EMERALD || bcj.getItem() == Items.DIAMOND || bcj.getItem() == Items.GOLD_INGOT || bcj.getItem() == Items.IRON_INGOT;
            }
            
            public int getMaxStackSize() {
                return 1;
            }
        };
        AbstractContainerMenu.checkContainerDataCount(ayt, 3);
        this.beaconData = ayt;
        this.access = ayu;
        this.addSlot(this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110));
        this.addDataSlots(ayt);
        final int integer2 = 36;
        final int integer3 = 137;
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(ahc, integer5 + integer4 * 9 + 9, 36 + integer5 * 18, 137 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(ahc, integer4, 36 + integer4 * 18, 195));
        }
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        if (awg.level.isClientSide) {
            return;
        }
        final ItemStack bcj3 = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
        if (!bcj3.isEmpty()) {
            awg.drop(bcj3, false);
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.BEACON);
    }
    
    @Override
    public void setData(final int integer1, final int integer2) {
        super.setData(integer1, integer2);
        this.broadcastChanges();
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 0) {
                if (!this.moveItemStackTo(bcj5, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace(bcj5) && bcj5.getCount() == 1) {
                if (!this.moveItemStackTo(bcj5, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 1 && integer < 28) {
                if (!this.moveItemStackTo(bcj5, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 28 && integer < 37) {
                if (!this.moveItemStackTo(bcj5, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 1, 37, false)) {
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
    
    public int getLevels() {
        return this.beaconData.get(0);
    }
    
    @Nullable
    public MobEffect getPrimaryEffect() {
        return MobEffect.byId(this.beaconData.get(1));
    }
    
    @Nullable
    public MobEffect getSecondaryEffect() {
        return MobEffect.byId(this.beaconData.get(2));
    }
    
    public void updateEffects(final int integer1, final int integer2) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(1, integer1);
            this.beaconData.set(2, integer2);
            this.paymentSlot.remove(1);
        }
    }
    
    public boolean hasPayment() {
        return !this.beacon.getItem(0).isEmpty();
    }
    
    class PaymentSlot extends Slot {
        public PaymentSlot(final Container ahc, final int integer3, final int integer4, final int integer5) {
            super(ahc, integer3, integer4, integer5);
        }
        
        @Override
        public boolean mayPlace(final ItemStack bcj) {
            final Item bce3 = bcj.getItem();
            return bce3 == Items.EMERALD || bce3 == Items.DIAMOND || bce3 == Items.GOLD_INGOT || bce3 == Items.IRON_INGOT;
        }
        
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
