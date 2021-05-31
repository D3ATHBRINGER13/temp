package net.minecraft.world.entity.projectile;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class ThrownEgg extends ThrowableItemProjectile {
    public ThrownEgg(final EntityType<? extends ThrownEgg> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public ThrownEgg(final Level bhr, final LivingEntity aix) {
        super(EntityType.EGG, aix, bhr);
    }
    
    public ThrownEgg(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.EGG, double2, double3, double4, bhr);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 3) {
            final double double3 = 0.08;
            for (int integer5 = 0; integer5 < 8; ++integer5) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.x, this.y, this.z, (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (csf.getType() == HitResult.Type.ENTITY) {
            ((EntityHitResult)csf).getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0f);
        }
        if (!this.level.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int integer3 = 1;
                if (this.random.nextInt(32) == 0) {
                    integer3 = 4;
                }
                for (int integer4 = 0; integer4 < integer3; ++integer4) {
                    final Chicken arc5 = EntityType.CHICKEN.create(this.level);
                    arc5.setAge(-24000);
                    arc5.moveTo(this.x, this.y, this.z, this.yRot, 0.0f);
                    this.level.addFreshEntity(arc5);
                }
            }
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
    }
}
