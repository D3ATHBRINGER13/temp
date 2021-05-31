package net.minecraft.world.inventory;

import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.entity.player.Player;

public class MerchantResultSlot extends Slot {
    private final MerchantContainer slots;
    private final Player player;
    private int removeCount;
    private final Merchant merchant;
    
    public MerchantResultSlot(final Player awg, final Merchant bgt, final MerchantContainer azm, final int integer4, final int integer5, final int integer6) {
        super(azm, integer4, integer5, integer6);
        this.player = awg;
        this.merchant = bgt;
        this.slots = azm;
    }
    
    @Override
    public boolean mayPlace(final ItemStack bcj) {
        return false;
    }
    
    @Override
    public ItemStack remove(final int integer) {
        if (this.hasItem()) {
            this.removeCount += Math.min(integer, this.getItem().getCount());
        }
        return super.remove(integer);
    }
    
    @Override
    protected void onQuickCraft(final ItemStack bcj, final int integer) {
        this.removeCount += integer;
        this.checkTakeAchievements(bcj);
    }
    
    @Override
    protected void checkTakeAchievements(final ItemStack bcj) {
        bcj.onCraftedBy(this.player.level, this.player, this.removeCount);
        this.removeCount = 0;
    }
    
    @Override
    public ItemStack onTake(final Player awg, final ItemStack bcj) {
        this.checkTakeAchievements(bcj);
        final MerchantOffer bgu4 = this.slots.getActiveOffer();
        if (bgu4 != null) {
            final ItemStack bcj2 = this.slots.getItem(0);
            final ItemStack bcj3 = this.slots.getItem(1);
            if (bgu4.take(bcj2, bcj3) || bgu4.take(bcj3, bcj2)) {
                this.merchant.notifyTrade(bgu4);
                awg.awardStat(Stats.TRADED_WITH_VILLAGER);
                this.slots.setItem(0, bcj2);
                this.slots.setItem(1, bcj3);
            }
            this.merchant.overrideXp(this.merchant.getVillagerXp() + bgu4.getXp());
        }
        return bcj;
    }
}
