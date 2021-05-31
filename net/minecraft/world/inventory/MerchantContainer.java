package net.minecraft.world.inventory;

import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.ContainerHelper;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.Container;

public class MerchantContainer implements Container {
    private final Merchant merchant;
    private final NonNullList<ItemStack> itemStacks;
    @Nullable
    private MerchantOffer activeOffer;
    private int selectionHint;
    private int futureXp;
    
    public MerchantContainer(final Merchant bgt) {
        this.itemStacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
        this.merchant = bgt;
    }
    
    public int getContainerSize() {
        return this.itemStacks.size();
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.itemStacks) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public ItemStack getItem(final int integer) {
        return this.itemStacks.get(integer);
    }
    
    public ItemStack removeItem(final int integer1, final int integer2) {
        final ItemStack bcj4 = this.itemStacks.get(integer1);
        if (integer1 == 2 && !bcj4.isEmpty()) {
            return ContainerHelper.removeItem((List<ItemStack>)this.itemStacks, integer1, bcj4.getCount());
        }
        final ItemStack bcj5 = ContainerHelper.removeItem((List<ItemStack>)this.itemStacks, integer1, integer2);
        if (!bcj5.isEmpty() && this.isPaymentSlot(integer1)) {
            this.updateSellItem();
        }
        return bcj5;
    }
    
    private boolean isPaymentSlot(final int integer) {
        return integer == 0 || integer == 1;
    }
    
    public ItemStack removeItemNoUpdate(final int integer) {
        return ContainerHelper.takeItem((List<ItemStack>)this.itemStacks, integer);
    }
    
    public void setItem(final int integer, final ItemStack bcj) {
        this.itemStacks.set(integer, bcj);
        if (!bcj.isEmpty() && bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
        if (this.isPaymentSlot(integer)) {
            this.updateSellItem();
        }
    }
    
    public boolean stillValid(final Player awg) {
        return this.merchant.getTradingPlayer() == awg;
    }
    
    public void setChanged() {
        this.updateSellItem();
    }
    
    public void updateSellItem() {
        this.activeOffer = null;
        ItemStack bcj2;
        ItemStack bcj3;
        if (this.itemStacks.get(0).isEmpty()) {
            bcj2 = this.itemStacks.get(1);
            bcj3 = ItemStack.EMPTY;
        }
        else {
            bcj2 = this.itemStacks.get(0);
            bcj3 = this.itemStacks.get(1);
        }
        if (bcj2.isEmpty()) {
            this.setItem(2, ItemStack.EMPTY);
            this.futureXp = 0;
            return;
        }
        final MerchantOffers bgv4 = this.merchant.getOffers();
        if (!bgv4.isEmpty()) {
            MerchantOffer bgu5 = bgv4.getRecipeFor(bcj2, bcj3, this.selectionHint);
            if (bgu5 == null || bgu5.isOutOfStock()) {
                this.activeOffer = bgu5;
                bgu5 = bgv4.getRecipeFor(bcj3, bcj2, this.selectionHint);
            }
            if (bgu5 != null && !bgu5.isOutOfStock()) {
                this.activeOffer = bgu5;
                this.setItem(2, bgu5.assemble());
                this.futureXp = bgu5.getXp();
            }
            else {
                this.setItem(2, ItemStack.EMPTY);
                this.futureXp = 0;
            }
        }
        this.merchant.notifyTradeUpdated(this.getItem(2));
    }
    
    @Nullable
    public MerchantOffer getActiveOffer() {
        return this.activeOffer;
    }
    
    public void setSelectionHint(final int integer) {
        this.selectionHint = integer;
        this.updateSellItem();
    }
    
    public void clearContent() {
        this.itemStacks.clear();
    }
    
    public int getFutureXp() {
        return this.futureXp;
    }
}
