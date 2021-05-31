package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.Container;

public abstract class RecipeBookMenu<C extends Container> extends AbstractContainerMenu {
    public RecipeBookMenu(final MenuType<?> azl, final int integer) {
        super(azl, integer);
    }
    
    public void handlePlacement(final boolean boolean1, final Recipe<?> ber, final ServerPlayer vl) {
        new ServerPlaceRecipe<>(this).recipeClicked(vl, ber, boolean1);
    }
    
    public abstract void fillCraftSlotsStackedContents(final StackedContents awi);
    
    public abstract void clearCraftingContent();
    
    public abstract boolean recipeMatches(final Recipe<? super C> ber);
    
    public abstract int getResultSlotIndex();
    
    public abstract int getGridWidth();
    
    public abstract int getGridHeight();
    
    public abstract int getSize();
}
