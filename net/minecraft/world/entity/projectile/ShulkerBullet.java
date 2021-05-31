package net.minecraft.world.entity.projectile;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Iterator;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.Difficulty;
import java.util.List;
import net.minecraft.util.Mth;
import com.google.common.collect.Lists;
import net.minecraft.core.Position;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class ShulkerBullet extends Entity {
    private LivingEntity owner;
    private Entity finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID ownerId;
    private BlockPos lastKnownOwnerPos;
    @Nullable
    private UUID targetId;
    private BlockPos lastKnownTargetPos;
    
    public ShulkerBullet(final EntityType<? extends ShulkerBullet> ais, final Level bhr) {
        super(ais, bhr);
        this.noPhysics = true;
    }
    
    public ShulkerBullet(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        this(EntityType.SHULKER_BULLET, bhr);
        this.moveTo(double2, double3, double4, this.yRot, this.xRot);
        this.setDeltaMovement(double5, double6, double7);
    }
    
    public ShulkerBullet(final Level bhr, final LivingEntity aix, final Entity aio, final Direction.Axis a) {
        this(EntityType.SHULKER_BULLET, bhr);
        this.owner = aix;
        final BlockPos ew6 = new BlockPos(aix);
        final double double7 = ew6.getX() + 0.5;
        final double double8 = ew6.getY() + 0.5;
        final double double9 = ew6.getZ() + 0.5;
        this.moveTo(double7, double8, double9, this.yRot, this.xRot);
        this.finalTarget = aio;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(a);
    }
    
    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        if (this.owner != null) {
            final BlockPos ew3 = new BlockPos(this.owner);
            final CompoundTag id2 = NbtUtils.createUUIDTag(this.owner.getUUID());
            id2.putInt("X", ew3.getX());
            id2.putInt("Y", ew3.getY());
            id2.putInt("Z", ew3.getZ());
            id.put("Owner", (Tag)id2);
        }
        if (this.finalTarget != null) {
            final BlockPos ew3 = new BlockPos(this.finalTarget);
            final CompoundTag id2 = NbtUtils.createUUIDTag(this.finalTarget.getUUID());
            id2.putInt("X", ew3.getX());
            id2.putInt("Y", ew3.getY());
            id2.putInt("Z", ew3.getZ());
            id.put("Target", (Tag)id2);
        }
        if (this.currentMoveDirection != null) {
            id.putInt("Dir", this.currentMoveDirection.get3DDataValue());
        }
        id.putInt("Steps", this.flightSteps);
        id.putDouble("TXD", this.targetDeltaX);
        id.putDouble("TYD", this.targetDeltaY);
        id.putDouble("TZD", this.targetDeltaZ);
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        this.flightSteps = id.getInt("Steps");
        this.targetDeltaX = id.getDouble("TXD");
        this.targetDeltaY = id.getDouble("TYD");
        this.targetDeltaZ = id.getDouble("TZD");
        if (id.contains("Dir", 99)) {
            this.currentMoveDirection = Direction.from3DDataValue(id.getInt("Dir"));
        }
        if (id.contains("Owner", 10)) {
            final CompoundTag id2 = id.getCompound("Owner");
            this.ownerId = NbtUtils.loadUUIDTag(id2);
            this.lastKnownOwnerPos = new BlockPos(id2.getInt("X"), id2.getInt("Y"), id2.getInt("Z"));
        }
        if (id.contains("Target", 10)) {
            final CompoundTag id2 = id.getCompound("Target");
            this.targetId = NbtUtils.loadUUIDTag(id2);
            this.lastKnownTargetPos = new BlockPos(id2.getInt("X"), id2.getInt("Y"), id2.getInt("Z"));
        }
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    private void setMoveDirection(@Nullable final Direction fb) {
        this.currentMoveDirection = fb;
    }
    
    private void selectNextMoveDirection(@Nullable final Direction.Axis a) {
        double double4 = 0.5;
        BlockPos ew3;
        if (this.finalTarget == null) {
            ew3 = new BlockPos(this).below();
        }
        else {
            double4 = this.finalTarget.getBbHeight() * 0.5;
            ew3 = new BlockPos(this.finalTarget.x, this.finalTarget.y + double4, this.finalTarget.z);
        }
        double double5 = ew3.getX() + 0.5;
        double double6 = ew3.getY() + double4;
        double double7 = ew3.getZ() + 0.5;
        Direction fb12 = null;
        if (!ew3.closerThan(this.position(), 2.0)) {
            final BlockPos ew4 = new BlockPos(this);
            final List<Direction> list14 = (List<Direction>)Lists.newArrayList();
            if (a != Direction.Axis.X) {
                if (ew4.getX() < ew3.getX() && this.level.isEmptyBlock(ew4.east())) {
                    list14.add(Direction.EAST);
                }
                else if (ew4.getX() > ew3.getX() && this.level.isEmptyBlock(ew4.west())) {
                    list14.add(Direction.WEST);
                }
            }
            if (a != Direction.Axis.Y) {
                if (ew4.getY() < ew3.getY() && this.level.isEmptyBlock(ew4.above())) {
                    list14.add(Direction.UP);
                }
                else if (ew4.getY() > ew3.getY() && this.level.isEmptyBlock(ew4.below())) {
                    list14.add(Direction.DOWN);
                }
            }
            if (a != Direction.Axis.Z) {
                if (ew4.getZ() < ew3.getZ() && this.level.isEmptyBlock(ew4.south())) {
                    list14.add(Direction.SOUTH);
                }
                else if (ew4.getZ() > ew3.getZ() && this.level.isEmptyBlock(ew4.north())) {
                    list14.add(Direction.NORTH);
                }
            }
            fb12 = Direction.getRandomFace(this.random);
            if (list14.isEmpty()) {
                for (int integer15 = 5; !this.level.isEmptyBlock(ew4.relative(fb12)) && integer15 > 0; fb12 = Direction.getRandomFace(this.random), --integer15) {}
            }
            else {
                fb12 = (Direction)list14.get(this.random.nextInt(list14.size()));
            }
            double5 = this.x + fb12.getStepX();
            double6 = this.y + fb12.getStepY();
            double7 = this.z + fb12.getStepZ();
        }
        this.setMoveDirection(fb12);
        final double double8 = double5 - this.x;
        final double double9 = double6 - this.y;
        final double double10 = double7 - this.z;
        final double double11 = Mth.sqrt(double8 * double8 + double9 * double9 + double10 * double10);
        if (double11 == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        }
        else {
            this.targetDeltaX = double8 / double11 * 0.15;
            this.targetDeltaY = double9 / double11 * 0.15;
            this.targetDeltaZ = double10 / double11 * 0.15;
        }
        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }
    
    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
            return;
        }
        super.tick();
        if (!this.level.isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                final List<LivingEntity> list2 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, new AABB(this.lastKnownTargetPos.offset(-2, -2, -2), this.lastKnownTargetPos.offset(2, 2, 2)));
                for (final LivingEntity aix4 : list2) {
                    if (aix4.getUUID().equals(this.targetId)) {
                        this.finalTarget = aix4;
                        break;
                    }
                }
                this.targetId = null;
            }
            if (this.owner == null && this.ownerId != null) {
                final List<LivingEntity> list2 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, new AABB(this.lastKnownOwnerPos.offset(-2, -2, -2), this.lastKnownOwnerPos.offset(2, 2, 2)));
                for (final LivingEntity aix4 : list2) {
                    if (aix4.getUUID().equals(this.ownerId)) {
                        this.owner = aix4;
                        break;
                    }
                }
                this.ownerId = null;
            }
            if (this.finalTarget != null && this.finalTarget.isAlive() && (!(this.finalTarget instanceof Player) || !((Player)this.finalTarget).isSpectator())) {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
                final Vec3 csi2 = this.getDeltaMovement();
                this.setDeltaMovement(csi2.add((this.targetDeltaX - csi2.x) * 0.2, (this.targetDeltaY - csi2.y) * 0.2, (this.targetDeltaZ - csi2.z) * 0.2));
            }
            else if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }
            final HitResult csf2 = ProjectileUtil.forwardsRaycast(this, true, false, this.owner, ClipContext.Block.COLLIDER);
            if (csf2.getType() != HitResult.Type.MISS) {
                this.onHit(csf2);
            }
        }
        final Vec3 csi2 = this.getDeltaMovement();
        this.setPos(this.x + csi2.x, this.y + csi2.y, this.z + csi2.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.END_ROD, this.x - csi2.x, this.y - csi2.y + 0.15, this.z - csi2.z, 0.0, 0.0, 0.0);
        }
        else if (this.finalTarget != null && !this.finalTarget.removed) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection((this.currentMoveDirection == null) ? null : this.currentMoveDirection.getAxis());
                }
            }
            if (this.currentMoveDirection != null) {
                final BlockPos ew3 = new BlockPos(this);
                final Direction.Axis a4 = this.currentMoveDirection.getAxis();
                if (this.level.loadedAndEntityCanStandOn(ew3.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection(a4);
                }
                else {
                    final BlockPos ew4 = new BlockPos(this.finalTarget);
                    if ((a4 == Direction.Axis.X && ew3.getX() == ew4.getX()) || (a4 == Direction.Axis.Z && ew3.getZ() == ew4.getZ()) || (a4 == Direction.Axis.Y && ew3.getY() == ew4.getY())) {
                        this.selectNextMoveDirection(a4);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isOnFire() {
        return false;
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        return double1 < 16384.0;
    }
    
    @Override
    public float getBrightness() {
        return 1.0f;
    }
    
    @Override
    public int getLightColor() {
        return 15728880;
    }
    
    protected void onHit(final HitResult csf) {
        if (csf.getType() == HitResult.Type.ENTITY) {
            final Entity aio3 = ((EntityHitResult)csf).getEntity();
            final boolean boolean4 = aio3.hurt(DamageSource.indirectMobAttack(this, this.owner).setProjectile(), 4.0f);
            if (boolean4) {
                this.doEnchantDamageEffects(this.owner, aio3);
                if (aio3 instanceof LivingEntity) {
                    ((LivingEntity)aio3).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200));
                }
            }
        }
        else {
            ((ServerLevel)this.level).<SimpleParticleType>sendParticles(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 2, 0.2, 0.2, 0.2, 0.0);
            this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f);
        }
        this.remove();
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerLevel)this.level).<SimpleParticleType>sendParticles(ParticleTypes.CRIT, this.x, this.y, this.z, 15, 0.2, 0.2, 0.2, 0.0);
            this.remove();
        }
        return true;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
