package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class Snowball extends ThrowableItemProjectile {
    public Snowball(final EntityType<? extends Snowball> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public Snowball(final Level bhr, final LivingEntity aix) {
        super(EntityType.SNOWBALL, aix, bhr);
    }
    
    public Snowball(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.SNOWBALL, double2, double3, double4, bhr);
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }
    
    private ParticleOptions getParticle() {
        final ItemStack bcj2 = this.getItemRaw();
        return bcj2.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, bcj2);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 3) {
            final ParticleOptions gf3 = this.getParticle();
            for (int integer4 = 0; integer4 < 8; ++integer4) {
                this.level.addParticle(gf3, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (csf.getType() == HitResult.Type.ENTITY) {
            final Entity aio3 = ((EntityHitResult)csf).getEntity();
            final int integer4 = (aio3 instanceof Blaze) ? 3 : 0;
            aio3.hurt(DamageSource.thrown(this, this.getOwner()), (float)integer4);
        }
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }
}
