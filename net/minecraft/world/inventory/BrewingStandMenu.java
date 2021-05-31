package net.minecraft.world.inventory;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class BrewingStandMenu extends AbstractContainerMenu {
    private final Container brewingStand;
    private final ContainerData brewingStandData;
    private final Slot ingredientSlot;
    
    public BrewingStandMenu(final int integer, final Inventory awf) {
        this(integer, awf, new SimpleContainer(5), new SimpleContainerData(2));
    }
    
    public BrewingStandMenu(final int integer, final Inventory awf, final Container ahc, final ContainerData ayt) {
        super(MenuType.BREWING_STAND, integer);
        AbstractContainerMenu.checkContainerSize(ahc, 5);
        AbstractContainerMenu.checkContainerDataCount(ayt, 2);
        this.brewingStand = ahc;
        this.brewingStandData = ayt;
        this.addSlot(new PotionSlot(ahc, 0, 56, 51));
        this.addSlot(new PotionSlot(ahc, 1, 79, 58));
        this.addSlot(new PotionSlot(ahc, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new IngredientsSlot(ahc, 3, 79, 17));
        this.addSlot(new FuelSlot(ahc, 4, 17, 17));
        this.addDataSlots(ayt);
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(awf, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(awf, integer2, 8 + integer2 * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.brewingStand.stillValid(awg);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if ((integer >= 0 && integer <= 2) || integer == 3 || integer == 4) {
                if (!this.moveItemStackTo(bcj5, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (this.ingredientSlot.mayPlace(bcj5)) {
                if (!this.moveItemStackTo(bcj5, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (PotionSlot.mayPlaceItem(bcj4) && bcj4.getCount() == 1) {
                if (!this.moveItemStackTo(bcj5, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (FuelSlot.mayPlaceItem(bcj4)) {
                if (!this.moveItemStackTo(bcj5, 4, 5, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 5 && integer < 32) {
                if (!this.moveItemStackTo(bcj5, 32, 41, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 32 && integer < 41) {
                if (!this.moveItemStackTo(bcj5, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 5, 41, false)) {
                return ItemStack.EMPTY;
            }
            if (bcj5.isEmpty()) {
                azx5.set(ItemStack.EMPTY);
            }
            else {
                azx5.setChanged();
            }
            if (bcj5.getCount() == bcj4.getCount()) {
                return ItemStack.EMPTY;
            }
            azx5.onTake(awg, bcj5);
        }
        return bcj4;
    }
    
    public int getFuel() {
        return this.brewingStandData.get(1);
    }
    
    public int getBrewingTicks() {
        return this.brewingStandData.get(0);
    }
    
    static class PotionSlot extends Slot {
        public PotionSlot(final Container ahc, final int integer2, final int integer3, final int integer4) {
            super(ahc, integer2, integer3, integer4);
        }
        
        @Override
        public boolean mayPlace(final ItemStack bcj) {
            return mayPlaceItem(bcj);
        }
        
        @Override
        public int getMaxStackSize() {
            return 1;
        }
        
        @Override
        public ItemStack onTake(final Player awg, final ItemStack bcj) {
            final Potion bdy4 = PotionUtils.getPotion(bcj);
            if (awg instanceof ServerPlayer) {
                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayer)awg, bdy4);
            }
            super.onTake(awg, bcj);
            return bcj;
        }
        
        public static boolean mayPlaceItem(final ItemStack bcj) {
            final Item bce2 = bcj.getItem();
            return bce2 == Items.POTION || bce2 == Items.SPLASH_POTION || bce2 == Items.LINGERING_POTION || bce2 == Items.GLASS_BOTTLE;
        }
    }
    
    static class IngredientsSlot extends Slot {
        public IngredientsSlot(final Container ahc, final int integer2, final int integer3, final int integer4) {
            super(ahc, integer2, integer3, integer4);
        }
        
        @Override
        public boolean mayPlace(final ItemStack bcj) {
            return PotionBrewing.isIngredient(bcj);
        }
        
        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
    
    static class FuelSlot extends Slot {
        public FuelSlot(final Container ahc, final int integer2, final int integer3, final int integer4) {
            super(ahc, integer2, integer3, integer4);
        }
        
        @Override
        public boolean mayPlace(final ItemStack bcj) {
            return mayPlaceItem(bcj);
        }
        
        public static boolean mayPlaceItem(final ItemStack bcj) {
            return bcj.getItem() == Items.BLAZE_POWDER;
        }
        
        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
}
