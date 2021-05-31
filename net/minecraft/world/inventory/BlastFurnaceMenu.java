package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.entity.player.Inventory;

public class BlastFurnaceMenu extends AbstractFurnaceMenu {
    public BlastFurnaceMenu(final int integer, final Inventory awf) {
        super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, integer, awf);
    }
    
    public BlastFurnaceMenu(final int integer, final Inventory awf, final Container ahc, final ContainerData ayt) {
        super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, integer, awf, ahc, ayt);
    }
}
