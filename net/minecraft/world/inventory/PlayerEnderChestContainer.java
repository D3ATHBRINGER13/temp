package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.SimpleContainer;

public class PlayerEnderChestContainer extends SimpleContainer {
    private EnderChestBlockEntity activeChest;
    
    public PlayerEnderChestContainer() {
        super(27);
    }
    
    public void setActiveChest(final EnderChestBlockEntity bui) {
        this.activeChest = bui;
    }
    
    public void fromTag(final ListTag ik) {
        for (int integer3 = 0; integer3 < this.getContainerSize(); ++integer3) {
            this.setItem(integer3, ItemStack.EMPTY);
        }
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            final int integer4 = id4.getByte("Slot") & 0xFF;
            if (integer4 >= 0 && integer4 < this.getContainerSize()) {
                this.setItem(integer4, ItemStack.of(id4));
            }
        }
    }
    
    public ListTag createTag() {
        final ListTag ik2 = new ListTag();
        for (int integer3 = 0; integer3 < this.getContainerSize(); ++integer3) {
            final ItemStack bcj4 = this.getItem(integer3);
            if (!bcj4.isEmpty()) {
                final CompoundTag id5 = new CompoundTag();
                id5.putByte("Slot", (byte)integer3);
                bcj4.save(id5);
                ik2.add(id5);
            }
        }
        return ik2;
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return (this.activeChest == null || this.activeChest.stillValid(awg)) && super.stillValid(awg);
    }
    
    public void startOpen(final Player awg) {
        if (this.activeChest != null) {
            this.activeChest.startOpen();
        }
        super.startOpen(awg);
    }
    
    public void stopOpen(final Player awg) {
        if (this.activeChest != null) {
            this.activeChest.stopOpen();
        }
        super.stopOpen(awg);
        this.activeChest = null;
    }
}
