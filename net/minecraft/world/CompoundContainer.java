package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CompoundContainer implements Container {
    private final Container container1;
    private final Container container2;
    
    public CompoundContainer(Container ahc1, Container ahc2) {
        if (ahc1 == null) {
            ahc1 = ahc2;
        }
        if (ahc2 == null) {
            ahc2 = ahc1;
        }
        this.container1 = ahc1;
        this.container2 = ahc2;
    }
    
    public int getContainerSize() {
        return this.container1.getContainerSize() + this.container2.getContainerSize();
    }
    
    public boolean isEmpty() {
        return this.container1.isEmpty() && this.container2.isEmpty();
    }
    
    public boolean contains(final Container ahc) {
        return this.container1 == ahc || this.container2 == ahc;
    }
    
    public ItemStack getItem(final int integer) {
        if (integer >= this.container1.getContainerSize()) {
            return this.container2.getItem(integer - this.container1.getContainerSize());
        }
        return this.container1.getItem(integer);
    }
    
    public ItemStack removeItem(final int integer1, final int integer2) {
        if (integer1 >= this.container1.getContainerSize()) {
            return this.container2.removeItem(integer1 - this.container1.getContainerSize(), integer2);
        }
        return this.container1.removeItem(integer1, integer2);
    }
    
    public ItemStack removeItemNoUpdate(final int integer) {
        if (integer >= this.container1.getContainerSize()) {
            return this.container2.removeItemNoUpdate(integer - this.container1.getContainerSize());
        }
        return this.container1.removeItemNoUpdate(integer);
    }
    
    public void setItem(final int integer, final ItemStack bcj) {
        if (integer >= this.container1.getContainerSize()) {
            this.container2.setItem(integer - this.container1.getContainerSize(), bcj);
        }
        else {
            this.container1.setItem(integer, bcj);
        }
    }
    
    public int getMaxStackSize() {
        return this.container1.getMaxStackSize();
    }
    
    public void setChanged() {
        this.container1.setChanged();
        this.container2.setChanged();
    }
    
    public boolean stillValid(final Player awg) {
        return this.container1.stillValid(awg) && this.container2.stillValid(awg);
    }
    
    public void startOpen(final Player awg) {
        this.container1.startOpen(awg);
        this.container2.startOpen(awg);
    }
    
    public void stopOpen(final Player awg) {
        this.container1.stopOpen(awg);
        this.container2.stopOpen(awg);
    }
    
    public boolean canPlaceItem(final int integer, final ItemStack bcj) {
        if (integer >= this.container1.getContainerSize()) {
            return this.container2.canPlaceItem(integer - this.container1.getContainerSize(), bcj);
        }
        return this.container1.canPlaceItem(integer, bcj);
    }
    
    public void clearContent() {
        this.container1.clearContent();
        this.container2.clearContent();
    }
}
