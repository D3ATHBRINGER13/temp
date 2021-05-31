package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;

public class SuspiciousStewRecipe extends CustomRecipe {
    public SuspiciousStewRecipe(final ResourceLocation qv) {
        super(qv);
    }
    
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        boolean boolean4 = false;
        boolean boolean5 = false;
        boolean boolean6 = false;
        boolean boolean7 = false;
        for (int integer8 = 0; integer8 < ayw.getContainerSize(); ++integer8) {
            final ItemStack bcj9 = ayw.getItem(integer8);
            if (!bcj9.isEmpty()) {
                if (bcj9.getItem() == Blocks.BROWN_MUSHROOM.asItem() && !boolean6) {
                    boolean6 = true;
                }
                else if (bcj9.getItem() == Blocks.RED_MUSHROOM.asItem() && !boolean5) {
                    boolean5 = true;
                }
                else if (bcj9.getItem().is(ItemTags.SMALL_FLOWERS) && !boolean4) {
                    boolean4 = true;
                }
                else {
                    if (bcj9.getItem() != Items.BOWL || boolean7) {
                        return false;
                    }
                    boolean7 = true;
                }
            }
        }
        return boolean4 && boolean6 && boolean5 && boolean7;
    }
    
    public ItemStack assemble(final CraftingContainer ayw) {
        ItemStack bcj3 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < ayw.getContainerSize(); ++integer4) {
            final ItemStack bcj4 = ayw.getItem(integer4);
            if (!bcj4.isEmpty()) {
                if (bcj4.getItem().is(ItemTags.SMALL_FLOWERS)) {
                    bcj3 = bcj4;
                    break;
                }
            }
        }
        final ItemStack bcj5 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        if (bcj3.getItem() instanceof BlockItem && ((BlockItem)bcj3.getItem()).getBlock() instanceof FlowerBlock) {
            final FlowerBlock boy5 = (FlowerBlock)((BlockItem)bcj3.getItem()).getBlock();
            final MobEffect aig6 = boy5.getSuspiciousStewEffect();
            SuspiciousStewItem.saveMobEffect(bcj5, aig6, boy5.getEffectDuration());
        }
        return bcj5;
    }
    
    public boolean canCraftInDimensions(final int integer1, final int integer2) {
        return integer1 >= 2 && integer2 >= 2;
    }
    
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}
