package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import java.util.List;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class ArmorDyeRecipe extends CustomRecipe {
    public ArmorDyeRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final List<ItemStack> list5 = (List<ItemStack>)Lists.newArrayList();
        for (int integer6 = 0; integer6 < ayw.getContainerSize(); ++integer6) {
            final ItemStack bcj5 = ayw.getItem(integer6);
            if (!bcj5.isEmpty()) {
                if (bcj5.getItem() instanceof DyeableLeatherItem) {
                    if (!bcj4.isEmpty()) {
                        return false;
                    }
                    bcj4 = bcj5;
                }
                else {
                    if (!(bcj5.getItem() instanceof DyeItem)) {
                        return false;
                    }
                    list5.add(bcj5);
                }
            }
        }
        return !bcj4.isEmpty() && !list5.isEmpty();
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        final List<DyeItem> list3 = (List<DyeItem>)Lists.newArrayList();
        ItemStack bcj4 = ItemStack.EMPTY;
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj5 = ayw.getItem(integer5);
            if (!bcj5.isEmpty()) {
                final Item bce7 = bcj5.getItem();
                if (bce7 instanceof DyeableLeatherItem) {
                    if (!bcj4.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bcj4 = bcj5.copy();
                }
                else {
                    if (!(bce7 instanceof DyeItem)) {
                        return ItemStack.EMPTY;
                    }
                    list3.add(bce7);
                }
            }
        }
        if (bcj4.isEmpty() || list3.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return DyeableLeatherItem.dyeArmor(bcj4, list3);
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}
