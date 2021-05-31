package net.minecraft.world.inventory;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.recipebook.ServerPlaceSmeltingRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;

public abstract class AbstractFurnaceMenu extends RecipeBookMenu<Container> {
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    
    protected AbstractFurnaceMenu(final MenuType<?> azl, final RecipeType<? extends AbstractCookingRecipe> beu, final int integer, final Inventory awf) {
        this(azl, beu, integer, awf, new SimpleContainer(3), new SimpleContainerData(4));
    }
    
    protected AbstractFurnaceMenu(final MenuType<?> azl, final RecipeType<? extends AbstractCookingRecipe> beu, final int integer, final Inventory awf, final Container ahc, final ContainerData ayt) {
        super(azl, integer);
        this.recipeType = beu;
        AbstractContainerMenu.checkContainerSize(ahc, 3);
        AbstractContainerMenu.checkContainerDataCount(ayt, 4);
        this.container = ahc;
        this.data = ayt;
        this.level = awf.player.level;
        this.addSlot(new Slot(ahc, 0, 56, 17));
        this.addSlot(new FurnaceFuelSlot(this, ahc, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(awf.player, ahc, 2, 116, 35));
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(awf, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(awf, integer2, 8 + integer2 * 18, 142));
        }
        this.addDataSlots(ayt);
    }
    
    @Override
    public void fillCraftSlotsStackedContents(final StackedContents awi) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible)this.container).fillStackedContents(awi);
        }
    }
    
    @Override
    public void clearCraftingContent() {
        this.container.clearContent();
    }
    
    @Override
    public void handlePlacement(final boolean boolean1, final Recipe<?> ber, final ServerPlayer vl) {
        new ServerPlaceSmeltingRecipe<>(this).recipeClicked(vl, ber, boolean1);
    }
    
    @Override
    public boolean recipeMatches(final Recipe<? super Container> ber) {
        return ber.matches(this.container, this.level);
    }
    
    @Override
    public int getResultSlotIndex() {
        return 2;
    }
    
    @Override
    public int getGridWidth() {
        return 1;
    }
    
    @Override
    public int getGridHeight() {
        return 1;
    }
    
    @Override
    public int getSize() {
        return 3;
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.container.stillValid(awg);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 2) {
                if (!this.moveItemStackTo(bcj5, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (integer == 1 || integer == 0) {
                if (!this.moveItemStackTo(bcj5, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.canSmelt(bcj5)) {
                if (!this.moveItemStackTo(bcj5, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.isFuel(bcj5)) {
                if (!this.moveItemStackTo(bcj5, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 3 && integer < 30) {
                if (!this.moveItemStackTo(bcj5, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 30 && integer < 39 && !this.moveItemStackTo(bcj5, 3, 30, false)) {
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
    
    protected boolean canSmelt(final ItemStack bcj) {
        return this.level.getRecipeManager().<SimpleContainer, Recipe>getRecipeFor((RecipeType<Recipe>)this.recipeType, new SimpleContainer(new ItemStack[] { bcj }), this.level).isPresent();
    }
    
    protected boolean isFuel(final ItemStack bcj) {
        return AbstractFurnaceBlockEntity.isFuel(bcj);
    }
    
    public int getBurnProgress() {
        final int integer2 = this.data.get(2);
        final int integer3 = this.data.get(3);
        if (integer3 == 0 || integer2 == 0) {
            return 0;
        }
        return integer2 * 24 / integer3;
    }
    
    public int getLitProgress() {
        int integer2 = this.data.get(1);
        if (integer2 == 0) {
            integer2 = 200;
        }
        return this.data.get(0) * 13 / integer2;
    }
    
    public boolean isLit() {
        return this.data.get(0) > 0;
    }
}
