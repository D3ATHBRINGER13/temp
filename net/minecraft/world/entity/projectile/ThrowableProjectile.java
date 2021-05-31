package net.minecraft.world.entity.projectile;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import java.util.function.Predicate;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public abstract class ThrowableProjectile extends Entity implements Projectile {
    private int xBlock;
    private int yBlock;
    private int zBlock;
    protected boolean inGround;
    public int shakeTime;
    protected LivingEntity owner;
    private UUID ownerId;
    private Entity entityToIgnore;
    private int timeToIgnore;
    
    protected ThrowableProjectile(final EntityType<? extends ThrowableProjectile> ais, final Level bhr) {
        super(ais, bhr);
        this.xBlock = -1;
        this.yBlock = -1;
        this.zBlock = -1;
    }
    
    protected ThrowableProjectile(final EntityType<? extends ThrowableProjectile> ais, final double double2, final double double3, final double double4, final Level bhr) {
        this(ais, bhr);
        this.setPos(double2, double3, double4);
    }
    
    protected ThrowableProjectile(final EntityType<? extends ThrowableProjectile> ais, final LivingEntity aix, final Level bhr) {
        this(ais, aix.x, aix.y + aix.getEyeHeight() - 0.10000000149011612, aix.z, bhr);
        this.owner = aix;
        this.ownerId = aix.getUUID();
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
    
    public void shootFromRotation(final Entity aio, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final float float7 = -Mth.sin(float3 * 0.017453292f) * Mth.cos(float2 * 0.017453292f);
        final float float8 = -Mth.sin((float2 + float4) * 0.017453292f);
        final float float9 = Mth.cos(float3 * 0.017453292f) * Mth.cos(float2 * 0.017453292f);
        this.shoot(float7, float8, float9, float5, float6);
        final Vec3 csi11 = aio.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(csi11.x, aio.onGround ? 0.0 : csi11.y, csi11.z));
    }
    
    @Override
    public void shoot(final double double1, final double double2, final double double3, final float float4, final float float5) {
        final Vec3 csi10 = new Vec3(double1, double2, double3).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * float5, this.random.nextGaussian() * 0.007499999832361937 * float5, this.random.nextGaussian() * 0.007499999832361937 * float5).scale(float4);
        this.setDeltaMovement(csi10);
        final float float6 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi10));
        this.yRot = (float)(Mth.atan2(csi10.x, csi10.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi10.y, float6) * 57.2957763671875);
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }
    
    @Override
    public void lerpMotion(final double double1, final double double2, final double double3) {
        this.setDeltaMovement(double1, double2, double3);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float float8 = Mth.sqrt(double1 * double1 + double3 * double3);
            this.yRot = (float)(Mth.atan2(double1, double3) * 57.2957763671875);
            this.xRot = (float)(Mth.atan2(double2, float8) * 57.2957763671875);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }
    }
    
    @Override
    public void tick() {
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        super.tick();
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }
        if (this.inGround) {
            this.inGround = false;
            this.setDeltaMovement(this.getDeltaMovement().multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        }
        final AABB csc2 = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0);
        for (final Entity aio4 : this.level.getEntities(this, csc2, (aio -> !aio.isSpectator() && aio.isPickable()))) {
            if (aio4 == this.entityToIgnore) {
                ++this.timeToIgnore;
                break;
            }
            if (this.owner != null && this.tickCount < 2 && this.entityToIgnore == null) {
                this.entityToIgnore = aio4;
                this.timeToIgnore = 3;
                break;
            }
        }
        final HitResult csf3 = ProjectileUtil.getHitResult(this, csc2, (Predicate<Entity>)(aio -> !aio.isSpectator() && aio.isPickable() && aio != this.entityToIgnore), ClipContext.Block.OUTLINE, true);
        if (this.entityToIgnore != null && this.timeToIgnore-- <= 0) {
            this.entityToIgnore = null;
        }
        if (csf3.getType() != HitResult.Type.MISS) {
            if (csf3.getType() == HitResult.Type.BLOCK && this.level.getBlockState(((BlockHitResult)csf3).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
                this.handleInsidePortal(((BlockHitResult)csf3).getBlockPos());
            }
            else {
                this.onHit(csf3);
            }
        }
        final Vec3 csi4 = this.getDeltaMovement();
        this.x += csi4.x;
        this.y += csi4.y;
        this.z += csi4.z;
        final float float5 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi4));
        this.yRot = (float)(Mth.atan2(csi4.x, csi4.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi4.y, float5) * 57.2957763671875);
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
        float float7;
        if (this.isInWater()) {
            for (int integer7 = 0; integer7 < 4; ++integer7) {
                final float float6 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, this.x - csi4.x * 0.25, this.y - csi4.y * 0.25, this.z - csi4.z * 0.25, csi4.x, csi4.y, csi4.z);
            }
            float7 = 0.8f;
        }
        else {
            float7 = 0.99f;
        }
        this.setDeltaMovement(csi4.scale(float7));
        if (!this.isNoGravity()) {
            final Vec3 csi5 = this.getDeltaMovement();
            this.setDeltaMovement(csi5.x, csi5.y - this.getGravity(), csi5.z);
        }
        this.setPos(this.x, this.y, this.z);
    }
    
    protected float getGravity() {
        return 0.03f;
    }
    
    protected abstract void onHit(final HitResult csf);
    
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("xTile", this.xBlock);
        id.putInt("yTile", this.yBlock);
        id.putInt("zTile", this.zBlock);
        id.putByte("shake", (byte)this.shakeTime);
        id.putByte("inGround", (byte)(byte)(this.inGround ? 1 : 0));
        if (this.ownerId != null) {
            id.put("owner", (Tag)NbtUtils.createUUIDTag(this.ownerId));
        }
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        this.xBlock = id.getInt("xTile");
        this.yBlock = id.getInt("yTile");
        this.zBlock = id.getInt("zTile");
        this.shakeTime = (id.getByte("shake") & 0xFF);
        this.inGround = (id.getByte("inGround") == 1);
        this.owner = null;
        if (id.contains("owner", 10)) {
            this.ownerId = NbtUtils.loadUUIDTag(id.getCompound("owner"));
        }
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerId != null && this.level instanceof ServerLevel) {
            final Entity aio2 = ((ServerLevel)this.level).getEntity(this.ownerId);
            if (aio2 instanceof LivingEntity) {
                this.owner = (LivingEntity)aio2;
            }
            else {
                this.ownerId = null;
            }
        }
        return this.owner;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
