package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.Item;
import java.util.Set;

public class SmokingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    @Override
    protected boolean getFilteringCraftable() {
        return this.book.isSmokerFilteringCraftable();
    }
    
    @Override
    protected void setFilteringCraftable(final boolean boolean1) {
        this.book.setSmokerFilteringCraftable(boolean1);
    }
    
    @Override
    protected boolean isGuiOpen() {
        return this.book.isSmokerGuiOpen();
    }
    
    @Override
    protected void setGuiOpen(final boolean boolean1) {
        this.book.setSmokerGuiOpen(boolean1);
    }
    
    @Override
    protected String getRecipeFilterName() {
        return "gui.recipebook.toggleRecipes.smokable";
    }
    
    @Override
    protected Set<Item> getFuelItems() {
        return (Set<Item>)AbstractFurnaceBlockEntity.getFuel().keySet();
    }
}
