package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ContainerListener;

public class CreativeInventoryListener implements ContainerListener {
    private final Minecraft minecraft;
    
    public CreativeInventoryListener(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void refreshContainer(final AbstractContainerMenu ayk, final NonNullList<ItemStack> fk) {
    }
    
    public void slotChanged(final AbstractContainerMenu ayk, final int integer, final ItemStack bcj) {
        this.minecraft.gameMode.handleCreativeModeItemAdd(bcj, integer);
    }
    
    public void setContainerData(final AbstractContainerMenu ayk, final int integer2, final int integer3) {
    }
}
