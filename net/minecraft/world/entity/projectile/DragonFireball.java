package net.minecraft.world.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class DragonFireball extends AbstractHurtingProjectile {
    public DragonFireball(final EntityType<? extends DragonFireball> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public DragonFireball(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(EntityType.DRAGON_FIREBALL, double2, double3, double4, double5, double6, double7, bhr);
    }
    
    public DragonFireball(final Level bhr, final LivingEntity aix, final double double3, final double double4, final double double5) {
        super(EntityType.DRAGON_FIREBALL, aix, double3, double4, double5, bhr);
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (csf.getType() == HitResult.Type.ENTITY && ((EntityHitResult)csf).getEntity().is(this.owner)) {
            return;
        }
        if (!this.level.isClientSide) {
            final List<LivingEntity> list3 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
            final AreaEffectCloud ain4 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
            ain4.setOwner(this.owner);
            ain4.setParticle(ParticleTypes.DRAGON_BREATH);
            ain4.setRadius(3.0f);
            ain4.setDuration(600);
            ain4.setRadiusPerTick((7.0f - ain4.getRadius()) / ain4.getDuration());
            ain4.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!list3.isEmpty()) {
                for (final LivingEntity aix6 : list3) {
                    final double double7 = this.distanceToSqr(aix6);
                    if (double7 < 16.0) {
                        ain4.setPos(aix6.x, aix6.y, aix6.z);
                        break;
                    }
                }
            }
            this.level.levelEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
            this.level.addFreshEntity(ain4);
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
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }
    
    @Override
    protected boolean shouldBurn() {
        return false;
    }
}
