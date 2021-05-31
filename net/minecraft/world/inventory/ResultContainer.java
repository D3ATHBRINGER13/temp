package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.ContainerHelper;
import java.util.Iterator;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;

public class ResultContainer implements Container, RecipeHolder {
    private final NonNullList<ItemStack> itemStacks;
    private Recipe<?> recipeUsed;
    
    public ResultContainer() {
        this.itemStacks = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
    }
    
    public int getContainerSize() {
        return 1;
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.itemStacks) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public ItemStack getItem(final int integer) {
        return this.itemStacks.get(0);
    }
    
    public ItemStack removeItem(final int integer1, final int integer2) {
        return ContainerHelper.takeItem((List<ItemStack>)this.itemStacks, 0);
    }
    
    public ItemStack removeItemNoUpdate(final int integer) {
        return ContainerHelper.takeItem((List<ItemStack>)this.itemStacks, 0);
    }
    
    public void setItem(final int integer, final ItemStack bcj) {
        this.itemStacks.set(0, bcj);
    }
    
    public void setChanged() {
    }
    
    public boolean stillValid(final Player awg) {
        return true;
    }
    
    public void clearContent() {
        this.itemStacks.clear();
    }
    
    public void setRecipeUsed(@Nullable final Recipe<?> ber) {
        this.recipeUsed = ber;
    }
    
    @Nullable
    public Recipe<?> getRecipeUsed() {
        return this.recipeUsed;
    }
}
