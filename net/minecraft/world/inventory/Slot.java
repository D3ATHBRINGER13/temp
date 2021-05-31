package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;

public class Slot {
    private final int slot;
    public final Container container;
    public int index;
    public int x;
    public int y;
    
    public Slot(final Container ahc, final int integer2, final int integer3, final int integer4) {
        this.container = ahc;
        this.slot = integer2;
        this.x = integer3;
        this.y = integer4;
    }
    
    public void onQuickCraft(final ItemStack bcj1, final ItemStack bcj2) {
        final int integer4 = bcj2.getCount() - bcj1.getCount();
        if (integer4 > 0) {
            this.onQuickCraft(bcj2, integer4);
        }
    }
    
    protected void onQuickCraft(final ItemStack bcj, final int integer) {
    }
    
    protected void onSwapCraft(final int integer) {
    }
    
    protected void checkTakeAchievements(final ItemStack bcj) {
    }
    
    public ItemStack onTake(final Player awg, final ItemStack bcj) {
        this.setChanged();
        return bcj;
    }
    
    public boolean mayPlace(final ItemStack bcj) {
        return true;
    }
    
    public ItemStack getItem() {
        return this.container.getItem(this.slot);
    }
    
    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }
    
    public void set(final ItemStack bcj) {
        this.container.setItem(this.slot, bcj);
        this.setChanged();
    }
    
    public void setChanged() {
        this.container.setChanged();
    }
    
    public int getMaxStackSize() {
        return this.container.getMaxStackSize();
    }
    
    public int getMaxStackSize(final ItemStack bcj) {
        return this.getMaxStackSize();
    }
    
    @Nullable
    public String getNoItemIcon() {
        return null;
    }
    
    public ItemStack remove(final int integer) {
        return this.container.removeItem(this.slot, integer);
    }
    
    public boolean mayPickup(final Player awg) {
        return true;
    }
    
    public boolean isActive() {
        return true;
    }
}
