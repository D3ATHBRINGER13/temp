package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;

public class LecternScreen extends BookViewScreen implements MenuAccess<LecternMenu> {
    private final LecternMenu menu;
    private final ContainerListener listener;
    
    public LecternScreen(final LecternMenu azi, final Inventory awf, final Component jo) {
        this.listener = new ContainerListener() {
            public void refreshContainer(final AbstractContainerMenu ayk, final NonNullList<ItemStack> fk) {
                LecternScreen.this.bookChanged();
            }
            
            public void slotChanged(final AbstractContainerMenu ayk, final int integer, final ItemStack bcj) {
                LecternScreen.this.bookChanged();
            }
            
            public void setContainerData(final AbstractContainerMenu ayk, final int integer2, final int integer3) {
                if (integer2 == 0) {
                    LecternScreen.this.pageChanged();
                }
            }
        };
        this.menu = azi;
    }
    
    @Override
    public LecternMenu getMenu() {
        return this.menu;
    }
    
    @Override
    protected void init() {
        super.init();
        this.menu.addSlotListener(this.listener);
    }
    
    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }
    
    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this.listener);
    }
    
    @Override
    protected void createMenuControls() {
        if (this.minecraft.player.mayBuild()) {
            this.<Button>addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(null)));
            this.<Button>addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("lectern.take_book"), czi -> this.sendButtonClick(3)));
        }
        else {
            super.createMenuControls();
        }
    }
    
    @Override
    protected void pageBack() {
        this.sendButtonClick(1);
    }
    
    @Override
    protected void pageForward() {
        this.sendButtonClick(2);
    }
    
    @Override
    protected boolean forcePage(final int integer) {
        if (integer != this.menu.getPage()) {
            this.sendButtonClick(100 + integer);
            return true;
        }
        return false;
    }
    
    private void sendButtonClick(final int integer) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, integer);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void bookChanged() {
        final ItemStack bcj2 = this.menu.getBook();
        this.setBookAccess(BookAccess.fromItem(bcj2));
    }
    
    private void pageChanged() {
        this.setPage(this.menu.getPage());
    }
}
