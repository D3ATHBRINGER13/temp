package net.minecraft.client.gui.components.toasts;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.resources.language.I18n;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.world.item.crafting.Recipe;
import java.util.List;

public class RecipeToast implements Toast {
    private final List<Recipe<?>> recipes;
    private long lastChanged;
    private boolean changed;
    
    public RecipeToast(final Recipe<?> ber) {
        (this.recipes = (List<Recipe<?>>)Lists.newArrayList()).add(ber);
    }
    
    public Visibility render(final ToastComponent dan, final long long2) {
        if (this.changed) {
            this.lastChanged = long2;
            this.changed = false;
        }
        if (this.recipes.isEmpty()) {
            return Visibility.HIDE;
        }
        dan.getMinecraft().getTextureManager().bind(RecipeToast.TEXTURE);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        dan.blit(0, 0, 0, 32, 160, 32);
        dan.getMinecraft().font.draw(I18n.get("recipe.toast.title"), 30.0f, 7.0f, -11534256);
        dan.getMinecraft().font.draw(I18n.get("recipe.toast.description"), 30.0f, 18.0f, -16777216);
        Lighting.turnOnGui();
        final Recipe<?> ber5 = this.recipes.get((int)(long2 / (5000L / this.recipes.size()) % this.recipes.size()));
        final ItemStack bcj6 = ber5.getToastSymbol();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6f, 0.6f, 1.0f);
        dan.getMinecraft().getItemRenderer().renderAndDecorateItem(null, bcj6, 3, 3);
        GlStateManager.popMatrix();
        dan.getMinecraft().getItemRenderer().renderAndDecorateItem(null, ber5.getResultItem(), 8, 8);
        return (long2 - this.lastChanged >= 5000L) ? Visibility.HIDE : Visibility.SHOW;
    }
    
    public void addItem(final Recipe<?> ber) {
        if (this.recipes.add(ber)) {
            this.changed = true;
        }
    }
    
    public static void addOrUpdate(final ToastComponent dan, final Recipe<?> ber) {
        final RecipeToast dak3 = dan.<RecipeToast>getToast((java.lang.Class<? extends RecipeToast>)RecipeToast.class, RecipeToast.NO_TOKEN);
        if (dak3 == null) {
            dan.addToast(new RecipeToast(ber));
        }
        else {
            dak3.addItem(ber);
        }
    }
}
