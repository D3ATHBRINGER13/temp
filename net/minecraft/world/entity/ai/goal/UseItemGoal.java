package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Mob;

public class UseItemGoal<T extends Mob> extends Goal {
    private final T mob;
    private final ItemStack item;
    private final Predicate<? super T> canUseSelector;
    private final SoundEvent finishUsingSound;
    
    public UseItemGoal(final T aiy, final ItemStack bcj, @Nullable final SoundEvent yo, final Predicate<? super T> predicate) {
        this.mob = aiy;
        this.item = bcj;
        this.finishUsingSound = yo;
        this.canUseSelector = predicate;
    }
    
    @Override
    public boolean canUse() {
        return this.canUseSelector.test(this.mob);
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.mob.isUsingItem();
    }
    
    @Override
    public void start() {
        this.mob.setItemSlot(EquipmentSlot.MAINHAND, this.item.copy());
        this.mob.startUsingItem(InteractionHand.MAIN_HAND);
    }
    
    @Override
    public void stop() {
        this.mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        if (this.finishUsingSound != null) {
            this.mob.playSound(this.finishUsingSound, 1.0f, this.mob.getRandom().nextFloat() * 0.2f + 0.9f);
        }
    }
}
