package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class BookCloningRecipe extends CustomRecipe {
    public BookCloningRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        int integer4 = 0;
        ItemStack bcj5 = ItemStack.EMPTY;
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj6 = ayw.getItem(integer5);
            if (!bcj6.isEmpty()) {
                if (bcj6.getItem() == Items.WRITTEN_BOOK) {
                    if (!bcj5.isEmpty()) {
                        return false;
                    }
                    bcj5 = bcj6;
                }
                else {
                    if (bcj6.getItem() != Items.WRITABLE_BOOK) {
                        return false;
                    }
                    ++integer4;
                }
            }
        }
        return !bcj5.isEmpty() && bcj5.hasTag() && integer4 > 0;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        int integer3 = 0;
        ItemStack bcj4 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < ayw.getContainerSize(); ++integer4) {
            final ItemStack bcj5 = ayw.getItem(integer4);
            if (!bcj5.isEmpty()) {
                if (bcj5.getItem() == Items.WRITTEN_BOOK) {
                    if (!bcj4.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bcj4 = bcj5;
                }
                else {
                    if (bcj5.getItem() != Items.WRITABLE_BOOK) {
                        return ItemStack.EMPTY;
                    }
                    ++integer3;
                }
            }
        }
        if (bcj4.isEmpty() || !bcj4.hasTag() || integer3 < 1 || WrittenBookItem.getGeneration(bcj4) >= 2) {
            return ItemStack.EMPTY;
        }
        final ItemStack bcj6 = new ItemStack(Items.WRITTEN_BOOK, integer3);
        final CompoundTag id6 = bcj4.getTag().copy();
        id6.putInt("generation", WrittenBookItem.getGeneration(bcj4) + 1);
        bcj6.setTag(id6);
        return bcj6;
    }
    
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer ayw) {
        final NonNullList<ItemStack> fk3 = NonNullList.<ItemStack>withSize(ayw.getContainerSize(), ItemStack.EMPTY);
        for (int integer4 = 0; integer4 < fk3.size(); ++integer4) {
            final ItemStack bcj5 = ayw.getItem(integer4);
            if (bcj5.getItem().hasCraftingRemainingItem()) {
                fk3.set(integer4, new ItemStack(bcj5.getItem().getCraftingRemainingItem()));
            }
            else if (bcj5.getItem() instanceof WrittenBookItem) {
                final ItemStack bcj6 = bcj5.copy();
                bcj6.setCount(1);
                fk3.set(integer4, bcj6);
                break;
            }
        }
        return fk3;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 >= 3 && integer2 >= 3;
    }
}
