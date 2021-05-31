package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;

public class RecipeBookTabButton extends StateSwitchingButton {
    private final RecipeBookCategories category;
    private float animationTime;
    
    public RecipeBookTabButton(final RecipeBookCategories cyj) {
        super(0, 0, 35, 27, false);
        this.category = cyj;
        this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }
    
    public void startAnimation(final Minecraft cyc) {
        final ClientRecipeBook cxr3 = cyc.player.getRecipeBook();
        final List<RecipeCollection> list4 = cxr3.getCollection(this.category);
        if (!(cyc.player.containerMenu instanceof RecipeBookMenu)) {
            return;
        }
        for (final RecipeCollection dfc6 : list4) {
            for (final Recipe<?> ber8 : dfc6.getRecipes(cxr3.isFilteringCraftable(cyc.player.containerMenu))) {
                if (cxr3.willHighlight(ber8)) {
                    this.animationTime = 15.0f;
                }
            }
        }
    }
    
    @Override
    public void renderButton(final int integer1, final int integer2, final float float3) {
        if (this.animationTime > 0.0f) {
            final float float4 = 1.0f + 0.1f * (float)Math.sin((double)(this.animationTime / 15.0f * 3.1415927f));
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0f);
            GlStateManager.scalef(1.0f, float4, 1.0f);
            GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0f);
        }
        final Minecraft cyc5 = Minecraft.getInstance();
        cyc5.getTextureManager().bind(this.resourceLocation);
        GlStateManager.disableDepthTest();
        int integer3 = this.xTexStart;
        int integer4 = this.yTexStart;
        if (this.isStateTriggered) {
            integer3 += this.xDiffTex;
        }
        if (this.isHovered()) {
            integer4 += this.yDiffTex;
        }
        int integer5 = this.x;
        if (this.isStateTriggered) {
            integer5 -= 2;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(integer5, this.y, integer3, integer4, this.width, this.height);
        GlStateManager.enableDepthTest();
        Lighting.turnOnGui();
        GlStateManager.disableLighting();
        this.renderIcon(cyc5.getItemRenderer());
        GlStateManager.enableLighting();
        Lighting.turnOff();
        if (this.animationTime > 0.0f) {
            GlStateManager.popMatrix();
            this.animationTime -= float3;
        }
    }
    
    private void renderIcon(final ItemRenderer dsv) {
        final List<ItemStack> list3 = this.category.getIconItems();
        final int integer4 = this.isStateTriggered ? -2 : 0;
        if (list3.size() == 1) {
            dsv.renderAndDecorateItem((ItemStack)list3.get(0), this.x + 9 + integer4, this.y + 5);
        }
        else if (list3.size() == 2) {
            dsv.renderAndDecorateItem((ItemStack)list3.get(0), this.x + 3 + integer4, this.y + 5);
            dsv.renderAndDecorateItem((ItemStack)list3.get(1), this.x + 14 + integer4, this.y + 5);
        }
    }
    
    public RecipeBookCategories getCategory() {
        return this.category;
    }
    
    public boolean updateVisibility(final ClientRecipeBook cxr) {
        final List<RecipeCollection> list3 = cxr.getCollection(this.category);
        this.visible = false;
        if (list3 != null) {
            for (final RecipeCollection dfc5 : list3) {
                if (dfc5.hasKnownRecipes() && dfc5.hasFitting()) {
                    this.visible = true;
                    break;
                }
            }
        }
        return this.visible;
    }
}
