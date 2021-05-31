package net.minecraft.world.inventory;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.entity.player.StackedContents;
import javax.annotation.Nullable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;

public class InventoryMenu extends RecipeBookMenu<CraftingContainer> {
    private static final String[] TEXTURE_EMPTY_SLOTS;
    private static final EquipmentSlot[] SLOT_IDS;
    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots;
    public final boolean active;
    private final Player owner;
    
    public InventoryMenu(final Inventory awf, final boolean boolean2, final Player awg) {
        super(null, 0);
        this.craftSlots = new CraftingContainer(this, 2, 2);
        this.resultSlots = new ResultContainer();
        this.active = boolean2;
        this.owner = awg;
        this.addSlot(new ResultSlot(awf.player, this.craftSlots, this.resultSlots, 0, 154, 28));
        for (int integer5 = 0; integer5 < 2; ++integer5) {
            for (int integer6 = 0; integer6 < 2; ++integer6) {
                this.addSlot(new Slot(this.craftSlots, integer6 + integer5 * 2, 98 + integer6 * 18, 18 + integer5 * 18));
            }
        }
        for (int integer5 = 0; integer5 < 4; ++integer5) {
            final EquipmentSlot ait6 = InventoryMenu.SLOT_IDS[integer5];
            this.addSlot(new Slot(awf, 39 - integer5, 8, 8 + integer5 * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }
                
                @Override
                public boolean mayPlace(final ItemStack bcj) {
                    return ait6 == Mob.getEquipmentSlotForItem(bcj);
                }
                
                @Override
                public boolean mayPickup(final Player awg) {
                    final ItemStack bcj3 = this.getItem();
                    return (bcj3.isEmpty() || awg.isCreative() || !EnchantmentHelper.hasBindingCurse(bcj3)) && super.mayPickup(awg);
                }
                
                @Nullable
                @Override
                public String getNoItemIcon() {
                    return InventoryMenu.TEXTURE_EMPTY_SLOTS[ait6.getIndex()];
                }
            });
        }
        for (int integer5 = 0; integer5 < 3; ++integer5) {
            for (int integer6 = 0; integer6 < 9; ++integer6) {
                this.addSlot(new Slot(awf, integer6 + (integer5 + 1) * 9, 8 + integer6 * 18, 84 + integer5 * 18));
            }
        }
        for (int integer5 = 0; integer5 < 9; ++integer5) {
            this.addSlot(new Slot(awf, integer5, 8 + integer5 * 18, 142));
        }
        this.addSlot(new Slot(awf, 40, 77, 62) {
            @Nullable
            @Override
            public String getNoItemIcon() {
                return "item/empty_armor_slot_shield";
            }
        });
    }
    
    @Override
    public void fillCraftSlotsStackedContents(final StackedContents awi) {
        this.craftSlots.fillStackedContents(awi);
    }
    
    @Override
    public void clearCraftingContent() {
        this.resultSlots.clearContent();
        this.craftSlots.clearContent();
    }
    
    @Override
    public boolean recipeMatches(final Recipe<? super CraftingContainer> ber) {
        return ber.matches(this.craftSlots, this.owner.level);
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        CraftingMenu.slotChangedCraftingGrid(this.containerId, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.resultSlots.clearContent();
        if (awg.level.isClientSide) {
            return;
        }
        this.clearContainer(awg, awg.level, this.craftSlots);
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return true;
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            final EquipmentSlot ait7 = Mob.getEquipmentSlotForItem(bcj4);
            if (integer == 0) {
                if (!this.moveItemStackTo(bcj5, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (integer >= 1 && integer < 5) {
                if (!this.moveItemStackTo(bcj5, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 5 && integer < 9) {
                if (!this.moveItemStackTo(bcj5, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (ait7.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - ait7.getIndex())).hasItem()) {
                final int integer2 = 8 - ait7.getIndex();
                if (!this.moveItemStackTo(bcj5, integer2, integer2 + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (ait7 == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasItem()) {
                if (!this.moveItemStackTo(bcj5, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 9 && integer < 36) {
                if (!this.moveItemStackTo(bcj5, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 36 && integer < 45) {
                if (!this.moveItemStackTo(bcj5, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(bcj5, 9, 45, false)) {
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
        return 5;
    }
    
    static {
        TEXTURE_EMPTY_SLOTS = new String[] { "item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet" };
        SLOT_IDS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    }
}
