package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.Item;
import java.util.Set;

public class SmeltingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    @Override
    protected boolean getFilteringCraftable() {
        return this.book.isFurnaceFilteringCraftable();
    }
    
    @Override
    protected void setFilteringCraftable(final boolean boolean1) {
        this.book.setFurnaceFilteringCraftable(boolean1);
    }
    
    @Override
    protected boolean isGuiOpen() {
        return this.book.isFurnaceGuiOpen();
    }
    
    @Override
    protected void setGuiOpen(final boolean boolean1) {
        this.book.setFurnaceGuiOpen(boolean1);
    }
    
    @Override
    protected String getRecipeFilterName() {
        return "gui.recipebook.toggleRecipes.smeltable";
    }
    
    @Override
    protected Set<Item> getFuelItems() {
        return (Set<Item>)AbstractFurnaceBlockEntity.getFuel().keySet();
    }
}
