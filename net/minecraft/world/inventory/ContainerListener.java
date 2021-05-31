package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public interface ContainerListener {
    void refreshContainer(final AbstractContainerMenu ayk, final NonNullList<ItemStack> fk);
    
    void slotChanged(final AbstractContainerMenu ayk, final int integer, final ItemStack bcj);
    
    void setContainerData(final AbstractContainerMenu ayk, final int integer2, final int integer3);
}
