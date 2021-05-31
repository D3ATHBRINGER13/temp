package net.minecraft.world.item.crafting;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
    public CampfireCookingRecipe(final ResourceLocation qv, final String string, final Ingredient beo, final ItemStack bcj, final float float5, final int integer) {
        super(RecipeType.CAMPFIRE_COOKING, qv, string, beo, bcj, float5, integer);
    }
    
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CAMPFIRE);
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
    }
}
