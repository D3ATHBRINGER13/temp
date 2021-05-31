package net.minecraft.client.player.inventory;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ForwardingList;

public class Hotbar extends ForwardingList<ItemStack> {
    private final NonNullList<ItemStack> items;
    
    public Hotbar() {
        this.items = NonNullList.<ItemStack>withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);
    }
    
    protected List<ItemStack> delegate() {
        return (List<ItemStack>)this.items;
    }
    
    public ListTag createTag() {
        final ListTag ik2 = new ListTag();
        for (final ItemStack bcj4 : this.delegate()) {
            ik2.add(bcj4.save(new CompoundTag()));
        }
        return ik2;
    }
    
    public void fromTag(final ListTag ik) {
        final List<ItemStack> list3 = this.delegate();
        for (int integer4 = 0; integer4 < list3.size(); ++integer4) {
            list3.set(integer4, ItemStack.of(ik.getCompound(integer4)));
        }
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.delegate()) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
