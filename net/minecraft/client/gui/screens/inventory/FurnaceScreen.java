package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.FurnaceMenu;

public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceMenu> {
    private static final ResourceLocation TEXTURE;
    
    public FurnaceScreen(final FurnaceMenu azc, final Inventory awf, final Component jo) {
        super(azc, new SmeltingRecipeBookComponent(), awf, jo, FurnaceScreen.TEXTURE);
    }
    
    static {
        TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
    }
}
