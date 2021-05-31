package net.minecraft.recipebook;

import org.apache.logging.log4j.LogManager;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.inventory.Slot;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.Container;

public class ServerPlaceRecipe<C extends Container> implements PlaceRecipe<Integer> {
    protected static final Logger LOGGER;
    protected final StackedContents stackedContents;
    protected Inventory inventory;
    protected RecipeBookMenu<C> menu;
    
    public ServerPlaceRecipe(final RecipeBookMenu<C> azq) {
        this.stackedContents = new StackedContents();
        this.menu = azq;
    }
    
    public void recipeClicked(final ServerPlayer vl, @Nullable final Recipe<C> ber, final boolean boolean3) {
        if (ber == null || !vl.getRecipeBook().contains(ber)) {
            return;
        }
        this.inventory = vl.inventory;
        if (!this.testClearGrid() && !vl.isCreative()) {
            return;
        }
        this.stackedContents.clear();
        vl.inventory.fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        if (this.stackedContents.canCraft(ber, null)) {
            this.handleRecipeClicked(ber, boolean3);
        }
        else {
            this.clearGrid();
            vl.connection.send(new ClientboundPlaceGhostRecipePacket(vl.containerMenu.containerId, ber));
        }
        vl.inventory.setChanged();
    }
    
    protected void clearGrid() {
        for (int integer2 = 0; integer2 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++integer2) {
            if (integer2 == this.menu.getResultSlotIndex()) {
                if (this.menu instanceof CraftingMenu) {
                    continue;
                }
                if (this.menu instanceof InventoryMenu) {
                    continue;
                }
            }
            this.moveItemToInventory(integer2);
        }
        this.menu.clearCraftingContent();
    }
    
    protected void moveItemToInventory(final int integer) {
        final ItemStack bcj3 = this.menu.getSlot(integer).getItem();
        if (bcj3.isEmpty()) {
            return;
        }
        while (bcj3.getCount() > 0) {
            int integer2 = this.inventory.getSlotWithRemainingSpace(bcj3);
            if (integer2 == -1) {
                integer2 = this.inventory.getFreeSlot();
            }
            final ItemStack bcj4 = bcj3.copy();
            bcj4.setCount(1);
            if (!this.inventory.add(integer2, bcj4)) {
                ServerPlaceRecipe.LOGGER.error("Can't find any space for item in the inventory");
            }
            this.menu.getSlot(integer).remove(1);
        }
    }
    
    protected void handleRecipeClicked(final Recipe<C> ber, final boolean boolean2) {
        final boolean boolean3 = this.menu.recipeMatches(ber);
        final int integer5 = this.stackedContents.getBiggestCraftableStack(ber, null);
        if (boolean3) {
            for (int integer6 = 0; integer6 < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++integer6) {
                if (integer6 != this.menu.getResultSlotIndex()) {
                    final ItemStack bcj7 = this.menu.getSlot(integer6).getItem();
                    if (!bcj7.isEmpty() && Math.min(integer5, bcj7.getMaxStackSize()) < bcj7.getCount() + 1) {
                        return;
                    }
                }
            }
        }
        int integer6 = this.getStackSize(boolean2, integer5, boolean3);
        final IntList intList7 = (IntList)new IntArrayList();
        if (this.stackedContents.canCraft(ber, intList7, integer6)) {
            int integer7 = integer6;
            for (final int integer8 : intList7) {
                final int integer9 = StackedContents.fromStackingIndex(integer8).getMaxStackSize();
                if (integer9 < integer7) {
                    integer7 = integer9;
                }
            }
            integer6 = integer7;
            if (this.stackedContents.canCraft(ber, intList7, integer6)) {
                this.clearGrid();
                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), ber, (java.util.Iterator<Integer>)intList7.iterator(), integer6);
            }
        }
    }
    
    public void addItemToSlot(final Iterator<Integer> iterator, final int integer2, final int integer3, final int integer4, final int integer5) {
        final Slot azx7 = this.menu.getSlot(integer2);
        final ItemStack bcj8 = StackedContents.fromStackingIndex((int)iterator.next());
        if (!bcj8.isEmpty()) {
            for (int integer6 = 0; integer6 < integer3; ++integer6) {
                this.moveItemToGrid(azx7, bcj8);
            }
        }
    }
    
    protected int getStackSize(final boolean boolean1, final int integer, final boolean boolean3) {
        int integer2 = 1;
        if (boolean1) {
            integer2 = integer;
        }
        else if (boolean3) {
            integer2 = 64;
            for (int integer3 = 0; integer3 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++integer3) {
                if (integer3 != this.menu.getResultSlotIndex()) {
                    final ItemStack bcj7 = this.menu.getSlot(integer3).getItem();
                    if (!bcj7.isEmpty() && integer2 > bcj7.getCount()) {
                        integer2 = bcj7.getCount();
                    }
                }
            }
            if (integer2 < 64) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    protected void moveItemToGrid(final Slot azx, final ItemStack bcj) {
        final int integer4 = this.inventory.findSlotMatchingUnusedItem(bcj);
        if (integer4 == -1) {
            return;
        }
        final ItemStack bcj2 = this.inventory.getItem(integer4).copy();
        if (bcj2.isEmpty()) {
            return;
        }
        if (bcj2.getCount() > 1) {
            this.inventory.removeItem(integer4, 1);
        }
        else {
            this.inventory.removeItemNoUpdate(integer4);
        }
        bcj2.setCount(1);
        if (azx.getItem().isEmpty()) {
            azx.set(bcj2);
        }
        else {
            azx.getItem().grow(1);
        }
    }
    
    private boolean testClearGrid() {
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayList();
        final int integer3 = this.getAmountOfFreeSlotsInInventory();
        for (int integer4 = 0; integer4 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++integer4) {
            if (integer4 != this.menu.getResultSlotIndex()) {
                final ItemStack bcj5 = this.menu.getSlot(integer4).getItem().copy();
                if (!bcj5.isEmpty()) {
                    final int integer5 = this.inventory.getSlotWithRemainingSpace(bcj5);
                    if (integer5 == -1 && list2.size() <= integer3) {
                        for (final ItemStack bcj6 : list2) {
                            if (bcj6.sameItem(bcj5) && bcj6.getCount() != bcj6.getMaxStackSize() && bcj6.getCount() + bcj5.getCount() <= bcj6.getMaxStackSize()) {
                                bcj6.grow(bcj5.getCount());
                                bcj5.setCount(0);
                                break;
                            }
                        }
                        if (!bcj5.isEmpty()) {
                            if (list2.size() >= integer3) {
                                return false;
                            }
                            list2.add(bcj5);
                        }
                    }
                    else if (integer5 == -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private int getAmountOfFreeSlotsInInventory() {
        int integer2 = 0;
        for (final ItemStack bcj4 : this.inventory.items) {
            if (bcj4.isEmpty()) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
