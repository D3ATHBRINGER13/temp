package net.minecraft.world.item;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BowlFoodItem extends Item {
    public BowlFoodItem(final Properties a) {
        super(a);
    }
    
    @Override
    public ItemStack finishUsingItem(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        super.finishUsingItem(bcj, bhr, aix);
        return new ItemStack(Items.BOWL);
    }
}
