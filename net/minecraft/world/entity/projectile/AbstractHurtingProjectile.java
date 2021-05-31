package net.minecraft.world.entity.projectile;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public abstract class AbstractHurtingProjectile extends Entity {
    public LivingEntity owner;
    private int life;
    private int flightTime;
    public double xPower;
    public double yPower;
    public double zPower;
    
    protected AbstractHurtingProjectile(final EntityType<? extends AbstractHurtingProjectile> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public AbstractHurtingProjectile(final EntityType<? extends AbstractHurtingProjectile> ais, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final Level bhr) {
        this(ais, bhr);
        this.moveTo(double2, double3, double4, this.yRot, this.xRot);
        this.setPos(double2, double3, double4);
        final double double8 = Mth.sqrt(double5 * double5 + double6 * double6 + double7 * double7);
        this.xPower = double5 / double8 * 0.1;
        this.yPower = double6 / double8 * 0.1;
        this.zPower = double7 / double8 * 0.1;
    }
    
    public AbstractHurtingProjectile(final EntityType<? extends AbstractHurtingProjectile> ais, final LivingEntity aix, double double3, double double4, double double5, final Level bhr) {
        this(ais, bhr);
        this.owner = aix;
        this.moveTo(aix.x, aix.y, aix.z, aix.yRot, aix.xRot);
        this.setPos(this.x, this.y, this.z);
        this.setDeltaMovement(Vec3.ZERO);
        double3 += this.random.nextGaussian() * 0.4;
        double4 += this.random.nextGaussian() * 0.4;
        double5 += this.random.nextGaussian() * 0.4;
        final double double6 = Mth.sqrt(double3 * double3 + double4 * double4 + double5 * double5);
        this.xPower = double3 / double6 * 0.1;
        this.yPower = double4 / double6 * 0.1;
        this.zPower = double5 / double6 * 0.1;
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(double2)) {
            double2 = 4.0;
        }
        double2 *= 64.0;
        return double1 < double2 * double2;
    }
    
    @Override
    public void tick() {
        if (!this.level.isClientSide && ((this.owner != null && this.owner.removed) || !this.level.hasChunkAt(new BlockPos(this)))) {
            this.remove();
            return;
        }
        super.tick();
        if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
        }
        ++this.flightTime;
        final HitResult csf2 = ProjectileUtil.forwardsRaycast(this, true, this.flightTime >= 25, this.owner, ClipContext.Block.COLLIDER);
        if (csf2.getType() != HitResult.Type.MISS) {
            this.onHit(csf2);
        }
        final Vec3 csi3 = this.getDeltaMovement();
        this.x += csi3.x;
        this.y += csi3.y;
        this.z += csi3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2f);
        float float4 = this.getInertia();
        if (this.isInWater()) {
            for (int integer5 = 0; integer5 < 4; ++integer5) {
                final float float5 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, this.x - csi3.x * 0.25, this.y - csi3.y * 0.25, this.z - csi3.z * 0.25, csi3.x, csi3.y, csi3.z);
            }
            float4 = 0.8f;
        }
        this.setDeltaMovement(csi3.add(this.xPower, this.yPower, this.zPower).scale(float4));
        this.level.addParticle(this.getTrailParticle(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        this.setPos(this.x, this.y, this.z);
    }
    
    protected boolean shouldBurn() {
        return true;
    }
    
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }
    
    protected float getInertia() {
        return 0.95f;
    }
    
    protected abstract void onHit(final HitResult csf);
    
    public void addAdditionalSaveData(final CompoundTag id) {
        final Vec3 csi3 = this.getDeltaMovement();
        id.put("direction", (Tag)this.newDoubleList(csi3.x, csi3.y, csi3.z));
        id.put("power", (Tag)this.newDoubleList(this.xPower, this.yPower, this.zPower));
        id.putInt("life", this.life);
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        if (id.contains("power", 9)) {
            final ListTag ik3 = id.getList("power", 6);
            if (ik3.size() == 3) {
                this.xPower = ik3.getDouble(0);
                this.yPower = ik3.getDouble(1);
                this.zPower = ik3.getDouble(2);
            }
        }
        this.life = id.getInt("life");
        if (id.contains("direction", 9) && id.getList("direction", 6).size() == 3) {
            final ListTag ik3 = id.getList("direction", 6);
            this.setDeltaMovement(ik3.getDouble(0), ik3.getDouble(1), ik3.getDouble(2));
        }
        else {
            this.remove();
        }
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public float getPickRadius() {
        return 1.0f;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        this.markHurt();
        if (ahx.getEntity() != null) {
            final Vec3 csi4 = ahx.getEntity().getLookAngle();
            this.setDeltaMovement(csi4);
            this.xPower = csi4.x * 0.1;
            this.yPower = csi4.y * 0.1;
            this.zPower = csi4.z * 0.1;
            if (ahx.getEntity() instanceof LivingEntity) {
                this.owner = (LivingEntity)ahx.getEntity();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public float getBrightness() {
        return 1.0f;
    }
    
    @Override
    public int getLightColor() {
        return 15728880;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        final int integer2 = (this.owner == null) ? 0 : this.owner.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.x, this.y, this.z, this.xRot, this.yRot, this.getType(), integer2, new Vec3(this.xPower, this.yPower, this.zPower));
    }
}
