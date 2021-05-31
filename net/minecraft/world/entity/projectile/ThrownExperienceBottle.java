package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class ThrownExperienceBottle extends ThrowableItemProjectile {
    public ThrownExperienceBottle(final EntityType<? extends ThrownExperienceBottle> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public ThrownExperienceBottle(final Level bhr, final LivingEntity aix) {
        super(EntityType.EXPERIENCE_BOTTLE, aix, bhr);
    }
    
    public ThrownExperienceBottle(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.EXPERIENCE_BOTTLE, double2, double3, double4, bhr);
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }
    
    @Override
    protected float getGravity() {
        return 0.07f;
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (!this.level.isClientSide) {
            this.level.levelEvent(2002, new BlockPos(this), PotionUtils.getColor(Potions.WATER));
            int integer3 = 3 + this.level.random.nextInt(5) + this.level.random.nextInt(5);
            while (integer3 > 0) {
                final int integer4 = ExperienceOrb.getExperienceValue(integer3);
                integer3 -= integer4;
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y, this.z, integer4));
            }
            this.remove();
        }
    }
}
