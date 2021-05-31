package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

public class ResultSlot extends Slot {
    private final CraftingContainer craftSlots;
    private final Player player;
    private int removeCount;
    
    public ResultSlot(final Player awg, final CraftingContainer ayw, final Container ahc, final int integer4, final int integer5, final int integer6) {
        super(ahc, integer4, integer5, integer6);
        this.player = awg;
        this.craftSlots = ayw;
    }
    
    @Override
    public boolean mayPlace(final ItemStack bcj) {
        return false;
    }
    
    @Override
    public ItemStack remove(final int integer) {
        if (this.hasItem()) {
            this.removeCount += Math.min(integer, this.getItem().getCount());
        }
        return super.remove(integer);
    }
    
    @Override
    protected void onQuickCraft(final ItemStack bcj, final int integer) {
        this.removeCount += integer;
        this.checkTakeAchievements(bcj);
    }
    
    @Override
    protected void onSwapCraft(final int integer) {
        this.removeCount += integer;
    }
    
    @Override
    protected void checkTakeAchievements(final ItemStack bcj) {
        if (this.removeCount > 0) {
            bcj.onCraftedBy(this.player.level, this.player, this.removeCount);
        }
        if (this.container instanceof RecipeHolder) {
            ((RecipeHolder)this.container).awardAndReset(this.player);
        }
        this.removeCount = 0;
    }
    
    @Override
    public ItemStack onTake(final Player awg, final ItemStack bcj) {
        this.checkTakeAchievements(bcj);
        final NonNullList<ItemStack> fk4 = awg.level.getRecipeManager().<CraftingContainer, CraftingRecipe>getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, awg.level);
        for (int integer5 = 0; integer5 < fk4.size(); ++integer5) {
            ItemStack bcj2 = this.craftSlots.getItem(integer5);
            final ItemStack bcj3 = fk4.get(integer5);
            if (!bcj2.isEmpty()) {
                this.craftSlots.removeItem(integer5, 1);
                bcj2 = this.craftSlots.getItem(integer5);
            }
            if (!bcj3.isEmpty()) {
                if (bcj2.isEmpty()) {
                    this.craftSlots.setItem(integer5, bcj3);
                }
                else if (ItemStack.isSame(bcj2, bcj3) && ItemStack.tagMatches(bcj2, bcj3)) {
                    bcj3.grow(bcj2.getCount());
                    this.craftSlots.setItem(integer5, bcj3);
                }
                else if (!this.player.inventory.add(bcj3)) {
                    this.player.drop(bcj3, false);
                }
            }
        }
        return bcj;
    }
}
