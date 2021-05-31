package net.minecraft.world.inventory;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.core.BlockPos;
import java.util.function.BiConsumer;
import java.util.Optional;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class CraftingMenu extends RecipeBookMenu<CraftingContainer> {
    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots;
    private final ContainerLevelAccess access;
    private final Player player;
    
    public CraftingMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public CraftingMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.CRAFTING, integer);
        this.craftSlots = new CraftingContainer(this, 3, 3);
        this.resultSlots = new ResultContainer();
        this.access = ayu;
        this.player = awf.player;
        this.addSlot(new ResultSlot(awf.player, this.craftSlots, this.resultSlots, 0, 124, 35));
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 3; ++integer3) {
                this.addSlot(new Slot(this.craftSlots, integer3 + integer2 * 3, 30 + integer3 * 18, 17 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(awf, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(awf, integer2, 8 + integer2 * 18, 142));
        }
    }
    
    protected static void slotChangedCraftingGrid(final int integer, final Level bhr, final Player awg, final CraftingContainer ayw, final ResultContainer azs) {
        if (bhr.isClientSide) {
            return;
        }
        final ServerPlayer vl6 = (ServerPlayer)awg;
        ItemStack bcj7 = ItemStack.EMPTY;
        final Optional<CraftingRecipe> optional8 = bhr.getServer().getRecipeManager().<CraftingContainer, CraftingRecipe>getRecipeFor(RecipeType.CRAFTING, ayw, bhr);
        if (optional8.isPresent()) {
            final CraftingRecipe bej9 = (CraftingRecipe)optional8.get();
            if (azs.setRecipeUsed(bhr, vl6, bej9)) {
                bcj7 = bej9.assemble(ayw);
            }
        }
        azs.setItem(0, bcj7);
        vl6.connection.send(new ClientboundContainerSetSlotPacket(integer, 0, bcj7));
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> slotChangedCraftingGrid(this.containerId, bhr, this.player, this.craftSlots, this.resultSlots)));
    }
    
    @Override
    public void fillCraftSlotsStackedContents(final StackedContents awi) {
        this.craftSlots.fillStackedContents(awi);
    }
    
    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }
    
    @Override
    public boolean recipeMatches(final Recipe<? super CraftingContainer> ber) {
        return ber.matches(this.craftSlots, this.player.level);
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, bhr, this.craftSlots)));
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.CRAFTING_TABLE);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 0) {
                this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> bcj5.getItem().onCraftedBy(bcj5, bhr, awg)));
                if (!this.moveItemStackTo(bcj5, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (integer >= 10 && integer < 37) {
                if (!this.moveItemStackTo(bcj5, 37, 46, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 37 && integer < 46) {
                if (!this.moveItemStackTo(bcj5, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 10, 46, false)) {
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
            final ItemStack bcj6 = azx5.onTake(awg, bcj5);
            if (integer == 0) {
                awg.drop(bcj6, false);
            }
        }
        return bcj4;
    }
    
    @Override
    public boolean canTakeItemForPickAll(final ItemStack bcj, final Slot azx) {
        return azx.container != this.resultSlots && super.canTakeItemForPickAll(bcj, azx);
    }
    
    @Override
    public int getResultSlotIndex() {
        return 0;
    }
    
    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }
    
    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }
    
    @Override
    public int getSize() {
        return 10;
    }
}
