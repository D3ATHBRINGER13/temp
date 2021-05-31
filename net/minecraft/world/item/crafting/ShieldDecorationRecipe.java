package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class ShieldDecorationRecipe extends CustomRecipe {
    public ShieldDecorationRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        ItemStack bcj4 = ItemStack.EMPTY;
        ItemStack bcj5 = ItemStack.EMPTY;
        for (int integer6 = 0; integer6 < ayw.getContainerSize(); ++integer6) {
            final ItemStack bcj6 = ayw.getItem(integer6);
            if (!bcj6.isEmpty()) {
                if (bcj6.getItem() instanceof BannerItem) {
                    if (!bcj5.isEmpty()) {
                        return false;
                    }
                    bcj5 = bcj6;
                }
                else {
                    if (bcj6.getItem() != Items.SHIELD) {
                        return false;
                    }
                    if (!bcj4.isEmpty()) {
                        return false;
                    }
                    if (bcj6.getTagElement("BlockEntityTag") != null) {
                        return false;
                    }
                    bcj4 = bcj6;
                }
            }
        }
        return !bcj4.isEmpty() && !bcj5.isEmpty();
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        ItemStack bcj3 = ItemStack.EMPTY;
        ItemStack bcj4 = ItemStack.EMPTY;
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj5 = ayw.getItem(integer5);
            if (!bcj5.isEmpty()) {
                if (bcj5.getItem() instanceof BannerItem) {
                    bcj3 = bcj5;
                }
                else if (bcj5.getItem() == Items.SHIELD) {
                    bcj4 = bcj5.copy();
                }
            }
        }
        if (bcj4.isEmpty()) {
            return bcj4;
        }
        final CompoundTag id5 = bcj3.getTagElement("BlockEntityTag");
        final CompoundTag id6 = (id5 == null) ? new CompoundTag() : id5.copy();
        id6.putInt("Base", ((BannerItem)bcj3.getItem()).getColor().getId());
        bcj4.addTagElement("BlockEntityTag", (Tag)id6);
        return bcj4;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}
