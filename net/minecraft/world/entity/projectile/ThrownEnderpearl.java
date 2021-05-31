package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.level.GameRules;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class ThrownEnderpearl extends ThrowableItemProjectile {
    private LivingEntity originalOwner;
    
    public ThrownEnderpearl(final EntityType<? extends ThrownEnderpearl> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public ThrownEnderpearl(final Level bhr, final LivingEntity aix) {
        super(EntityType.ENDER_PEARL, aix, bhr);
        this.originalOwner = aix;
    }
    
    public ThrownEnderpearl(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.ENDER_PEARL, double2, double3, double4, bhr);
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        final LivingEntity aix3 = this.getOwner();
        if (csf.getType() == HitResult.Type.ENTITY) {
            final Entity aio4 = ((EntityHitResult)csf).getEntity();
            if (aio4 == this.originalOwner) {
                return;
            }
            aio4.hurt(DamageSource.thrown(this, aix3), 0.0f);
        }
        if (csf.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew4 = ((BlockHitResult)csf).getBlockPos();
            final BlockEntity btw5 = this.level.getBlockEntity(ew4);
            if (btw5 instanceof TheEndGatewayBlockEntity) {
                final TheEndGatewayBlockEntity bux6 = (TheEndGatewayBlockEntity)btw5;
                if (aix3 != null) {
                    if (aix3 instanceof ServerPlayer) {
                        CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayer)aix3, this.level.getBlockState(ew4));
                    }
                    bux6.teleportEntity(aix3);
                    this.remove();
                    return;
                }
                bux6.teleportEntity(this);
                return;
            }
        }
        for (int integer4 = 0; integer4 < 32; ++integer4) {
            this.level.addParticle(ParticleTypes.PORTAL, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
        if (!this.level.isClientSide) {
            if (aix3 instanceof ServerPlayer) {
                final ServerPlayer vl4 = (ServerPlayer)aix3;
                if (vl4.connection.getConnection().isConnected() && vl4.level == this.level && !vl4.isSleeping()) {
                    if (this.random.nextFloat() < 0.05f && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        final Endermite auj5 = EntityType.ENDERMITE.create(this.level);
                        auj5.setPlayerSpawned(true);
                        auj5.moveTo(aix3.x, aix3.y, aix3.z, aix3.yRot, aix3.xRot);
                        this.level.addFreshEntity(auj5);
                    }
                    if (aix3.isPassenger()) {
                        aix3.stopRiding();
                    }
                    aix3.teleportTo(this.x, this.y, this.z);
                    aix3.fallDistance = 0.0f;
                    aix3.hurt(DamageSource.FALL, 5.0f);
                }
            }
            else if (aix3 != null) {
                aix3.teleportTo(this.x, this.y, this.z);
                aix3.fallDistance = 0.0f;
            }
            this.remove();
        }
    }
    
    @Override
    public void tick() {
        final LivingEntity aix2 = this.getOwner();
        if (aix2 != null && aix2 instanceof Player && !aix2.isAlive()) {
            this.remove();
        }
        else {
            super.tick();
        }
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType byn) {
        if (this.owner.dimension != byn) {
            this.owner = null;
        }
        return super.changeDimension(byn);
    }
}
