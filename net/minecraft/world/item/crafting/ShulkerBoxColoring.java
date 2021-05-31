package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class ShulkerBoxColoring extends CustomRecipe {
    public ShulkerBoxColoring(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        int integer4 = 0;
        int integer5 = 0;
        for (int integer6 = 0; integer6 < ayw.getContainerSize(); ++integer6) {
            final ItemStack bcj7 = ayw.getItem(integer6);
            if (!bcj7.isEmpty()) {
                if (Block.byItem(bcj7.getItem()) instanceof ShulkerBoxBlock) {
                    ++integer4;
                }
                else {
                    if (!(bcj7.getItem() instanceof DyeItem)) {
                        return false;
                    }
                    ++integer5;
                }
                if (integer5 > 1 || integer4 > 1) {
                    return false;
                }
            }
        }
        return integer4 == 1 && integer5 == 1;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        ItemStack bcj3 = ItemStack.EMPTY;
        DyeItem bbh4 = (DyeItem)Items.WHITE_DYE;
        for (int integer5 = 0; integer5 < ayw.getContainerSize(); ++integer5) {
            final ItemStack bcj4 = ayw.getItem(integer5);
            if (!bcj4.isEmpty()) {
                final Item bce7 = bcj4.getItem();
                if (Block.byItem(bce7) instanceof ShulkerBoxBlock) {
                    bcj3 = bcj4;
                }
                else if (bce7 instanceof DyeItem) {
                    bbh4 = (DyeItem)bce7;
                }
            }
        }
        final ItemStack bcj5 = ShulkerBoxBlock.getColoredItemStack(bbh4.getDyeColor());
        if (bcj3.hasTag()) {
            bcj5.setTag(bcj3.getTag().copy());
        }
        return bcj5;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 * integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHULKER_BOX_COLORING;
    }
}
