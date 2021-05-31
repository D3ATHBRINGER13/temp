package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.entity.player.Inventory;

public class FurnaceMenu extends AbstractFurnaceMenu {
    public FurnaceMenu(final int integer, final Inventory awf) {
        super(MenuType.FURNACE, RecipeType.SMELTING, integer, awf);
    }
    
    public FurnaceMenu(final int integer, final Inventory awf, final Container ahc, final ContainerData ayt) {
        super(MenuType.FURNACE, RecipeType.SMELTING, integer, awf, ahc, ayt);
    }
}
