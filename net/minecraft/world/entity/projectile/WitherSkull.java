package net.minecraft.world.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public class WitherSkull extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS;
    
    public WitherSkull(final EntityType<? extends WitherSkull> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public WitherSkull(final Level bhr, final LivingEntity aix, final double double3, final double double4, final double double5) {
        super(EntityType.WITHER_SKULL, aix, double3, double4, double5, bhr);
    }
    
    public WitherSkull(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(EntityType.WITHER_SKULL, double2, double3, double4, double5, double6, double7, bhr);
    }
    
    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.73f : super.getInertia();
    }
    
    @Override
    public boolean isOnFire() {
        return false;
    }
    
    @Override
    public float getBlockExplosionResistance(final Explosion bhk, final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final FluidState clk, final float float6) {
        if (this.isDangerous() && WitherBoss.canDestroy(bvt)) {
            return Math.min(0.8f, float6);
        }
        return float6;
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (!this.level.isClientSide) {
            if (csf.getType() == HitResult.Type.ENTITY) {
                final Entity aio3 = ((EntityHitResult)csf).getEntity();
                if (this.owner != null) {
                    if (aio3.hurt(DamageSource.mobAttack(this.owner), 8.0f)) {
                        if (aio3.isAlive()) {
                            this.doEnchantDamageEffects(this.owner, aio3);
                        }
                        else {
                            this.owner.heal(5.0f);
                        }
                    }
                }
                else {
                    aio3.hurt(DamageSource.MAGIC, 5.0f);
                }
                if (aio3 instanceof LivingEntity) {
                    int integer4 = 0;
                    if (this.level.getDifficulty() == Difficulty.NORMAL) {
                        integer4 = 10;
                    }
                    else if (this.level.getDifficulty() == Difficulty.HARD) {
                        integer4 = 40;
                    }
                    if (integer4 > 0) {
                        ((LivingEntity)aio3).addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * integer4, 1));
                    }
                }
            }
            final Explosion.BlockInteraction a3 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.x, this.y, this.z, 1.0f, false, a3);
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
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<Boolean>define(WitherSkull.DATA_DANGEROUS, false);
    }
    
    public boolean isDangerous() {
        return this.entityData.<Boolean>get(WitherSkull.DATA_DANGEROUS);
    }
    
    public void setDangerous(final boolean boolean1) {
        this.entityData.<Boolean>set(WitherSkull.DATA_DANGEROUS, boolean1);
    }
    
    @Override
    protected boolean shouldBurn() {
        return false;
    }
    
    static {
        DATA_DANGEROUS = SynchedEntityData.<Boolean>defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);
    }
}
