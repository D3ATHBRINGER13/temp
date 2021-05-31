package net.minecraft.world.item.trading;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import java.util.OptionalInt;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;

public interface Merchant {
    void setTradingPlayer(@Nullable final Player awg);
    
    @Nullable
    Player getTradingPlayer();
    
    MerchantOffers getOffers();
    
    void overrideOffers(@Nullable final MerchantOffers bgv);
    
    void notifyTrade(final MerchantOffer bgu);
    
    void notifyTradeUpdated(final ItemStack bcj);
    
    Level getLevel();
    
    int getVillagerXp();
    
    void overrideXp(final int integer);
    
    boolean showProgressBar();
    
    SoundEvent getNotifyTradeSound();
    
    default boolean canRestock() {
        return false;
    }
    
    default void openTradingScreen(final Player awg, final Component jo, final int integer) {
        final OptionalInt optionalInt5 = awg.openMenu(new SimpleMenuProvider((integer, awf, awg) -> new MerchantMenu(integer, awf, this), jo));
        if (optionalInt5.isPresent()) {
            final MerchantOffers bgv6 = this.getOffers();
            if (!bgv6.isEmpty()) {
                awg.sendMerchantOffers(optionalInt5.getAsInt(), bgv6, integer, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
            }
        }
    }
}
