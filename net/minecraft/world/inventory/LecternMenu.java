package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;

public class LecternMenu extends AbstractContainerMenu {
    private final Container lectern;
    private final ContainerData lecternData;
    
    public LecternMenu(final int integer) {
        this(integer, new SimpleContainer(1), new SimpleContainerData(1));
    }
    
    public LecternMenu(final int integer, final Container ahc, final ContainerData ayt) {
        super(MenuType.LECTERN, integer);
        AbstractContainerMenu.checkContainerSize(ahc, 1);
        AbstractContainerMenu.checkContainerDataCount(ayt, 1);
        this.lectern = ahc;
        this.lecternData = ayt;
        this.addSlot(new Slot(ahc, 0, 0, 0) {
            @Override
            public void setChanged() {
                super.setChanged();
                LecternMenu.this.slotsChanged(this.container);
            }
        });
        this.addDataSlots(ayt);
    }
    
    @Override
    public boolean clickMenuButton(final Player awg, final int integer) {
        if (integer >= 100) {
            final int integer2 = integer - 100;
            this.setData(0, integer2);
            return true;
        }
        switch (integer) {
            case 2: {
                final int integer2 = this.lecternData.get(0);
                this.setData(0, integer2 + 1);
                return true;
            }
            case 1: {
                final int integer2 = this.lecternData.get(0);
                this.setData(0, integer2 - 1);
                return true;
            }
            case 3: {
                if (!awg.mayBuild()) {
                    return false;
                }
                final ItemStack bcj4 = this.lectern.removeItemNoUpdate(0);
                this.lectern.setChanged();
                if (!awg.inventory.add(bcj4)) {
                    awg.drop(bcj4, false);
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void setData(final int integer1, final int integer2) {
        super.setData(integer1, integer2);
        this.broadcastChanges();
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.lectern.stillValid(awg);
    }
    
    public ItemStack getBook() {
        return this.lectern.getItem(0);
    }
    
    public int getPage() {
        return this.lecternData.get(0);
    }
}
