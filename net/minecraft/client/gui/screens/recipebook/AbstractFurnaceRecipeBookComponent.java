package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.inventory.Slot;
import java.util.Set;
import net.minecraft.world.item.Item;
import java.util.Iterator;

public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {
    private Iterator<Item> iterator;
    private Set<Item> fuels;
    private Slot fuelSlot;
    private Item fuel;
    private float time;
    
    @Override
    protected boolean updateFiltering() {
        final boolean boolean2 = !this.getFilteringCraftable();
        this.setFilteringCraftable(boolean2);
        return boolean2;
    }
    
    protected abstract boolean getFilteringCraftable();
    
    protected abstract void setFilteringCraftable(final boolean boolean1);
    
    @Override
    public boolean isVisible() {
        return this.isGuiOpen();
    }
    
    protected abstract boolean isGuiOpen();
    
    @Override
    protected void setVisible(final boolean boolean1) {
        this.setGuiOpen(boolean1);
        if (!boolean1) {
            this.recipeBookPage.setInvisible();
        }
        this.sendUpdateSettings();
    }
    
    protected abstract void setGuiOpen(final boolean boolean1);
    
    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 182, 28, 18, AbstractFurnaceRecipeBookComponent.RECIPE_BOOK_LOCATION);
    }
    
    @Override
    protected String getFilterButtonTooltip() {
        return I18n.get(this.filterButton.isStateTriggered() ? this.getRecipeFilterName() : "gui.recipebook.toggleRecipes.all");
    }
    
    protected abstract String getRecipeFilterName();
    
    @Override
    public void slotClicked(@Nullable final Slot azx) {
        super.slotClicked(azx);
        if (azx != null && azx.index < this.menu.getSize()) {
            this.fuelSlot = null;
        }
    }
    
    @Override
    public void setupGhostRecipe(final Recipe<?> ber, final List<Slot> list) {
        final ItemStack bcj4 = ber.getResultItem();
        this.ghostRecipe.setRecipe(ber);
        this.ghostRecipe.addIngredient(Ingredient.of(bcj4), ((Slot)list.get(2)).x, ((Slot)list.get(2)).y);
        final NonNullList<Ingredient> fk5 = ber.getIngredients();
        this.fuelSlot = (Slot)list.get(1);
        if (this.fuels == null) {
            this.fuels = this.getFuelItems();
        }
        this.iterator = (Iterator<Item>)this.fuels.iterator();
        this.fuel = null;
        final Iterator<Ingredient> iterator6 = (Iterator<Ingredient>)fk5.iterator();
        for (int integer7 = 0; integer7 < 2; ++integer7) {
            if (!iterator6.hasNext()) {
                return;
            }
            final Ingredient beo8 = (Ingredient)iterator6.next();
            if (!beo8.isEmpty()) {
                final Slot azx9 = (Slot)list.get(integer7);
                this.ghostRecipe.addIngredient(beo8, azx9.x, azx9.y);
            }
        }
    }
    
    protected abstract Set<Item> getFuelItems();
    
    @Override
    public void renderGhostRecipe(final int integer1, final int integer2, final boolean boolean3, final float float4) {
        super.renderGhostRecipe(integer1, integer2, boolean3, float4);
        if (this.fuelSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.time += float4;
        }
        Lighting.turnOnGui();
        GlStateManager.disableLighting();
        final int integer3 = this.fuelSlot.x + integer1;
        final int integer4 = this.fuelSlot.y + integer2;
        GuiComponent.fill(integer3, integer4, integer3 + 16, integer4 + 16, 822018048);
        this.minecraft.getItemRenderer().renderAndDecorateItem(this.minecraft.player, this.getFuel().getDefaultInstance(), integer3, integer4);
        GlStateManager.depthFunc(516);
        GuiComponent.fill(integer3, integer4, integer3 + 16, integer4 + 16, 822083583);
        GlStateManager.depthFunc(515);
        GlStateManager.enableLighting();
        Lighting.turnOff();
    }
    
    private Item getFuel() {
        if (this.fuel == null || this.time > 30.0f) {
            this.time = 0.0f;
            if (this.iterator == null || !this.iterator.hasNext()) {
                if (this.fuels == null) {
                    this.fuels = this.getFuelItems();
                }
                this.iterator = (Iterator<Item>)this.fuels.iterator();
            }
            this.fuel = (Item)this.iterator.next();
        }
        return this.fuel;
    }
}
