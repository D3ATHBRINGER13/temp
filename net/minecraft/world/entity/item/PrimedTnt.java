package net.minecraft.world.entity.item;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Explosion;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class PrimedTnt extends Entity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID;
    @Nullable
    private LivingEntity owner;
    private int life;
    
    public PrimedTnt(final EntityType<? extends PrimedTnt> ais, final Level bhr) {
        super(ais, bhr);
        this.life = 80;
        this.blocksBuilding = true;
    }
    
    public PrimedTnt(final Level bhr, final double double2, final double double3, final double double4, @Nullable final LivingEntity aix) {
        this(EntityType.TNT, bhr);
        this.setPos(double2, double3, double4);
        final double double5 = bhr.random.nextDouble() * 6.2831854820251465;
        this.setDeltaMovement(-Math.sin(double5) * 0.02, 0.20000000298023224, -Math.cos(double5) * 0.02);
        this.setFuse(80);
        this.xo = double2;
        this.yo = double3;
        this.zo = double4;
        this.owner = aix;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<Integer>define(PrimedTnt.DATA_FUSE_ID, 80);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }
        --this.life;
        if (this.life <= 0) {
            this.remove();
            if (!this.level.isClientSide) {
                this.explode();
            }
        }
        else {
            this.updateInWaterState();
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    private void explode() {
        final float float2 = 4.0f;
        this.level.explode(this, this.x, this.y + this.getBbHeight() / 16.0f, this.z, 4.0f, Explosion.BlockInteraction.BREAK);
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        id.putShort("Fuse", (short)this.getLife());
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        this.setFuse(id.getShort("Fuse"));
    }
    
    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }
    
    @Override
    protected float getEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.0f;
    }
    
    public void setFuse(final int integer) {
        this.entityData.<Integer>set(PrimedTnt.DATA_FUSE_ID, integer);
        this.life = integer;
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (PrimedTnt.DATA_FUSE_ID.equals(qk)) {
            this.life = this.getFuse();
        }
    }
    
    public int getFuse() {
        return this.entityData.<Integer>get(PrimedTnt.DATA_FUSE_ID);
    }
    
    public int getLife() {
        return this.life;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    static {
        DATA_FUSE_ID = SynchedEntityData.<Integer>defineId(PrimedTnt.class, EntityDataSerializers.INT);
    }
}
