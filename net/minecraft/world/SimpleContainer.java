package net.minecraft.world;

import java.util.stream.Collectors;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.Player;
import java.util.Iterator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.StackedContentsCompatible;

public class SimpleContainer implements Container, StackedContentsCompatible {
    private final int size;
    private final NonNullList<ItemStack> items;
    private List<ContainerListener> listeners;
    
    public SimpleContainer(final int integer) {
        this.size = integer;
        this.items = NonNullList.<ItemStack>withSize(integer, ItemStack.EMPTY);
    }
    
    public SimpleContainer(final ItemStack... arr) {
        this.size = arr.length;
        this.items = NonNullList.<ItemStack>of(ItemStack.EMPTY, arr);
    }
    
    public void addListener(final ContainerListener ahe) {
        if (this.listeners == null) {
            this.listeners = (List<ContainerListener>)Lists.newArrayList();
        }
        this.listeners.add(ahe);
    }
    
    public void removeListener(final ContainerListener ahe) {
        this.listeners.remove(ahe);
    }
    
    public ItemStack getItem(final int integer) {
        if (integer < 0 || integer >= this.items.size()) {
            return ItemStack.EMPTY;
        }
        return this.items.get(integer);
    }
    
    public ItemStack removeItem(final int integer1, final int integer2) {
        final ItemStack bcj4 = ContainerHelper.removeItem((List<ItemStack>)this.items, integer1, integer2);
        if (!bcj4.isEmpty()) {
            this.setChanged();
        }
        return bcj4;
    }
    
    public ItemStack removeItemType(final Item bce, final int integer) {
        final ItemStack bcj4 = new ItemStack(bce, 0);
        for (int integer2 = this.size - 1; integer2 >= 0; --integer2) {
            final ItemStack bcj5 = this.getItem(integer2);
            if (bcj5.getItem().equals(bce)) {
                final int integer3 = integer - bcj4.getCount();
                final ItemStack bcj6 = bcj5.split(integer3);
                bcj4.grow(bcj6.getCount());
                if (bcj4.getCount() == integer) {
                    break;
                }
            }
        }
        if (!bcj4.isEmpty()) {
            this.setChanged();
        }
        return bcj4;
    }
    
    public ItemStack addItem(final ItemStack bcj) {
        final ItemStack bcj2 = bcj.copy();
        this.moveItemToOccupiedSlotsWithSameType(bcj2);
        if (bcj2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.moveItemToEmptySlots(bcj2);
        if (bcj2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return bcj2;
    }
    
    public ItemStack removeItemNoUpdate(final int integer) {
        final ItemStack bcj3 = this.items.get(integer);
        if (bcj3.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.items.set(integer, ItemStack.EMPTY);
        return bcj3;
    }
    
    public void setItem(final int integer, final ItemStack bcj) {
        this.items.set(integer, bcj);
        if (!bcj.isEmpty() && bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }
    
    public int getContainerSize() {
        return this.size;
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public void setChanged() {
        if (this.listeners != null) {
            for (final ContainerListener ahe3 : this.listeners) {
                ahe3.containerChanged(this);
            }
        }
    }
    
    public boolean stillValid(final Player awg) {
        return true;
    }
    
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
    
    public void fillStackedContents(final StackedContents awi) {
        for (final ItemStack bcj4 : this.items) {
            awi.accountStack(bcj4);
        }
    }
    
    public String toString() {
        return ((List)this.items.stream().filter(bcj -> !bcj.isEmpty()).collect(Collectors.toList())).toString();
    }
    
    private void moveItemToEmptySlots(final ItemStack bcj) {
        for (int integer3 = 0; integer3 < this.size; ++integer3) {
            final ItemStack bcj2 = this.getItem(integer3);
            if (bcj2.isEmpty()) {
                this.setItem(integer3, bcj.copy());
                bcj.setCount(0);
                return;
            }
        }
    }
    
    private void moveItemToOccupiedSlotsWithSameType(final ItemStack bcj) {
        for (int integer3 = 0; integer3 < this.size; ++integer3) {
            final ItemStack bcj2 = this.getItem(integer3);
            if (ItemStack.isSame(bcj2, bcj)) {
                this.moveItemsBetweenStacks(bcj, bcj2);
                if (bcj.isEmpty()) {
                    return;
                }
            }
        }
    }
    
    private void moveItemsBetweenStacks(final ItemStack bcj1, final ItemStack bcj2) {
        final int integer4 = Math.min(this.getMaxStackSize(), bcj2.getMaxStackSize());
        final int integer5 = Math.min(bcj1.getCount(), integer4 - bcj2.getCount());
        if (integer5 > 0) {
            bcj2.grow(integer5);
            bcj1.shrink(integer5);
            this.setChanged();
        }
    }
}
