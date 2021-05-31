package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import net.minecraft.util.Mth;
import java.util.Collections;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import com.google.common.collect.Lists;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.GuiComponent;

public class OverlayRecipeComponent extends GuiComponent implements Widget, GuiEventListener {
    private static final ResourceLocation RECIPE_BOOK_LOCATION;
    private final List<OverlayRecipeButton> recipeButtons;
    private boolean isVisible;
    private int x;
    private int y;
    private Minecraft minecraft;
    private RecipeCollection collection;
    private Recipe<?> lastRecipeClicked;
    private float time;
    private boolean isFurnaceMenu;
    
    public OverlayRecipeComponent() {
        this.recipeButtons = (List<OverlayRecipeButton>)Lists.newArrayList();
    }
    
    public void init(final Minecraft cyc, final RecipeCollection dfc, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.minecraft = cyc;
        this.collection = dfc;
        if (cyc.player.containerMenu instanceof AbstractFurnaceMenu) {
            this.isFurnaceMenu = true;
        }
        final boolean boolean9 = cyc.player.getRecipeBook().isFilteringCraftable(cyc.player.containerMenu);
        final List<Recipe<?>> list10 = dfc.getDisplayRecipes(true);
        final List<Recipe<?>> list11 = (List<Recipe<?>>)(boolean9 ? Collections.emptyList() : dfc.getDisplayRecipes(false));
        final int integer7 = list10.size();
        final int integer8 = integer7 + list11.size();
        final int integer9 = (integer8 <= 16) ? 4 : 5;
        final int integer10 = (int)Math.ceil((double)(integer8 / (float)integer9));
        this.x = integer3;
        this.y = integer4;
        final int integer11 = 25;
        final float float8 = (float)(this.x + Math.min(integer8, integer9) * 25);
        final float float9 = (float)(integer5 + 50);
        if (float8 > float9) {
            this.x -= (int)(float7 * (int)((float8 - float9) / float7));
        }
        final float float10 = (float)(this.y + integer10 * 25);
        final float float11 = (float)(integer6 + 50);
        if (float10 > float11) {
            this.y -= (int)(float7 * Mth.ceil((float10 - float11) / float7));
        }
        final float float12 = (float)this.y;
        final float float13 = (float)(integer6 - 100);
        if (float12 < float13) {
            this.y -= (int)(float7 * Mth.ceil((float12 - float13) / float7));
        }
        this.isVisible = true;
        this.recipeButtons.clear();
        for (int integer12 = 0; integer12 < integer8; ++integer12) {
            final boolean boolean10 = integer12 < integer7;
            final Recipe<?> ber25 = (boolean10 ? list10.get(integer12) : ((Recipe)list11.get(integer12 - integer7)));
            final int integer13 = this.x + 4 + 25 * (integer12 % integer9);
            final int integer14 = this.y + 5 + 25 * (integer12 / integer9);
            if (this.isFurnaceMenu) {
                this.recipeButtons.add(new OverlaySmeltingRecipeButton(integer13, integer14, ber25, boolean10));
            }
            else {
                this.recipeButtons.add(new OverlayRecipeButton(integer13, integer14, ber25, boolean10));
            }
        }
        this.lastRecipeClicked = null;
    }
    
    @Override
    public boolean changeFocus(final boolean boolean1) {
        return false;
    }
    
    public RecipeCollection getRecipeCollection() {
        return this.collection;
    }
    
    public Recipe<?> getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (integer != 0) {
            return false;
        }
        for (final OverlayRecipeButton a8 : this.recipeButtons) {
            if (a8.mouseClicked(double1, double2, integer)) {
                this.lastRecipeClicked = a8.recipe;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isMouseOver(final double double1, final double double2) {
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (!this.isVisible) {
            return;
        }
        this.time += float3;
        Lighting.turnOnGui();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(OverlayRecipeComponent.RECIPE_BOOK_LOCATION);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 170.0f);
        final int integer3 = (this.recipeButtons.size() <= 16) ? 4 : 5;
        final int integer4 = Math.min(this.recipeButtons.size(), integer3);
        final int integer5 = Mth.ceil(this.recipeButtons.size() / (float)integer3);
        final int integer6 = 24;
        final int integer7 = 4;
        final int integer8 = 82;
        final int integer9 = 208;
        this.nineInchSprite(integer4, integer5, 24, 4, 82, 208);
        GlStateManager.disableBlend();
        Lighting.turnOff();
        for (final OverlayRecipeButton a13 : this.recipeButtons) {
            a13.render(integer1, integer2, float3);
        }
        GlStateManager.popMatrix();
    }
    
    private void nineInchSprite(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.blit(this.x, this.y, integer5, integer6, integer4, integer4);
        this.blit(this.x + integer4 * 2 + integer1 * integer3, this.y, integer5 + integer3 + integer4, integer6, integer4, integer4);
        this.blit(this.x, this.y + integer4 * 2 + integer2 * integer3, integer5, integer6 + integer3 + integer4, integer4, integer4);
        this.blit(this.x + integer4 * 2 + integer1 * integer3, this.y + integer4 * 2 + integer2 * integer3, integer5 + integer3 + integer4, integer6 + integer3 + integer4, integer4, integer4);
        for (int integer7 = 0; integer7 < integer1; ++integer7) {
            this.blit(this.x + integer4 + integer7 * integer3, this.y, integer5 + integer4, integer6, integer3, integer4);
            this.blit(this.x + integer4 + (integer7 + 1) * integer3, this.y, integer5 + integer4, integer6, integer4, integer4);
            for (int integer8 = 0; integer8 < integer2; ++integer8) {
                if (integer7 == 0) {
                    this.blit(this.x, this.y + integer4 + integer8 * integer3, integer5, integer6 + integer4, integer4, integer3);
                    this.blit(this.x, this.y + integer4 + (integer8 + 1) * integer3, integer5, integer6 + integer4, integer4, integer4);
                }
                this.blit(this.x + integer4 + integer7 * integer3, this.y + integer4 + integer8 * integer3, integer5 + integer4, integer6 + integer4, integer3, integer3);
                this.blit(this.x + integer4 + (integer7 + 1) * integer3, this.y + integer4 + integer8 * integer3, integer5 + integer4, integer6 + integer4, integer4, integer3);
                this.blit(this.x + integer4 + integer7 * integer3, this.y + integer4 + (integer8 + 1) * integer3, integer5 + integer4, integer6 + integer4, integer3, integer4);
                this.blit(this.x + integer4 + (integer7 + 1) * integer3 - 1, this.y + integer4 + (integer8 + 1) * integer3 - 1, integer5 + integer4, integer6 + integer4, integer4 + 1, integer4 + 1);
                if (integer7 == integer1 - 1) {
                    this.blit(this.x + integer4 * 2 + integer1 * integer3, this.y + integer4 + integer8 * integer3, integer5 + integer3 + integer4, integer6 + integer4, integer4, integer3);
                    this.blit(this.x + integer4 * 2 + integer1 * integer3, this.y + integer4 + (integer8 + 1) * integer3, integer5 + integer3 + integer4, integer6 + integer4, integer4, integer4);
                }
            }
            this.blit(this.x + integer4 + integer7 * integer3, this.y + integer4 * 2 + integer2 * integer3, integer5 + integer4, integer6 + integer3 + integer4, integer3, integer4);
            this.blit(this.x + integer4 + (integer7 + 1) * integer3, this.y + integer4 * 2 + integer2 * integer3, integer5 + integer4, integer6 + integer3 + integer4, integer4, integer4);
        }
    }
    
    public void setVisible(final boolean boolean1) {
        this.isVisible = boolean1;
    }
    
    public boolean isVisible() {
        return this.isVisible;
    }
    
    static {
        RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    }
    
    class OverlaySmeltingRecipeButton extends OverlayRecipeButton {
        public OverlaySmeltingRecipeButton(final int integer2, final int integer3, final Recipe<?> ber, final boolean boolean5) {
            super(integer2, integer3, ber, boolean5);
        }
        
        @Override
        protected void calculateIngredientsPositions(final Recipe<?> ber) {
            final ItemStack[] arr3 = ber.getIngredients().get(0).getItems();
            this.ingredientPos.add(new Pos(10, 10, arr3));
        }
    }
    
    class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {
        private final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<Pos> ingredientPos;
        
        public OverlayRecipeButton(final int integer2, final int integer3, final Recipe<?> ber, final boolean boolean5) {
            super(integer2, integer3, 200, 20, "");
            this.ingredientPos = (List<Pos>)Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = ber;
            this.isCraftable = boolean5;
            this.calculateIngredientsPositions(ber);
        }
        
        protected void calculateIngredientsPositions(final Recipe<?> ber) {
            this.placeRecipe(3, 3, -1, ber, (java.util.Iterator<Ingredient>)ber.getIngredients().iterator(), 0);
        }
        
        @Override
        public void addItemToSlot(final Iterator<Ingredient> iterator, final int integer2, final int integer3, final int integer4, final int integer5) {
            final ItemStack[] arr7 = ((Ingredient)iterator.next()).getItems();
            if (arr7.length != 0) {
                this.ingredientPos.add(new Pos(3 + integer5 * 7, 3 + integer4 * 7, arr7));
            }
        }
        
        @Override
        public void renderButton(final int integer1, final int integer2, final float float3) {
            Lighting.turnOnGui();
            GlStateManager.enableAlphaTest();
            OverlayRecipeComponent.this.minecraft.getTextureManager().bind(OverlayRecipeComponent.RECIPE_BOOK_LOCATION);
            int integer3 = 152;
            if (!this.isCraftable) {
                integer3 += 26;
            }
            int integer4 = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
            if (this.isHovered()) {
                integer4 += 26;
            }
            this.blit(this.x, this.y, integer3, integer4, this.width, this.height);
            for (final Pos a8 : this.ingredientPos) {
                GlStateManager.pushMatrix();
                final float float4 = 0.42f;
                final int integer5 = (int)((this.x + a8.x) / 0.42f - 3.0f);
                final int integer6 = (int)((this.y + a8.y) / 0.42f - 3.0f);
                GlStateManager.scalef(0.42f, 0.42f, 1.0f);
                GlStateManager.enableLighting();
                OverlayRecipeComponent.this.minecraft.getItemRenderer().renderAndDecorateItem(a8.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0f) % a8.ingredients.length], integer5, integer6);
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
            }
            GlStateManager.disableAlphaTest();
            Lighting.turnOff();
        }
        
        public class Pos {
            public final ItemStack[] ingredients;
            public final int x;
            public final int y;
            
            public Pos(final int integer2, final int integer3, final ItemStack[] arr) {
                this.x = integer2;
                this.y = integer3;
                this.ingredients = arr;
            }
        }
    }
}
