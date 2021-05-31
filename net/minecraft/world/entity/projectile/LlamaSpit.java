package net.minecraft.world.entity.projectile;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.material.Material;
import java.util.function.Predicate;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.Entity;

public class LlamaSpit extends Entity implements Projectile {
    public Llama owner;
    private CompoundTag ownerTag;
    
    public LlamaSpit(final EntityType<? extends LlamaSpit> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public LlamaSpit(final Level bhr, final Llama ase) {
        this(EntityType.LLAMA_SPIT, bhr);
        this.owner = ase;
        this.setPos(ase.x - (ase.getBbWidth() + 1.0f) * 0.5 * Mth.sin(ase.yBodyRot * 0.017453292f), ase.y + ase.getEyeHeight() - 0.10000000149011612, ase.z + (ase.getBbWidth() + 1.0f) * 0.5 * Mth.cos(ase.yBodyRot * 0.017453292f));
    }
    
    public LlamaSpit(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        this(EntityType.LLAMA_SPIT, bhr);
        this.setPos(double2, double3, double4);
        for (int integer15 = 0; integer15 < 7; ++integer15) {
            final double double8 = 0.4 + 0.1 * integer15;
            bhr.addParticle(ParticleTypes.SPIT, double2, double3, double4, double5 * double8, double6, double7 * double8);
        }
        this.setDeltaMovement(double5, double6, double7);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.ownerTag != null) {
            this.restoreOwnerFromSave();
        }
        final Vec3 csi2 = this.getDeltaMovement();
        final HitResult csf3 = ProjectileUtil.getHitResult(this, this.getBoundingBox().expandTowards(csi2).inflate(1.0), (Predicate<Entity>)(aio -> !aio.isSpectator() && aio != this.owner), ClipContext.Block.OUTLINE, true);
        if (csf3 != null) {
            this.onHit(csf3);
        }
        this.x += csi2.x;
        this.y += csi2.y;
        this.z += csi2.z;
        final float float4 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi2));
        this.yRot = (float)(Mth.atan2(csi2.x, csi2.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi2.y, float4) * 57.2957763671875);
        while (this.xRot - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.xRot - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        while (this.yRot - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.yRot - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        this.xRot = Mth.lerp(0.2f, this.xRotO, this.xRot);
        this.yRot = Mth.lerp(0.2f, this.yRotO, this.yRot);
        final float float5 = 0.99f;
        final float float6 = 0.06f;
        if (!this.level.containsMaterial(this.getBoundingBox(), Material.AIR)) {
            this.remove();
            return;
        }
        if (this.isInWaterOrBubble()) {
            this.remove();
            return;
        }
        this.setDeltaMovement(csi2.scale(0.9900000095367432));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05999999865889549, 0.0));
        }
        this.setPos(this.x, this.y, this.z);
    }
    
    @Override
    public void lerpMotion(final double double1, final double double2, final double double3) {
        this.setDeltaMovement(double1, double2, double3);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float float8 = Mth.sqrt(double1 * double1 + double3 * double3);
            this.xRot = (float)(Mth.atan2(double2, float8) * 57.2957763671875);
            this.yRot = (float)(Mth.atan2(double1, double3) * 57.2957763671875);
            this.xRotO = this.xRot;
            this.yRotO = this.yRot;
            this.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
        }
    }
    
    @Override
    public void shoot(final double double1, final double double2, final double double3, final float float4, final float float5) {
        final Vec3 csi10 = new Vec3(double1, double2, double3).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * float5, this.random.nextGaussian() * 0.007499999832361937 * float5, this.random.nextGaussian() * 0.007499999832361937 * float5).scale(float4);
        this.setDeltaMovement(csi10);
        final float float6 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi10));
        this.yRot = (float)(Mth.atan2(csi10.x, double3) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi10.y, float6) * 57.2957763671875);
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }
    
    public void onHit(final HitResult csf) {
        final HitResult.Type a3 = csf.getType();
        if (a3 == HitResult.Type.ENTITY && this.owner != null) {
            ((EntityHitResult)csf).getEntity().hurt(DamageSource.indirectMobAttack(this, this.owner).setProjectile(), 1.0f);
        }
        else if (a3 == HitResult.Type.BLOCK && !this.level.isClientSide) {
            this.remove();
        }
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        if (id.contains("Owner", 10)) {
            this.ownerTag = id.getCompound("Owner");
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        if (this.owner != null) {
            final CompoundTag id2 = new CompoundTag();
            final UUID uUID4 = this.owner.getUUID();
            id2.putUUID("OwnerUUID", uUID4);
            id.put("Owner", (Tag)id2);
        }
    }
    
    private void restoreOwnerFromSave() {
        if (this.ownerTag != null && this.ownerTag.hasUUID("OwnerUUID")) {
            final UUID uUID2 = this.ownerTag.getUUID("OwnerUUID");
            final List<Llama> list3 = this.level.<Llama>getEntitiesOfClass((java.lang.Class<? extends Llama>)Llama.class, this.getBoundingBox().inflate(15.0));
            for (final Llama ase5 : list3) {
                if (ase5.getUUID().equals(uUID2)) {
                    this.owner = ase5;
                    break;
                }
            }
        }
        this.ownerTag = null;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
