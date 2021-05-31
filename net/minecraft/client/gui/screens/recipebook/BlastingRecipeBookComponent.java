package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.Item;
import java.util.Set;

public class BlastingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
    @Override
    protected boolean getFilteringCraftable() {
        return this.book.isBlastingFurnaceFilteringCraftable();
    }
    
    @Override
    protected void setFilteringCraftable(final boolean boolean1) {
        this.book.setBlastingFurnaceFilteringCraftable(boolean1);
    }
    
    @Override
    protected boolean isGuiOpen() {
        return this.book.isBlastingFurnaceGuiOpen();
    }
    
    @Override
    protected void setGuiOpen(final boolean boolean1) {
        this.book.setBlastingFurnaceGuiOpen(boolean1);
    }
    
    @Override
    protected String getRecipeFilterName() {
        return "gui.recipebook.toggleRecipes.blastable";
    }
    
    @Override
    protected Set<Item> getFuelItems() {
        return (Set<Item>)AbstractFurnaceBlockEntity.getFuel().keySet();
    }
}
