package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.entity.player.Inventory;

public class SmokerMenu extends AbstractFurnaceMenu {
    public SmokerMenu(final int integer, final Inventory awf) {
        super(MenuType.SMOKER, RecipeType.SMOKING, integer, awf);
    }
    
    public SmokerMenu(final int integer, final Inventory awf, final Container ahc, final ContainerData ayt) {
        super(MenuType.SMOKER, RecipeType.SMOKING, integer, awf, ahc, ayt);
    }
}
