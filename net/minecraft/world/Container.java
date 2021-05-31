package net.minecraft.world;

import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface Container extends Clearable {
    int getContainerSize();
    
    boolean isEmpty();
    
    ItemStack getItem(final int integer);
    
    ItemStack removeItem(final int integer1, final int integer2);
    
    ItemStack removeItemNoUpdate(final int integer);
    
    void setItem(final int integer, final ItemStack bcj);
    
    default int getMaxStackSize() {
        return 64;
    }
    
    void setChanged();
    
    boolean stillValid(final Player awg);
    
    default void startOpen(final Player awg) {
    }
    
    default void stopOpen(final Player awg) {
    }
    
    default boolean canPlaceItem(final int integer, final ItemStack bcj) {
        return true;
    }
    
    default int countItem(final Item bce) {
        int integer3 = 0;
        for (int integer4 = 0; integer4 < this.getContainerSize(); ++integer4) {
            final ItemStack bcj5 = this.getItem(integer4);
            if (bcj5.getItem().equals(bce)) {
                integer3 += bcj5.getCount();
            }
        }
        return integer3;
    }
    
    default boolean hasAnyOf(final Set<Item> set) {
        for (int integer3 = 0; integer3 < this.getContainerSize(); ++integer3) {
            final ItemStack bcj4 = this.getItem(integer3);
            if (set.contains(bcj4.getItem()) && bcj4.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
}
