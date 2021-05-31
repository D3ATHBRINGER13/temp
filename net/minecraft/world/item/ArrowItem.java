package net.minecraft.world.item;

import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ArrowItem extends Item {
    public ArrowItem(final Properties a) {
        super(a);
    }
    
    public AbstractArrow createArrow(final Level bhr, final ItemStack bcj, final LivingEntity aix) {
        final Arrow awm5 = new Arrow(bhr, aix);
        awm5.setEffectsFromItem(bcj);
        return awm5;
    }
}
