package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class BannerDuplicateRecipe extends CustomRecipe {
    public BannerDuplicateRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        DyeColor bbg4 = null;
        ItemStack bcj5 = null;
        ItemStack bcj6 = null;
        for (int integer7 = 0; integer7 < ayw.getContainerSize(); ++integer7) {
            final ItemStack bcj7 = ayw.getItem(integer7);
            final Item bce9 = bcj7.getItem();
            if (bce9 instanceof BannerItem) {
                final BannerItem baj10 = (BannerItem)bce9;
                if (bbg4 == null) {
                    bbg4 = baj10.getColor();
                }
                else if (bbg4 != baj10.getColor()) {
                    return false;
                }
                final int integer8 = BannerBlockEntity.getPatternCount(bcj7);
                if (integer8 > 6) {
                    return false;
                }
                if (integer8 > 0) {
                    if (bcj5 != null) {
                        return false;
                    }
                    bcj5 = bcj7;
                }
                else {
                    if (bcj6 != null) {
                        return false;
                    }
                    bcj6 = bcj7;
                }
            }
        }
        return bcj5 != null && bcj6 != null;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        for (int integer3 = 0; integer3 < ayw.getContainerSize(); ++integer3) {
            final ItemStack bcj4 = ayw.getItem(integer3);
            if (!bcj4.isEmpty()) {
                final int integer4 = BannerBlockEntity.getPatternCount(bcj4);
                if (integer4 > 0 && integer4 <= 6) {
                    final ItemStack bcj5 = bcj4.copy();
                    bcj5.setCount(1);
                    return bcj5;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer ayw) {
        final NonNullList<ItemStack> fk3 = NonNullList.<ItemStack>withSize(ayw.getContainerSize(), ItemStack.EMPTY);
        for (int integer4 = 0; integer4 < fk3.size(); ++integer4) {
            final ItemStack bcj5 = ayw.getItem(integer4);
            if (!bcj5.isEmpty()) {
                if (bcj5.getItem().hasCraftingRemainingItem()) {
                    fk3.set(integer4, new ItemStack(bcj5.getItem().getCraftingRemainingItem()));
                }
                else if (bcj5.hasTag() && BannerBlockEntity.getPatternCount(bcj5) > 0) {
                    final ItemStack bcj6 = bcj5.copy();
                    bcj6.setCount(1);
                    fk3.set(integer4, bcj6);
                }
            }
        }
        return fk3;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
}
