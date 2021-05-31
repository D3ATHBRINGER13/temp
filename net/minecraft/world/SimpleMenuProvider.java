package net.minecraft.world;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.network.chat.Component;

public final class SimpleMenuProvider implements MenuProvider {
    private final Component title;
    private final MenuConstructor menuConstructor;
    
    public SimpleMenuProvider(final MenuConstructor azk, final Component jo) {
        this.menuConstructor = azk;
        this.title = jo;
    }
    
    public Component getDisplayName() {
        return this.title;
    }
    
    public AbstractContainerMenu createMenu(final int integer, final Inventory awf, final Player awg) {
        return this.menuConstructor.createMenu(integer, awf, awg);
    }
}
