package net.minecraft.recipebook;

import net.minecraft.world.inventory.Slot;
import java.util.Iterator;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.Container;

public class ServerPlaceSmeltingRecipe<C extends Container> extends ServerPlaceRecipe<C> {
    private boolean recipeMatchesPlaced;
    
    public ServerPlaceSmeltingRecipe(final RecipeBookMenu<C> azq) {
        super(azq);
    }
    
    @Override
    protected void handleRecipeClicked(final Recipe<C> ber, final boolean boolean2) {
        this.recipeMatchesPlaced = this.menu.recipeMatches(ber);
        final int integer4 = this.stackedContents.getBiggestCraftableStack(ber, null);
        if (this.recipeMatchesPlaced) {
            final ItemStack bcj5 = this.menu.getSlot(0).getItem();
            if (bcj5.isEmpty() || integer4 <= bcj5.getCount()) {
                return;
            }
        }
        final int integer5 = this.getStackSize(boolean2, integer4, this.recipeMatchesPlaced);
        final IntList intList6 = (IntList)new IntArrayList();
        if (!this.stackedContents.canCraft(ber, intList6, integer5)) {
            return;
        }
        if (!this.recipeMatchesPlaced) {
            this.moveItemToInventory(this.menu.getResultSlotIndex());
            this.moveItemToInventory(0);
        }
        this.placeRecipe(integer5, intList6);
    }
    
    @Override
    protected void clearGrid() {
        this.moveItemToInventory(this.menu.getResultSlotIndex());
        super.clearGrid();
    }
    
    protected void placeRecipe(final int integer, final IntList intList) {
        final Iterator<Integer> iterator4 = (Iterator<Integer>)intList.iterator();
        final Slot azx5 = this.menu.getSlot(0);
        final ItemStack bcj6 = StackedContents.fromStackingIndex((int)iterator4.next());
        if (bcj6.isEmpty()) {
            return;
        }
        int integer2 = Math.min(bcj6.getMaxStackSize(), integer);
        if (this.recipeMatchesPlaced) {
            integer2 -= azx5.getItem().getCount();
        }
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.moveItemToGrid(azx5, bcj6);
        }
    }
}
