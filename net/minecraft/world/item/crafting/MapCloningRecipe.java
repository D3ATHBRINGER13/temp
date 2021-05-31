package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class MapCloningRecipe extends CustomRecipe {
    public MapCloningRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        int integer4 = 0;
        ItemStack bcj5 = ItemStack.EMPTY;
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj6 = ayw.getItem(integer5);
            if (!bcj6.isEmpty()) {
                if (bcj6.getItem() == Items.FILLED_MAP) {
                    if (!bcj5.isEmpty()) {
                        return false;
                    }
                    bcj5 = bcj6;
                }
                else {
                    if (bcj6.getItem() != Items.MAP) {
                        return false;
                    }
                    ++integer4;
                }
            }
        }
        return !bcj5.isEmpty() && integer4 > 0;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        int integer3 = 0;
        ItemStack bcj4 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < ayw.getContainerSize(); ++integer4) {
            final ItemStack bcj5 = ayw.getItem(integer4);
            if (!bcj5.isEmpty()) {
                if (bcj5.getItem() == Items.FILLED_MAP) {
                    if (!bcj4.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bcj4 = bcj5;
                }
                else {
                    if (bcj5.getItem() != Items.MAP) {
                        return ItemStack.EMPTY;
                    }
                    ++integer3;
                }
            }
        }
        if (bcj4.isEmpty() || integer3 < 1) {
            return ItemStack.EMPTY;
        }
        final ItemStack bcj6 = bcj4.copy();
        bcj6.setCount(integer3 + 1);
        return bcj6;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 >= 3 && integer2 >= 3;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}
