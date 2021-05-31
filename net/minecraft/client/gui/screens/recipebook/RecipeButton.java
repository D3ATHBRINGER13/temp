package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.client.resources.language.I18n;
import java.util.Collection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.gui.screens.Screen;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.AbstractWidget;

public class RecipeButton extends AbstractWidget {
    private static final ResourceLocation RECIPE_BOOK_LOCATION;
    private RecipeBookMenu<?> menu;
    private RecipeBook book;
    private RecipeCollection collection;
    private float time;
    private float animationTime;
    private int currentIndex;
    
    public RecipeButton() {
        super(0, 0, 25, 25, "");
    }
    
    public void init(final RecipeCollection dfc, final RecipeBookPage dez) {
        this.collection = dfc;
        this.menu = dez.getMinecraft().player.containerMenu;
        this.book = dez.getRecipeBook();
        final List<Recipe<?>> list4 = dfc.getRecipes(this.book.isFilteringCraftable(this.menu));
        for (final Recipe<?> ber6 : list4) {
            if (this.book.willHighlight(ber6)) {
                dez.recipesShown(list4);
                this.animationTime = 15.0f;
                break;
            }
        }
    }
    
    public RecipeCollection getCollection() {
        return this.collection;
    }
    
    public void setPosition(final int integer1, final int integer2) {
        this.x = integer1;
        this.y = integer2;
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        if (!Screen.hasControlDown()) {
            this.time += float3;
        }
        Lighting.turnOnGui();
        final Minecraft cyc5 = Minecraft.getInstance();
        cyc5.getTextureManager().bind(RecipeButton.RECIPE_BOOK_LOCATION);
        GlStateManager.disableLighting();
        int integer3 = 29;
        if (!this.collection.hasCraftable()) {
            integer3 += 25;
        }
        int integer4 = 206;
        if (this.collection.getRecipes(this.book.isFilteringCraftable(this.menu)).size() > 1) {
            integer4 += 25;
        }
        final boolean boolean8 = this.animationTime > 0.0f;
        if (boolean8) {
            final float float4 = 1.0f + 0.1f * (float)Math.sin((double)(this.animationTime / 15.0f * 3.1415927f));
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0f);
            GlStateManager.scalef(float4, float4, 1.0f);
            GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0f);
            this.animationTime -= float3;
        }
        this.blit(this.x, this.y, integer3, integer4, this.width, this.height);
        final List<Recipe<?>> list9 = this.getOrderedRecipes();
        this.currentIndex = Mth.floor(this.time / 30.0f) % list9.size();
        final ItemStack bcj10 = ((Recipe)list9.get(this.currentIndex)).getResultItem();
        int integer5 = 4;
        if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
            cyc5.getItemRenderer().renderAndDecorateItem(bcj10, this.x + integer5 + 1, this.y + integer5 + 1);
            --integer5;
        }
        cyc5.getItemRenderer().renderAndDecorateItem(bcj10, this.x + integer5, this.y + integer5);
        if (boolean8) {
            GlStateManager.popMatrix();
        }
        GlStateManager.enableLighting();
        Lighting.turnOff();
    }
    
    private List<Recipe<?>> getOrderedRecipes() {
        final List<Recipe<?>> list2 = this.collection.getDisplayRecipes(true);
        if (!this.book.isFilteringCraftable(this.menu)) {
            list2.addAll((Collection)this.collection.getDisplayRecipes(false));
        }
        return list2;
    }
    
    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }
    
    public Recipe<?> getRecipe() {
        final List<Recipe<?>> list2 = this.getOrderedRecipes();
        return list2.get(this.currentIndex);
    }
    
    public List<String> getTooltipText(final Screen dcl) {
        final ItemStack bcj3 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem();
        final List<String> list4 = dcl.getTooltipFromItem(bcj3);
        if (this.collection.getRecipes(this.book.isFilteringCraftable(this.menu)).size() > 1) {
            list4.add(I18n.get("gui.recipebook.moreRecipes"));
        }
        return list4;
    }
    
    @Override
    public int getWidth() {
        return 25;
    }
    
    @Override
    protected boolean isValidClickButton(final int integer) {
        return integer == 0 || integer == 1;
    }
    
    static {
        RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    }
}
