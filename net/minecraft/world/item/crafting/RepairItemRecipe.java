package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.Lists;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class RepairItemRecipe extends CustomRecipe {
    public RepairItemRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        final List<ItemStack> list4 = (List<ItemStack>)Lists.newArrayList();
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj6 = ayw.getItem(integer5);
            if (!bcj6.isEmpty()) {
                list4.add(bcj6);
                if (list4.size() > 1) {
                    final ItemStack bcj7 = (ItemStack)list4.get(0);
                    if (bcj6.getItem() != bcj7.getItem() || bcj7.getCount() != 1 || bcj6.getCount() != 1 || !bcj7.getItem().canBeDepleted()) {
                        return false;
                    }
                }
            }
        }
        return list4.size() == 2;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        final List<ItemStack> list3 = (List<ItemStack>)Lists.newArrayList();
        for (int integer4 = 0; integer4 < ayw.getContainerSize(); ++integer4) {
            final ItemStack bcj5 = ayw.getItem(integer4);
            if (!bcj5.isEmpty()) {
                list3.add(bcj5);
                if (list3.size() > 1) {
                    final ItemStack bcj6 = (ItemStack)list3.get(0);
                    if (bcj5.getItem() != bcj6.getItem() || bcj6.getCount() != 1 || bcj5.getCount() != 1 || !bcj6.getItem().canBeDepleted()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        if (list3.size() == 2) {
            final ItemStack bcj7 = (ItemStack)list3.get(0);
            final ItemStack bcj5 = (ItemStack)list3.get(1);
            if (bcj7.getItem() == bcj5.getItem() && bcj7.getCount() == 1 && bcj5.getCount() == 1 && bcj7.getItem().canBeDepleted()) {
                final Item bce6 = bcj7.getItem();
                final int integer5 = bce6.getMaxDamage() - bcj7.getDamageValue();
                final int integer6 = bce6.getMaxDamage() - bcj5.getDamageValue();
                final int integer7 = integer5 + integer6 + bce6.getMaxDamage() * 5 / 100;
                int integer8 = bce6.getMaxDamage() - integer7;
                if (integer8 < 0) {
                    integer8 = 0;
                }
                final ItemStack bcj8 = new ItemStack(bcj7.getItem());
                bcj8.setDamageValue(integer8);
                return bcj8;
            }
        }
        return ItemStack.EMPTY;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}
