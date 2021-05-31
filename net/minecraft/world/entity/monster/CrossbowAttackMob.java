package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;

public interface CrossbowAttackMob {
    void setChargingCrossbow(final boolean boolean1);
    
    void shootProjectile(final LivingEntity aix, final ItemStack bcj, final Projectile awv, final float float4);
    
    @Nullable
    LivingEntity getTarget();
}
