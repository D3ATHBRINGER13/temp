package net.minecraft.world.inventory;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

public class FurnaceResultSlot extends Slot {
    private final Player player;
    private int removeCount;
    
    public FurnaceResultSlot(final Player awg, final Container ahc, final int integer3, final int integer4, final int integer5) {
        super(ahc, integer3, integer4, integer5);
        this.player = awg;
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
    public ItemStack onTake(final Player awg, final ItemStack bcj) {
        this.checkTakeAchievements(bcj);
        super.onTake(awg, bcj);
        return bcj;
    }
    
    @Override
    protected void onQuickCraft(final ItemStack bcj, final int integer) {
        this.removeCount += integer;
        this.checkTakeAchievements(bcj);
    }
    
    @Override
    protected void checkTakeAchievements(final ItemStack bcj) {
        bcj.onCraftedBy(this.player.level, this.player, this.removeCount);
        if (!this.player.level.isClientSide && this.container instanceof AbstractFurnaceBlockEntity) {
            ((AbstractFurnaceBlockEntity)this.container).awardResetAndExperience(this.player);
        }
        this.removeCount = 0;
    }
}
