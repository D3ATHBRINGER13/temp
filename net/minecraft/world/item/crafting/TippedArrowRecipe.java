package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.Collection;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class TippedArrowRecipe extends CustomRecipe {
    public TippedArrowRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        if (ayw.getWidth() != 3 || ayw.getHeight() != 3) {
            return false;
        }
        for (int integer4 = 0; integer4 < ayw.getWidth(); ++integer4) {
            for (int integer5 = 0; integer5 < ayw.getHeight(); ++integer5) {
                final ItemStack bcj6 = ayw.getItem(integer4 + integer5 * ayw.getWidth());
                if (bcj6.isEmpty()) {
                    return false;
                }
                final Item bce7 = bcj6.getItem();
                if (integer4 == 1 && integer5 == 1) {
                    if (bce7 != Items.LINGERING_POTION) {
                        return false;
                    }
                }
                else if (bce7 != Items.ARROW) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        final ItemStack bcj3 = ayw.getItem(1 + ayw.getWidth());
        if (bcj3.getItem() != Items.LINGERING_POTION) {
            return ItemStack.EMPTY;
        }
        final ItemStack bcj4 = new ItemStack(Items.TIPPED_ARROW, 8);
        PotionUtils.setPotion(bcj4, PotionUtils.getPotion(bcj3));
        PotionUtils.setCustomEffects(bcj4, (Collection<MobEffectInstance>)PotionUtils.getCustomEffects(bcj3));
        return bcj4;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 >= 2 && integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}
