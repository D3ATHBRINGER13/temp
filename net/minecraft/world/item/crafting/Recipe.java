package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;

public interface Recipe<C extends Container> {
    boolean matches(final C ahc, final Level bhr);
    
    ItemStack assemble(final C ahc);
    
    boolean canCraftInDimensions(final int integer1, final int integer2);
    
    ItemStack getResultItem();
    
    default NonNullList<ItemStack> getRemainingItems(final C ahc) {
        final NonNullList<ItemStack> fk3 = NonNullList.<ItemStack>withSize(ahc.getContainerSize(), ItemStack.EMPTY);
        for (int integer4 = 0; integer4 < fk3.size(); ++integer4) {
            final Item bce5 = ahc.getItem(integer4).getItem();
            if (bce5.hasCraftingRemainingItem()) {
                fk3.set(integer4, new ItemStack(bce5.getCraftingRemainingItem()));
            }
        }
        return fk3;
    }
    
    default NonNullList<Ingredient> getIngredients() {
        return NonNullList.<Ingredient>create();
    }
    
    default boolean isSpecial() {
        return false;
    }
    
    default String getGroup() {
        return "";
    }
    
    default ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }
    
    ResourceLocation getId();
    
    RecipeSerializer<?> getSerializer();
    
    RecipeType<?> getType();
}
