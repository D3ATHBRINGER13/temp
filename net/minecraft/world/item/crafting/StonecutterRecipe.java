package net.minecraft.world.item.crafting;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class StonecutterRecipe extends SingleItemRecipe {
    public StonecutterRecipe(final ResourceLocation qv, final String string, final Ingredient beo, final ItemStack bcj) {
        super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTER, qv, string, beo, bcj);
    }
    
    public boolean matches(final Container ahc, final Level bhr) {
        return this.ingredient.test(ahc.getItem(0));
    }
    
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.STONECUTTER);
    }
}
