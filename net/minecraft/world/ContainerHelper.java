package net.minecraft.world;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class ContainerHelper {
    public static ItemStack removeItem(final List<ItemStack> list, final int integer2, final int integer3) {
        if (integer2 < 0 || integer2 >= list.size() || ((ItemStack)list.get(integer2)).isEmpty() || integer3 <= 0) {
            return ItemStack.EMPTY;
        }
        return ((ItemStack)list.get(integer2)).split(integer3);
    }
    
    public static ItemStack takeItem(final List<ItemStack> list, final int integer) {
        if (integer < 0 || integer >= list.size()) {
            return ItemStack.EMPTY;
        }
        return (ItemStack)list.set(integer, ItemStack.EMPTY);
    }
    
    public static CompoundTag saveAllItems(final CompoundTag id, final NonNullList<ItemStack> fk) {
        return saveAllItems(id, fk, true);
    }
    
    public static CompoundTag saveAllItems(final CompoundTag id, final NonNullList<ItemStack> fk, final boolean boolean3) {
        final ListTag ik4 = new ListTag();
        for (int integer5 = 0; integer5 < fk.size(); ++integer5) {
            final ItemStack bcj6 = fk.get(integer5);
            if (!bcj6.isEmpty()) {
                final CompoundTag id2 = new CompoundTag();
                id2.putByte("Slot", (byte)integer5);
                bcj6.save(id2);
                ik4.add(id2);
            }
        }
        if (!ik4.isEmpty() || boolean3) {
            id.put("Items", (Tag)ik4);
        }
        return id;
    }
    
    public static void loadAllItems(final CompoundTag id, final NonNullList<ItemStack> fk) {
        final ListTag ik3 = id.getList("Items", 10);
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            final CompoundTag id2 = ik3.getCompound(integer4);
            final int integer5 = id2.getByte("Slot") & 0xFF;
            if (integer5 >= 0 && integer5 < fk.size()) {
                fk.set(integer5, ItemStack.of(id2));
            }
        }
    }
}
