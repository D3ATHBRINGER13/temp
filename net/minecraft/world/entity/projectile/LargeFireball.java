package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class LargeFireball extends Fireball {
    public int explosionPower;
    
    public LargeFireball(final EntityType<? extends LargeFireball> ais, final Level bhr) {
        super(ais, bhr);
        this.explosionPower = 1;
    }
    
    public LargeFireball(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(EntityType.FIREBALL, double2, double3, double4, double5, double6, double7, bhr);
        this.explosionPower = 1;
    }
    
    public LargeFireball(final Level bhr, final LivingEntity aix, final double double3, final double double4, final double double5) {
        super(EntityType.FIREBALL, aix, double3, double4, double5, bhr);
        this.explosionPower = 1;
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (!this.level.isClientSide) {
            if (csf.getType() == HitResult.Type.ENTITY) {
                final Entity aio3 = ((EntityHitResult)csf).getEntity();
                aio3.hurt(DamageSource.fireball(this, this.owner), 6.0f);
                this.doEnchantDamageEffects(this.owner, aio3);
            }
            final boolean boolean3 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            this.level.explode(null, this.x, this.y, this.z, (float)this.explosionPower, boolean3, boolean3 ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
            this.remove();
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("ExplosionPower", this.explosionPower);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("ExplosionPower", 99)) {
            this.explosionPower = id.getInt("ExplosionPower");
        }
    }
}
