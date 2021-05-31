package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class SmallFireball extends Fireball {
    public SmallFireball(final EntityType<? extends SmallFireball> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public SmallFireball(final Level bhr, final LivingEntity aix, final double double3, final double double4, final double double5) {
        super(EntityType.SMALL_FIREBALL, aix, double3, double4, double5, bhr);
    }
    
    public SmallFireball(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(EntityType.SMALL_FIREBALL, double2, double3, double4, double5, double6, double7, bhr);
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (!this.level.isClientSide) {
            if (csf.getType() == HitResult.Type.ENTITY) {
                final Entity aio3 = ((EntityHitResult)csf).getEntity();
                if (!aio3.fireImmune()) {
                    final int integer4 = aio3.getRemainingFireTicks();
                    aio3.setSecondsOnFire(5);
                    final boolean boolean5 = aio3.hurt(DamageSource.fireball(this, this.owner), 5.0f);
                    if (boolean5) {
                        this.doEnchantDamageEffects(this.owner, aio3);
                    }
                    else {
                        aio3.setRemainingFireTicks(integer4);
                    }
                }
            }
            else if (this.owner == null || !(this.owner instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                final BlockHitResult csd3 = (BlockHitResult)csf;
                final BlockPos ew4 = csd3.getBlockPos().relative(csd3.getDirection());
                if (this.level.isEmptyBlock(ew4)) {
                    this.level.setBlockAndUpdate(ew4, Blocks.FIRE.defaultBlockState());
                }
            }
            this.remove();
        }
    }
    
    @Override
    public boolean isPickable() {
        return false;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        return false;
    }
}
