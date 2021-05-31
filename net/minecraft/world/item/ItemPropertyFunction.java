package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public interface ItemPropertyFunction {
    float call(final ItemStack bcj, @Nullable final Level bhr, @Nullable final LivingEntity aix);
}
