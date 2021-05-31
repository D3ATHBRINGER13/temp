package net.minecraft.world.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.Tag;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.function.Predicate;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.MoverType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.OptionalInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class FireworkRocketEntity extends Entity implements ItemSupplier, Projectile {
    private static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;
    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET;
    private static final EntityDataAccessor<Boolean> DATA_SHOT_AT_ANGLE;
    private int life;
    private int lifetime;
    private LivingEntity attachedToEntity;
    
    public FireworkRocketEntity(final EntityType<? extends FireworkRocketEntity> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<ItemStack>define(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
        this.entityData.<OptionalInt>define(FireworkRocketEntity.DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
        this.entityData.<Boolean>define(FireworkRocketEntity.DATA_SHOT_AT_ANGLE, false);
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        return double1 < 4096.0 && !this.isAttachedToEntity();
    }
    
    @Override
    public boolean shouldRender(final double double1, final double double2, final double double3) {
        return super.shouldRender(double1, double2, double3) && !this.isAttachedToEntity();
    }
    
    public FireworkRocketEntity(final Level bhr, final double double2, final double double3, final double double4, final ItemStack bcj) {
        super(EntityType.FIREWORK_ROCKET, bhr);
        this.life = 0;
        this.setPos(double2, double3, double4);
        int integer10 = 1;
        if (!bcj.isEmpty() && bcj.hasTag()) {
            this.entityData.<ItemStack>set(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM, bcj.copy());
            integer10 += bcj.getOrCreateTagElement("Fireworks").getByte("Flight");
        }
        this.setDeltaMovement(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
        this.lifetime = 10 * integer10 + this.random.nextInt(6) + this.random.nextInt(7);
    }
    
    public FireworkRocketEntity(final Level bhr, final ItemStack bcj, final LivingEntity aix) {
        this(bhr, aix.x, aix.y, aix.z, bcj);
        this.entityData.<OptionalInt>set(FireworkRocketEntity.DATA_ATTACHED_TO_TARGET, OptionalInt.of(aix.getId()));
        this.attachedToEntity = aix;
    }
    
    public FireworkRocketEntity(final Level bhr, final ItemStack bcj, final double double3, final double double4, final double double5, final boolean boolean6) {
        this(bhr, double3, double4, double5, bcj);
        this.entityData.<Boolean>set(FireworkRocketEntity.DATA_SHOT_AT_ANGLE, boolean6);
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
        if (this.isAttachedToEntity()) {
            if (this.attachedToEntity == null) {
                this.entityData.<OptionalInt>get(FireworkRocketEntity.DATA_ATTACHED_TO_TARGET).ifPresent(integer -> {
                    final Entity aio3 = this.level.getEntity(integer);
                    if (aio3 instanceof LivingEntity) {
                        this.attachedToEntity = (LivingEntity)aio3;
                    }
                });
            }
            if (this.attachedToEntity != null) {
                if (this.attachedToEntity.isFallFlying()) {
                    final Vec3 csi2 = this.attachedToEntity.getLookAngle();
                    final double double3 = 1.5;
                    final double double4 = 0.1;
                    final Vec3 csi3 = this.attachedToEntity.getDeltaMovement();
                    this.attachedToEntity.setDeltaMovement(csi3.add(csi2.x * 0.1 + (csi2.x * 1.5 - csi3.x) * 0.5, csi2.y * 0.1 + (csi2.y * 1.5 - csi3.y) * 0.5, csi2.z * 0.1 + (csi2.z * 1.5 - csi3.z) * 0.5));
                }
                this.setPos(this.attachedToEntity.x, this.attachedToEntity.y, this.attachedToEntity.z);
                this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
            }
        }
        else {
            if (!this.isShotAtAngle()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        final Vec3 csi2 = this.getDeltaMovement();
        final HitResult csf3 = ProjectileUtil.getHitResult(this, this.getBoundingBox().expandTowards(csi2).inflate(1.0), (Predicate<Entity>)(aio -> !aio.isSpectator() && aio.isAlive() && aio.isPickable()), ClipContext.Block.COLLIDER, true);
        if (!this.noPhysics) {
            this.performHitChecks(csf3);
            this.hasImpulse = true;
        }
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
        if (this.life == 0 && !this.isSilent()) {
            this.level.playSound(null, this.x, this.y, this.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0f, 1.0f);
        }
        ++this.life;
        if (this.level.isClientSide && this.life % 2 < 2) {
            this.level.addParticle(ParticleTypes.FIREWORK, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.getDeltaMovement().y * 0.5, this.random.nextGaussian() * 0.05);
        }
        if (!this.level.isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }
    
    private void explode() {
        this.level.broadcastEntityEvent(this, (byte)17);
        this.dealExplosionDamage();
        this.remove();
    }
    
    protected void performHitChecks(final HitResult csf) {
        if (csf.getType() == HitResult.Type.ENTITY && !this.level.isClientSide) {
            this.explode();
        }
        else if (this.collision) {
            BlockPos ew3;
            if (csf.getType() == HitResult.Type.BLOCK) {
                ew3 = new BlockPos(((BlockHitResult)csf).getBlockPos());
            }
            else {
                ew3 = new BlockPos(this);
            }
            this.level.getBlockState(ew3).entityInside(this.level, ew3, this);
            if (this.hasExplosion()) {
                this.explode();
            }
        }
    }
    
    private boolean hasExplosion() {
        final ItemStack bcj2 = this.entityData.<ItemStack>get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
        final CompoundTag id3 = bcj2.isEmpty() ? null : bcj2.getTagElement("Fireworks");
        final ListTag ik4 = (id3 != null) ? id3.getList("Explosions", 10) : null;
        return ik4 != null && !ik4.isEmpty();
    }
    
    private void dealExplosionDamage() {
        float float2 = 0.0f;
        final ItemStack bcj3 = this.entityData.<ItemStack>get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
        final CompoundTag id4 = bcj3.isEmpty() ? null : bcj3.getTagElement("Fireworks");
        final ListTag ik5 = (id4 != null) ? id4.getList("Explosions", 10) : null;
        if (ik5 != null && !ik5.isEmpty()) {
            float2 = 5.0f + ik5.size() * 2;
        }
        if (float2 > 0.0f) {
            if (this.attachedToEntity != null) {
                this.attachedToEntity.hurt(DamageSource.FIREWORKS, 5.0f + ik5.size() * 2);
            }
            final double double6 = 5.0;
            final Vec3 csi8 = new Vec3(this.x, this.y, this.z);
            final List<LivingEntity> list9 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, this.getBoundingBox().inflate(5.0));
            for (final LivingEntity aix11 : list9) {
                if (aix11 == this.attachedToEntity) {
                    continue;
                }
                if (this.distanceToSqr(aix11) > 25.0) {
                    continue;
                }
                boolean boolean12 = false;
                for (int integer13 = 0; integer13 < 2; ++integer13) {
                    final Vec3 csi9 = new Vec3(aix11.x, aix11.y + aix11.getBbHeight() * 0.5 * integer13, aix11.z);
                    final HitResult csf15 = this.level.clip(new ClipContext(csi8, csi9, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (csf15.getType() == HitResult.Type.MISS) {
                        boolean12 = true;
                        break;
                    }
                }
                if (!boolean12) {
                    continue;
                }
                final float float3 = float2 * (float)Math.sqrt((5.0 - this.distanceTo(aix11)) / 5.0);
                aix11.hurt(DamageSource.FIREWORKS, float3);
            }
        }
    }
    
    private boolean isAttachedToEntity() {
        return this.entityData.<OptionalInt>get(FireworkRocketEntity.DATA_ATTACHED_TO_TARGET).isPresent();
    }
    
    public boolean isShotAtAngle() {
        return this.entityData.<Boolean>get(FireworkRocketEntity.DATA_SHOT_AT_ANGLE);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 17 && this.level.isClientSide) {
            if (!this.hasExplosion()) {
                for (int integer3 = 0; integer3 < this.random.nextInt(3) + 2; ++integer3) {
                    this.level.addParticle(ParticleTypes.POOF, this.x, this.y, this.z, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
                }
            }
            else {
                final ItemStack bcj3 = this.entityData.<ItemStack>get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
                final CompoundTag id4 = bcj3.isEmpty() ? null : bcj3.getTagElement("Fireworks");
                final Vec3 csi5 = this.getDeltaMovement();
                this.level.createFireworks(this.x, this.y, this.z, csi5.x, csi5.y, csi5.z, id4);
            }
        }
        super.handleEntityEvent(byte1);
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("Life", this.life);
        id.putInt("LifeTime", this.lifetime);
        final ItemStack bcj3 = this.entityData.<ItemStack>get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
        if (!bcj3.isEmpty()) {
            id.put("FireworksItem", (Tag)bcj3.save(new CompoundTag()));
        }
        id.putBoolean("ShotAtAngle", (boolean)this.entityData.<Boolean>get(FireworkRocketEntity.DATA_SHOT_AT_ANGLE));
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        this.life = id.getInt("Life");
        this.lifetime = id.getInt("LifeTime");
        final ItemStack bcj3 = ItemStack.of(id.getCompound("FireworksItem"));
        if (!bcj3.isEmpty()) {
            this.entityData.<ItemStack>set(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM, bcj3);
        }
        if (id.contains("ShotAtAngle")) {
            this.entityData.<Boolean>set(FireworkRocketEntity.DATA_SHOT_AT_ANGLE, id.getBoolean("ShotAtAngle"));
        }
    }
    
    @Override
    public ItemStack getItem() {
        final ItemStack bcj2 = this.entityData.<ItemStack>get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
        return bcj2.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : bcj2;
    }
    
    @Override
    public boolean isAttackable() {
        return false;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    @Override
    public void shoot(double double1, double double2, double double3, final float float4, final float float5) {
        final float float6 = Mth.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
        double1 /= float6;
        double2 /= float6;
        double3 /= float6;
        double1 += this.random.nextGaussian() * 0.007499999832361937 * float5;
        double2 += this.random.nextGaussian() * 0.007499999832361937 * float5;
        double3 += this.random.nextGaussian() * 0.007499999832361937 * float5;
        double1 *= float4;
        double2 *= float4;
        double3 *= float4;
        this.setDeltaMovement(double1, double2, double3);
    }
    
    static {
        DATA_ID_FIREWORKS_ITEM = SynchedEntityData.<ItemStack>defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
        DATA_ATTACHED_TO_TARGET = SynchedEntityData.<OptionalInt>defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
        DATA_SHOT_AT_ANGLE = SynchedEntityData.<Boolean>defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
    }
}
