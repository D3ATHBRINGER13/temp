package net.minecraft.world.entity.projectile;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class EvokerFangs extends Entity {
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks;
    private boolean clientSideAttackStarted;
    private LivingEntity owner;
    private UUID ownerUUID;
    
    public EvokerFangs(final EntityType<? extends EvokerFangs> ais, final Level bhr) {
        super(ais, bhr);
        this.lifeTicks = 22;
    }
    
    public EvokerFangs(final Level bhr, final double double2, final double double3, final double double4, final float float5, final int integer, final LivingEntity aix) {
        this(EntityType.EVOKER_FANGS, bhr);
        this.warmupDelayTicks = integer;
        this.setOwner(aix);
        this.yRot = float5 * 57.295776f;
        this.setPos(double2, double3, double4);
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    public void setOwner(@Nullable final LivingEntity aix) {
        this.owner = aix;
        this.ownerUUID = ((aix == null) ? null : aix.getUUID());
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            final Entity aio2 = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            if (aio2 instanceof LivingEntity) {
                this.owner = (LivingEntity)aio2;
            }
        }
        return this.owner;
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        this.warmupDelayTicks = id.getInt("Warmup");
        if (id.hasUUID("OwnerUUID")) {
            this.ownerUUID = id.getUUID("OwnerUUID");
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("Warmup", this.warmupDelayTicks);
        if (this.ownerUUID != null) {
            id.putUUID("OwnerUUID", this.ownerUUID);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for (int integer2 = 0; integer2 < 12; ++integer2) {
                        final double double3 = this.x + (this.random.nextDouble() * 2.0 - 1.0) * this.getBbWidth() * 0.5;
                        final double double4 = this.y + 0.05 + this.random.nextDouble();
                        final double double5 = this.z + (this.random.nextDouble() * 2.0 - 1.0) * this.getBbWidth() * 0.5;
                        final double double6 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        final double double7 = 0.3 + this.random.nextDouble() * 0.3;
                        final double double8 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.level.addParticle(ParticleTypes.CRIT, double3, double4 + 1.0, double5, double6, double7, double8);
                    }
                }
            }
        }
        else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                final List<LivingEntity> list2 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                for (final LivingEntity aix4 : list2) {
                    this.dealDamageTo(aix4);
                }
            }
            if (!this.sentSpikeEvent) {
                this.level.broadcastEntityEvent(this, (byte)4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.remove();
            }
        }
    }
    
    private void dealDamageTo(final LivingEntity aix) {
        final LivingEntity aix2 = this.getOwner();
        if (!aix.isAlive() || aix.isInvulnerable() || aix == aix2) {
            return;
        }
        if (aix2 == null) {
            aix.hurt(DamageSource.MAGIC, 6.0f);
        }
        else {
            if (aix2.isAlliedTo(aix)) {
                return;
            }
            aix.hurt(DamageSource.indirectMagic(this, aix2), 6.0f);
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        super.handleEntityEvent(byte1);
        if (byte1 == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f, false);
            }
        }
    }
    
    public float getAnimationProgress(final float float1) {
        if (!this.clientSideAttackStarted) {
            return 0.0f;
        }
        final int integer3 = this.lifeTicks - 2;
        if (integer3 <= 0) {
            return 1.0f;
        }
        return 1.0f - (integer3 - float1) / 20.0f;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
