package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import java.util.Iterator;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;

public class MapExtendingRecipe extends ShapedRecipe {
    public MapExtendingRecipe(final ResourceLocation qv) {
        super(qv, "", 3, 3, NonNullList.<Ingredient>of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
    }
    
    @Override
    public boolean matches(final CraftingContainer ayw, final Level bhr) {
        if (!super.matches(ayw, bhr)) {
            return false;
        }
        ItemStack bcj4 = ItemStack.EMPTY;
        for (int integer5 = 0; integer5 < ayw.getContainerSize() && bcj4.isEmpty(); ++integer5) {
            final ItemStack bcj5 = ayw.getItem(integer5);
            if (bcj5.getItem() == Items.FILLED_MAP) {
                bcj4 = bcj5;
            }
        }
        if (bcj4.isEmpty()) {
            return false;
        }
        final MapItemSavedData coh5 = MapItem.getOrCreateSavedData(bcj4, bhr);
        return coh5 != null && !this.isExplorationMap(coh5) && coh5.scale < 4;
    }
    
    private boolean isExplorationMap(final MapItemSavedData coh) {
        if (coh.decorations != null) {
            for (final MapDecoration coe4 : coh.decorations.values()) {
                if (coe4.getType() == MapDecoration.Type.MANSION || coe4.getType() == MapDecoration.Type.MONUMENT) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public ItemStack assemble(final CraftingContainer ayw) {
        ItemStack bcj3 = ItemStack.EMPTY;
        for (int integer4 = 0; integer4 < ayw.getContainerSize() && bcj3.isEmpty(); ++integer4) {
            final ItemStack bcj4 = ayw.getItem(integer4);
            if (bcj4.getItem() == Items.FILLED_MAP) {
                bcj3 = bcj4;
            }
        }
        bcj3 = bcj3.copy();
        bcj3.setCount(1);
        bcj3.getOrCreateTag().putInt("map_scale_direction", 1);
        return bcj3;
    }
    
    public boolean isSpecial() {
        return true;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}
