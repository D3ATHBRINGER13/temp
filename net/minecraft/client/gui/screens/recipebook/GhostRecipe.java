package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.Ingredient;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;

public class GhostRecipe {
    private Recipe<?> recipe;
    private final List<GhostIngredient> ingredients;
    private float time;
    
    public GhostRecipe() {
        this.ingredients = (List<GhostIngredient>)Lists.newArrayList();
    }
    
    public void clear() {
        this.recipe = null;
        this.ingredients.clear();
        this.time = 0.0f;
    }
    
    public void addIngredient(final Ingredient beo, final int integer2, final int integer3) {
        this.ingredients.add(new GhostIngredient(beo, integer2, integer3));
    }
    
    public GhostIngredient get(final int integer) {
        return (GhostIngredient)this.ingredients.get(integer);
    }
    
    public int size() {
        return this.ingredients.size();
    }
    
    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }
    
    public void setRecipe(final Recipe<?> ber) {
        this.recipe = ber;
    }
    
    public void render(final Minecraft cyc, final int integer2, final int integer3, final boolean boolean4, final float float5) {
        if (!Screen.hasControlDown()) {
            this.time += float5;
        }
        Lighting.turnOnGui();
        GlStateManager.disableLighting();
        for (int integer4 = 0; integer4 < this.ingredients.size(); ++integer4) {
            final GhostIngredient a8 = (GhostIngredient)this.ingredients.get(integer4);
            final int integer5 = a8.getX() + integer2;
            final int integer6 = a8.getY() + integer3;
            if (integer4 == 0 && boolean4) {
                GuiComponent.fill(integer5 - 4, integer6 - 4, integer5 + 20, integer6 + 20, 822018048);
            }
            else {
                GuiComponent.fill(integer5, integer6, integer5 + 16, integer6 + 16, 822018048);
            }
            final ItemStack bcj11 = a8.getItem();
            final ItemRenderer dsv12 = cyc.getItemRenderer();
            dsv12.renderAndDecorateItem(cyc.player, bcj11, integer5, integer6);
            GlStateManager.depthFunc(516);
            GuiComponent.fill(integer5, integer6, integer5 + 16, integer6 + 16, 822083583);
            GlStateManager.depthFunc(515);
            if (integer4 == 0) {
                dsv12.renderGuiItemDecorations(cyc.font, bcj11, integer5, integer6);
            }
            GlStateManager.enableLighting();
        }
        Lighting.turnOff();
    }
    
    public class GhostIngredient {
        private final Ingredient ingredient;
        private final int x;
        private final int y;
        
        public GhostIngredient(final Ingredient beo, final int integer3, final int integer4) {
            this.ingredient = beo;
            this.x = integer3;
            this.y = integer4;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public ItemStack getItem() {
            final ItemStack[] arr2 = this.ingredient.getItems();
            return arr2[Mth.floor(GhostRecipe.this.time / 30.0f) % arr2.length];
        }
    }
}
