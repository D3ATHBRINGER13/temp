package net.minecraft.world.inventory;

import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.trading.Merchant;

public class MerchantMenu extends AbstractContainerMenu {
    private final Merchant trader;
    private final MerchantContainer tradeContainer;
    private int merchantLevel;
    private boolean showProgressBar;
    private boolean canRestock;
    
    public MerchantMenu(final int integer, final Inventory awf) {
        this(integer, awf, new ClientSideMerchant(awf.player));
    }
    
    public MerchantMenu(final int integer, final Inventory awf, final Merchant bgt) {
        super(MenuType.MERCHANT, integer);
        this.trader = bgt;
        this.tradeContainer = new MerchantContainer(bgt);
        this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
        this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
        this.addSlot(new MerchantResultSlot(awf.player, bgt, this.tradeContainer, 2, 220, 37));
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(awf, integer3 + integer2 * 9 + 9, 108 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(awf, integer2, 108 + integer2 * 18, 142));
        }
    }
    
    public void setShowProgressBar(final boolean boolean1) {
        this.showProgressBar = boolean1;
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged(ahc);
    }
    
    public void setSelectionHint(final int integer) {
        this.tradeContainer.setSelectionHint(integer);
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.trader.getTradingPlayer() == awg;
    }
    
    public int getTraderXp() {
        return this.trader.getVillagerXp();
    }
    
    public int getFutureTraderXp() {
        return this.tradeContainer.getFutureXp();
    }
    
    public void setXp(final int integer) {
        this.trader.overrideXp(integer);
    }
    
    public int getTraderLevel() {
        return this.merchantLevel;
    }
    
    public void setMerchantLevel(final int integer) {
        this.merchantLevel = integer;
    }
    
    public void setCanRestock(final boolean boolean1) {
        this.canRestock = boolean1;
    }
    
    public boolean canRestock() {
        return this.canRestock;
    }
    
    @Override
    public boolean canTakeItemForPickAll(final ItemStack bcj, final Slot azx) {
        return false;
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 2) {
                if (!this.moveItemStackTo(bcj5, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
                this.playTradeSound();
            }
            else if (integer == 0 || integer == 1) {
                if (!this.moveItemStackTo(bcj5, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 3 && integer < 30) {
                if (!this.moveItemStackTo(bcj5, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 30 && integer < 39 && !this.moveItemStackTo(bcj5, 3, 30, false)) {
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
    
    private void playTradeSound() {
        if (!this.trader.getLevel().isClientSide) {
            final Entity aio2 = (Entity)this.trader;
            this.trader.getLevel().playLocalSound(aio2.x, aio2.y, aio2.z, this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
        }
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.trader.setTradingPlayer(null);
        if (this.trader.getLevel().isClientSide) {
            return;
        }
        if (!awg.isAlive() || (awg instanceof ServerPlayer && ((ServerPlayer)awg).hasDisconnected())) {
            ItemStack bcj3 = this.tradeContainer.removeItemNoUpdate(0);
            if (!bcj3.isEmpty()) {
                awg.drop(bcj3, false);
            }
            bcj3 = this.tradeContainer.removeItemNoUpdate(1);
            if (!bcj3.isEmpty()) {
                awg.drop(bcj3, false);
            }
        }
        else {
            awg.inventory.placeItemBackInInventory(awg.level, this.tradeContainer.removeItemNoUpdate(0));
            awg.inventory.placeItemBackInInventory(awg.level, this.tradeContainer.removeItemNoUpdate(1));
        }
    }
    
    public void tryMoveItems(final int integer) {
        if (this.getOffers().size() <= integer) {
            return;
        }
        final ItemStack bcj3 = this.tradeContainer.getItem(0);
        if (!bcj3.isEmpty()) {
            if (!this.moveItemStackTo(bcj3, 3, 39, true)) {
                return;
            }
            this.tradeContainer.setItem(0, bcj3);
        }
        final ItemStack bcj4 = this.tradeContainer.getItem(1);
        if (!bcj4.isEmpty()) {
            if (!this.moveItemStackTo(bcj4, 3, 39, true)) {
                return;
            }
            this.tradeContainer.setItem(1, bcj4);
        }
        if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
            final ItemStack bcj5 = ((MerchantOffer)this.getOffers().get(integer)).getCostA();
            this.moveFromInventoryToPaymentSlot(0, bcj5);
            final ItemStack bcj6 = ((MerchantOffer)this.getOffers().get(integer)).getCostB();
            this.moveFromInventoryToPaymentSlot(1, bcj6);
        }
    }
    
    private void moveFromInventoryToPaymentSlot(final int integer, final ItemStack bcj) {
        if (!bcj.isEmpty()) {
            for (int integer2 = 3; integer2 < 39; ++integer2) {
                final ItemStack bcj2 = ((Slot)this.slots.get(integer2)).getItem();
                if (!bcj2.isEmpty() && this.isSameItem(bcj, bcj2)) {
                    final ItemStack bcj3 = this.tradeContainer.getItem(integer);
                    final int integer3 = bcj3.isEmpty() ? 0 : bcj3.getCount();
                    final int integer4 = Math.min(bcj.getMaxStackSize() - integer3, bcj2.getCount());
                    final ItemStack bcj4 = bcj2.copy();
                    final int integer5 = integer3 + integer4;
                    bcj2.shrink(integer4);
                    bcj4.setCount(integer5);
                    this.tradeContainer.setItem(integer, bcj4);
                    if (integer5 >= bcj.getMaxStackSize()) {
                        break;
                    }
                }
            }
        }
    }
    
    private boolean isSameItem(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1.getItem() == bcj2.getItem() && ItemStack.tagMatches(bcj1, bcj2);
    }
    
    public void setOffers(final MerchantOffers bgv) {
        this.trader.overrideOffers(bgv);
    }
    
    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }
    
    public boolean showProgressBar() {
        return this.showProgressBar;
    }
}
