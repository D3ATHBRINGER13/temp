package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class FireworkStarFadeRecipe extends CustomRecipe {
    private static final Ingredient STAR_INGREDIENT;
    
    public FireworkStarFadeRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        boolean boolean4 = false;
        boolean boolean5 = false;
        for (int integer6 = 0; integer6 < ayw.getContainerSize(); ++integer6) {
            final ItemStack bcj7 = ayw.getItem(integer6);
            if (!bcj7.isEmpty()) {
                if (bcj7.getItem() instanceof DyeItem) {
                    boolean4 = true;
                }
                else {
                    if (!FireworkStarFadeRecipe.STAR_INGREDIENT.test(bcj7)) {
                        return false;
                    }
                    if (boolean5) {
                        return false;
                    }
                    boolean5 = true;
                }
            }
        }
        return boolean5 && boolean4;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        final List<Integer> list3 = (List<Integer>)Lists.newArrayList();
        ItemStack bcj4 = null;
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj5 = ayw.getItem(integer5);
            final Item bce7 = bcj5.getItem();
            if (bce7 instanceof DyeItem) {
                list3.add(((DyeItem)bce7).getDyeColor().getFireworkColor());
            }
            else if (FireworkStarFadeRecipe.STAR_INGREDIENT.test(bcj5)) {
                bcj4 = bcj5.copy();
                bcj4.setCount(1);
            }
        }
        if (bcj4 == null || list3.isEmpty()) {
            return ItemStack.EMPTY;
        }
        bcj4.getOrCreateTagElement("Explosion").putIntArray("FadeColors", list3);
        return bcj4;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
    
    static {
        STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);
    }
}
