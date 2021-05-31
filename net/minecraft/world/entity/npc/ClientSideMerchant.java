package net.minecraft.world.entity.npc;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import javax.annotation.Nullable;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.trading.Merchant;

public class ClientSideMerchant implements Merchant {
    private final MerchantContainer container;
    private final Player source;
    private MerchantOffers offers;
    private int xp;
    
    public ClientSideMerchant(final Player awg) {
        this.offers = new MerchantOffers();
        this.source = awg;
        this.container = new MerchantContainer(this);
    }
    
    @Nullable
    public Player getTradingPlayer() {
        return this.source;
    }
    
    public void setTradingPlayer(@Nullable final Player awg) {
    }
    
    public MerchantOffers getOffers() {
        return this.offers;
    }
    
    public void overrideOffers(@Nullable final MerchantOffers bgv) {
        this.offers = bgv;
    }
    
    public void notifyTrade(final MerchantOffer bgu) {
        bgu.increaseUses();
    }
    
    public void notifyTradeUpdated(final ItemStack bcj) {
    }
    
    public Level getLevel() {
        return this.source.level;
    }
    
    public int getVillagerXp() {
        return this.xp;
    }
    
    public void overrideXp(final int integer) {
        this.xp = integer;
    }
    
    public boolean showProgressBar() {
        return true;
    }
    
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }
}
