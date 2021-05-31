package net.minecraft.client.gui.screens.recipebook;

import javax.annotation.Nullable;
import java.util.Iterator;
import com.mojang.blaze3d.platform.Lighting;
import com.google.common.collect.Lists;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.Minecraft;
import java.util.List;

public class RecipeBookPage {
    private final List<RecipeButton> buttons;
    private RecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay;
    private Minecraft minecraft;
    private final List<RecipeShownListener> showListeners;
    private List<RecipeCollection> recipeCollections;
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;
    private RecipeBook recipeBook;
    private Recipe<?> lastClickedRecipe;
    private RecipeCollection lastClickedRecipeCollection;
    
    public RecipeBookPage() {
        this.buttons = (List<RecipeButton>)Lists.newArrayListWithCapacity(20);
        this.overlay = new OverlayRecipeComponent();
        this.showListeners = (List<RecipeShownListener>)Lists.newArrayList();
        for (int integer2 = 0; integer2 < 20; ++integer2) {
            this.buttons.add(new RecipeButton());
        }
    }
    
    public void init(final Minecraft cyc, final int integer2, final int integer3) {
        this.minecraft = cyc;
        this.recipeBook = cyc.player.getRecipeBook();
        for (int integer4 = 0; integer4 < this.buttons.size(); ++integer4) {
            ((RecipeButton)this.buttons.get(integer4)).setPosition(integer2 + 11 + 25 * (integer4 % 5), integer3 + 31 + 25 * (integer4 / 5));
        }
        (this.forwardButton = new StateSwitchingButton(integer2 + 93, integer3 + 137, 12, 17, false)).initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
        (this.backButton = new StateSwitchingButton(integer2 + 38, integer3 + 137, 12, 17, true)).initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }
    
    public void addListener(final RecipeBookComponent dey) {
        this.showListeners.remove(dey);
        this.showListeners.add(dey);
    }
    
    public void updateCollections(final List<RecipeCollection> list, final boolean boolean2) {
        this.recipeCollections = list;
        this.totalPages = (int)Math.ceil(list.size() / 20.0);
        if (this.totalPages <= this.currentPage || boolean2) {
            this.currentPage = 0;
        }
        this.updateButtonsForPage();
    }
    
    private void updateButtonsForPage() {
        final int integer2 = 20 * this.currentPage;
        for (int integer3 = 0; integer3 < this.buttons.size(); ++integer3) {
            final RecipeButton dfb4 = (RecipeButton)this.buttons.get(integer3);
            if (integer2 + integer3 < this.recipeCollections.size()) {
                final RecipeCollection dfc5 = (RecipeCollection)this.recipeCollections.get(integer2 + integer3);
                dfb4.init(dfc5, this);
                dfb4.visible = true;
            }
            else {
                dfb4.visible = false;
            }
        }
        this.updateArrowButtons();
    }
    
    private void updateArrowButtons() {
        this.forwardButton.visible = (this.totalPages > 1 && this.currentPage < this.totalPages - 1);
        this.backButton.visible = (this.totalPages > 1 && this.currentPage > 0);
    }
    
    public void render(final int integer1, final int integer2, final int integer3, final int integer4, final float float5) {
        if (this.totalPages > 1) {
            final String string7 = new StringBuilder().append(this.currentPage + 1).append("/").append(this.totalPages).toString();
            final int integer5 = this.minecraft.font.width(string7);
            this.minecraft.font.draw(string7, (float)(integer1 - integer5 / 2 + 73), (float)(integer2 + 141), -1);
        }
        Lighting.turnOff();
        this.hoveredButton = null;
        for (final RecipeButton dfb8 : this.buttons) {
            dfb8.render(integer3, integer4, float5);
            if (dfb8.visible && dfb8.isHovered()) {
                this.hoveredButton = dfb8;
            }
        }
        this.backButton.render(integer3, integer4, float5);
        this.forwardButton.render(integer3, integer4, float5);
        this.overlay.render(integer3, integer4, float5);
    }
    
    public void renderTooltip(final int integer1, final int integer2) {
        if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            this.minecraft.screen.renderTooltip(this.hoveredButton.getTooltipText(this.minecraft.screen), integer1, integer2);
        }
    }
    
    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }
    
    @Nullable
    public RecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }
    
    public void setInvisible() {
        this.overlay.setVisible(false);
    }
    
    public boolean mouseClicked(final double double1, final double double2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked(double1, double2, integer3)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            }
            else {
                this.overlay.setVisible(false);
            }
            return true;
        }
        if (this.forwardButton.mouseClicked(double1, double2, integer3)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        if (this.backButton.mouseClicked(double1, double2, integer3)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        for (final RecipeButton dfb12 : this.buttons) {
            if (dfb12.mouseClicked(double1, double2, integer3)) {
                if (integer3 == 0) {
                    this.lastClickedRecipe = dfb12.getRecipe();
                    this.lastClickedRecipeCollection = dfb12.getCollection();
                }
                else if (integer3 == 1 && !this.overlay.isVisible() && !dfb12.isOnlyOption()) {
                    this.overlay.init(this.minecraft, dfb12.getCollection(), dfb12.x, dfb12.y, integer4 + integer6 / 2, integer5 + 13 + integer7 / 2, (float)dfb12.getWidth());
                }
                return true;
            }
        }
        return false;
    }
    
    public void recipesShown(final List<Recipe<?>> list) {
        for (final RecipeShownListener dfd4 : this.showListeners) {
            dfd4.recipesShown(list);
        }
    }
    
    public Minecraft getMinecraft() {
        return this.minecraft;
    }
    
    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }
}
