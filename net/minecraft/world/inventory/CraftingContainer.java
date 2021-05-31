package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.ContainerHelper;
import java.util.Iterator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;

public class CraftingContainer implements Container, StackedContentsCompatible {
    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;
    private final AbstractContainerMenu menu;
    
    public CraftingContainer(final AbstractContainerMenu ayk, final int integer2, final int integer3) {
        this.items = NonNullList.<ItemStack>withSize(integer2 * integer3, ItemStack.EMPTY);
        this.menu = ayk;
        this.width = integer2;
        this.height = integer3;
    }
    
    public int getContainerSize() {
        return this.items.size();
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public ItemStack getItem(final int integer) {
        if (integer >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        return this.items.get(integer);
    }
    
    public ItemStack removeItemNoUpdate(final int integer) {
        return ContainerHelper.takeItem((List<ItemStack>)this.items, integer);
    }
    
    public ItemStack removeItem(final int integer1, final int integer2) {
        final ItemStack bcj4 = ContainerHelper.removeItem((List<ItemStack>)this.items, integer1, integer2);
        if (!bcj4.isEmpty()) {
            this.menu.slotsChanged(this);
        }
        return bcj4;
    }
    
    public void setItem(final int integer, final ItemStack bcj) {
        this.items.set(integer, bcj);
        this.menu.slotsChanged(this);
    }
    
    public void setChanged() {
    }
    
    public boolean stillValid(final Player awg) {
        return true;
    }
    
    public void clearContent() {
        this.items.clear();
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void fillStackedContents(final StackedContents awi) {
        for (final ItemStack bcj4 : this.items) {
            awi.accountSimpleStack(bcj4);
        }
    }
}
