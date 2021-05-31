package net.minecraft.world.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Predicate;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.damagesource.DamageSource;
import com.google.common.collect.Lists;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Iterator;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import java.util.List;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.sounds.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public abstract class AbstractArrow extends Entity implements Projectile {
    private static final EntityDataAccessor<Byte> ID_FLAGS;
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL;
    @Nullable
    private BlockState lastState;
    protected boolean inGround;
    protected int inGroundTime;
    public Pickup pickup;
    public int shakeTime;
    public UUID ownerUUID;
    private int life;
    private int flightTime;
    private double baseDamage;
    private int knockback;
    private SoundEvent soundEvent;
    private IntOpenHashSet piercingIgnoreEntityIds;
    private List<Entity> piercedAndKilledEntities;
    
    protected AbstractArrow(final EntityType<? extends AbstractArrow> ais, final Level bhr) {
        super(ais, bhr);
        this.pickup = Pickup.DISALLOWED;
        this.baseDamage = 2.0;
        this.soundEvent = this.getDefaultHitGroundSoundEvent();
    }
    
    protected AbstractArrow(final EntityType<? extends AbstractArrow> ais, final double double2, final double double3, final double double4, final Level bhr) {
        this(ais, bhr);
        this.setPos(double2, double3, double4);
    }
    
    protected AbstractArrow(final EntityType<? extends AbstractArrow> ais, final LivingEntity aix, final Level bhr) {
        this(ais, aix.x, aix.y + aix.getEyeHeight() - 0.10000000149011612, aix.z, bhr);
        this.setOwner(aix);
        if (aix instanceof Player) {
            this.pickup = Pickup.ALLOWED;
        }
    }
    
    public void setSoundEvent(final SoundEvent yo) {
        this.soundEvent = yo;
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(double2)) {
            double2 = 1.0;
        }
        double2 *= 64.0 * getViewScale();
        return double1 < double2 * double2;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<Byte>define(AbstractArrow.ID_FLAGS, (Byte)0);
        this.entityData.<Optional<UUID>>define(AbstractArrow.DATA_OWNERUUID_ID, (Optional<UUID>)Optional.empty());
        this.entityData.<Byte>define(AbstractArrow.PIERCE_LEVEL, (Byte)0);
    }
    
    public void shootFromRotation(final Entity aio, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final float float7 = -Mth.sin(float3 * 0.017453292f) * Mth.cos(float2 * 0.017453292f);
        final float float8 = -Mth.sin(float2 * 0.017453292f);
        final float float9 = Mth.cos(float3 * 0.017453292f) * Mth.cos(float2 * 0.017453292f);
        this.shoot(float7, float8, float9, float5, float6);
        this.setDeltaMovement(this.getDeltaMovement().add(aio.getDeltaMovement().x, aio.onGround ? 0.0 : aio.getDeltaMovement().y, aio.getDeltaMovement().z));
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
        this.life = 0;
    }
    
    @Override
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
        this.setPos(double1, double2, double3);
        this.setRot(float4, float5);
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
            this.life = 0;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        final boolean boolean2 = this.isNoPhysics();
        Vec3 csi3 = this.getDeltaMovement();
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float float4 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi3));
            this.yRot = (float)(Mth.atan2(csi3.x, csi3.z) * 57.2957763671875);
            this.xRot = (float)(Mth.atan2(csi3.y, float4) * 57.2957763671875);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }
        final BlockPos ew4 = new BlockPos(this.x, this.y, this.z);
        final BlockState bvt5 = this.level.getBlockState(ew4);
        if (!bvt5.isAir() && !boolean2) {
            final VoxelShape ctc6 = bvt5.getCollisionShape(this.level, ew4);
            if (!ctc6.isEmpty()) {
                for (final AABB csc8 : ctc6.toAabbs()) {
                    if (csc8.move(ew4).contains(new Vec3(this.x, this.y, this.z))) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }
        if (this.isInWaterOrRain()) {
            this.clearFire();
        }
        if (this.inGround && !boolean2) {
            if (this.lastState != bvt5 && this.level.noCollision(this.getBoundingBox().inflate(0.06))) {
                this.inGround = false;
                this.setDeltaMovement(csi3.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
                this.life = 0;
                this.flightTime = 0;
            }
            else if (!this.level.isClientSide) {
                this.checkDespawn();
            }
            ++this.inGroundTime;
            return;
        }
        this.inGroundTime = 0;
        ++this.flightTime;
        final Vec3 csi4 = new Vec3(this.x, this.y, this.z);
        Vec3 csi5 = csi4.add(csi3);
        HitResult csf8 = this.level.clip(new ClipContext(csi4, csi5, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (csf8.getType() != HitResult.Type.MISS) {
            csi5 = csf8.getLocation();
        }
        while (!this.removed) {
            EntityHitResult cse9 = this.findHitEntity(csi4, csi5);
            if (cse9 != null) {
                csf8 = cse9;
            }
            if (csf8 != null && csf8.getType() == HitResult.Type.ENTITY) {
                final Entity aio10 = ((EntityHitResult)csf8).getEntity();
                final Entity aio11 = this.getOwner();
                if (aio10 instanceof Player && aio11 instanceof Player && !((Player)aio11).canHarmPlayer((Player)aio10)) {
                    csf8 = null;
                    cse9 = null;
                }
            }
            if (csf8 != null && !boolean2) {
                this.onHit(csf8);
                this.hasImpulse = true;
            }
            if (cse9 == null) {
                break;
            }
            if (this.getPierceLevel() <= 0) {
                break;
            }
            csf8 = null;
        }
        csi3 = this.getDeltaMovement();
        final double double9 = csi3.x;
        final double double10 = csi3.y;
        final double double11 = csi3.z;
        if (this.isCritArrow()) {
            for (int integer15 = 0; integer15 < 4; ++integer15) {
                this.level.addParticle(ParticleTypes.CRIT, this.x + double9 * integer15 / 4.0, this.y + double10 * integer15 / 4.0, this.z + double11 * integer15 / 4.0, -double9, -double10 + 0.2, -double11);
            }
        }
        this.x += double9;
        this.y += double10;
        this.z += double11;
        final float float5 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi3));
        if (boolean2) {
            this.yRot = (float)(Mth.atan2(-double9, -double11) * 57.2957763671875);
        }
        else {
            this.yRot = (float)(Mth.atan2(double9, double11) * 57.2957763671875);
        }
        this.xRot = (float)(Mth.atan2(double10, float5) * 57.2957763671875);
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
        float float6 = 0.99f;
        final float float7 = 0.05f;
        if (this.isInWater()) {
            for (int integer16 = 0; integer16 < 4; ++integer16) {
                final float float8 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, this.x - double9 * 0.25, this.y - double10 * 0.25, this.z - double11 * 0.25, double9, double10, double11);
            }
            float6 = this.getWaterInertia();
        }
        this.setDeltaMovement(csi3.scale(float6));
        if (!this.isNoGravity() && !boolean2) {
            final Vec3 csi6 = this.getDeltaMovement();
            this.setDeltaMovement(csi6.x, csi6.y - 0.05000000074505806, csi6.z);
        }
        this.setPos(this.x, this.y, this.z);
        this.checkInsideBlocks();
    }
    
    protected void checkDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.remove();
        }
    }
    
    protected void onHit(final HitResult csf) {
        final HitResult.Type a3 = csf.getType();
        if (a3 == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)csf);
        }
        else if (a3 == HitResult.Type.BLOCK) {
            final BlockHitResult csd4 = (BlockHitResult)csf;
            final BlockState bvt5 = this.level.getBlockState(csd4.getBlockPos());
            this.lastState = bvt5;
            final Vec3 csi6 = csd4.getLocation().subtract(this.x, this.y, this.z);
            this.setDeltaMovement(csi6);
            final Vec3 csi7 = csi6.normalize().scale(0.05000000074505806);
            this.x -= csi7.x;
            this.y -= csi7.y;
            this.z -= csi7.z;
            this.playSound(this.getHitGroundSoundEvent(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            this.inGround = true;
            this.shakeTime = 7;
            this.setCritArrow(false);
            this.setPierceLevel((byte)0);
            this.setSoundEvent(SoundEvents.ARROW_HIT);
            this.setShotFromCrossbow(false);
            this.resetPiercedEntities();
            bvt5.onProjectileHit(this.level, bvt5, csd4, this);
        }
    }
    
    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }
        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }
    
    protected void onHitEntity(final EntityHitResult cse) {
        final Entity aio3 = cse.getEntity();
        final float float4 = (float)this.getDeltaMovement().length();
        int integer5 = Mth.ceil(Math.max(float4 * this.baseDamage, 0.0));
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }
            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = (List<Entity>)Lists.newArrayListWithCapacity(5);
            }
            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.remove();
                return;
            }
            this.piercingIgnoreEntityIds.add(aio3.getId());
        }
        if (this.isCritArrow()) {
            integer5 += this.random.nextInt(integer5 / 2 + 2);
        }
        final Entity aio4 = this.getOwner();
        DamageSource ahx6;
        if (aio4 == null) {
            ahx6 = DamageSource.arrow(this, this);
        }
        else {
            ahx6 = DamageSource.arrow(this, aio4);
            if (aio4 instanceof LivingEntity) {
                ((LivingEntity)aio4).setLastHurtMob(aio3);
            }
        }
        final int integer6 = aio3.getRemainingFireTicks();
        if (this.isOnFire() && !(aio3 instanceof EnderMan)) {
            aio3.setSecondsOnFire(5);
        }
        if (aio3.hurt(ahx6, (float)integer5)) {
            if (aio3 instanceof LivingEntity) {
                final LivingEntity aix9 = (LivingEntity)aio3;
                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    aix9.setArrowCount(aix9.getArrowCount() + 1);
                }
                if (this.knockback > 0) {
                    final Vec3 csi10 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(this.knockback * 0.6);
                    if (csi10.lengthSqr() > 0.0) {
                        aix9.push(csi10.x, 0.1, csi10.z);
                    }
                }
                if (!this.level.isClientSide && aio4 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(aix9, aio4);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)aio4, aix9);
                }
                this.doPostHurtEffects(aix9);
                if (aio4 != null && aix9 != aio4 && aix9 instanceof Player && aio4 instanceof ServerPlayer) {
                    ((ServerPlayer)aio4).connection.send(new ClientboundGameEventPacket(6, 0.0f));
                }
                if (!aio3.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(aix9);
                }
                if (!this.level.isClientSide && aio4 instanceof ServerPlayer) {
                    final ServerPlayer vl10 = (ServerPlayer)aio4;
                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(vl10, (Collection<Entity>)this.piercedAndKilledEntities, this.piercedAndKilledEntities.size());
                    }
                    else if (!aio3.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(vl10, (Collection<Entity>)Arrays.asList((Object[])new Entity[] { aio3 }), 0);
                    }
                }
            }
            this.playSound(this.soundEvent, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0 && !(aio3 instanceof EnderMan)) {
                this.remove();
            }
        }
        else {
            aio3.setRemainingFireTicks(integer6);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
            this.yRot += 180.0f;
            this.yRotO += 180.0f;
            this.flightTime = 0;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                if (this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.remove();
            }
        }
    }
    
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }
    
    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }
    
    protected void doPostHurtEffects(final LivingEntity aix) {
    }
    
    @Nullable
    protected EntityHitResult findHitEntity(final Vec3 csi1, final Vec3 csi2) {
        return ProjectileUtil.getHitResult(this.level, this, csi1, csi2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), (Predicate<Entity>)(aio -> !aio.isSpectator() && aio.isAlive() && aio.isPickable() && (aio != this.getOwner() || this.flightTime >= 5) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(aio.getId()))));
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putShort("life", (short)this.life);
        if (this.lastState != null) {
            id.put("inBlockState", (Tag)NbtUtils.writeBlockState(this.lastState));
        }
        id.putByte("shake", (byte)this.shakeTime);
        id.putByte("inGround", (byte)(byte)(this.inGround ? 1 : 0));
        id.putByte("pickup", (byte)this.pickup.ordinal());
        id.putDouble("damage", this.baseDamage);
        id.putBoolean("crit", this.isCritArrow());
        id.putByte("PierceLevel", this.getPierceLevel());
        if (this.ownerUUID != null) {
            id.putUUID("OwnerUUID", this.ownerUUID);
        }
        id.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
        id.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        this.life = id.getShort("life");
        if (id.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(id.getCompound("inBlockState"));
        }
        this.shakeTime = (id.getByte("shake") & 0xFF);
        this.inGround = (id.getByte("inGround") == 1);
        if (id.contains("damage", 99)) {
            this.baseDamage = id.getDouble("damage");
        }
        if (id.contains("pickup", 99)) {
            this.pickup = Pickup.byOrdinal(id.getByte("pickup"));
        }
        else if (id.contains("player", 99)) {
            this.pickup = (id.getBoolean("player") ? Pickup.ALLOWED : Pickup.DISALLOWED);
        }
        this.setCritArrow(id.getBoolean("crit"));
        this.setPierceLevel(id.getByte("PierceLevel"));
        if (id.hasUUID("OwnerUUID")) {
            this.ownerUUID = id.getUUID("OwnerUUID");
        }
        if (id.contains("SoundEvent", 8)) {
            this.soundEvent = (SoundEvent)Registry.SOUND_EVENT.getOptional(new ResourceLocation(id.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
        }
        this.setShotFromCrossbow(id.getBoolean("ShotFromCrossbow"));
    }
    
    public void setOwner(@Nullable final Entity aio) {
        this.ownerUUID = ((aio == null) ? null : aio.getUUID());
        if (aio instanceof Player) {
            this.pickup = (((Player)aio).abilities.instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED);
        }
    }
    
    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            return ((ServerLevel)this.level).getEntity(this.ownerUUID);
        }
        return null;
    }
    
    @Override
    public void playerTouch(final Player awg) {
        if (this.level.isClientSide || (!this.inGround && !this.isNoPhysics()) || this.shakeTime > 0) {
            return;
        }
        boolean boolean3 = this.pickup == Pickup.ALLOWED || (this.pickup == Pickup.CREATIVE_ONLY && awg.abilities.instabuild) || (this.isNoPhysics() && this.getOwner().getUUID() == awg.getUUID());
        if (this.pickup == Pickup.ALLOWED && !awg.inventory.add(this.getPickupItem())) {
            boolean3 = false;
        }
        if (boolean3) {
            awg.take(this, 1);
            this.remove();
        }
    }
    
    protected abstract ItemStack getPickupItem();
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    public void setBaseDamage(final double double1) {
        this.baseDamage = double1;
    }
    
    public double getBaseDamage() {
        return this.baseDamage;
    }
    
    public void setKnockback(final int integer) {
        this.knockback = integer;
    }
    
    @Override
    public boolean isAttackable() {
        return false;
    }
    
    @Override
    protected float getEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.0f;
    }
    
    public void setCritArrow(final boolean boolean1) {
        this.setFlag(1, boolean1);
    }
    
    public void setPierceLevel(final byte byte1) {
        this.entityData.<Byte>set(AbstractArrow.PIERCE_LEVEL, byte1);
    }
    
    private void setFlag(final int integer, final boolean boolean2) {
        final byte byte4 = this.entityData.<Byte>get(AbstractArrow.ID_FLAGS);
        if (boolean2) {
            this.entityData.<Byte>set(AbstractArrow.ID_FLAGS, (byte)(byte4 | integer));
        }
        else {
            this.entityData.<Byte>set(AbstractArrow.ID_FLAGS, (byte)(byte4 & ~integer));
        }
    }
    
    public boolean isCritArrow() {
        final byte byte2 = this.entityData.<Byte>get(AbstractArrow.ID_FLAGS);
        return (byte2 & 0x1) != 0x0;
    }
    
    public boolean shotFromCrossbow() {
        final byte byte2 = this.entityData.<Byte>get(AbstractArrow.ID_FLAGS);
        return (byte2 & 0x4) != 0x0;
    }
    
    public byte getPierceLevel() {
        return this.entityData.<Byte>get(AbstractArrow.PIERCE_LEVEL);
    }
    
    public void setEnchantmentEffectsFromEntity(final LivingEntity aix, final float float2) {
        final int integer4 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, aix);
        final int integer5 = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, aix);
        this.setBaseDamage(float2 * 2.0f + (this.random.nextGaussian() * 0.25 + this.level.getDifficulty().getId() * 0.11f));
        if (integer4 > 0) {
            this.setBaseDamage(this.getBaseDamage() + integer4 * 0.5 + 0.5);
        }
        if (integer5 > 0) {
            this.setKnockback(integer5);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, aix) > 0) {
            this.setSecondsOnFire(100);
        }
    }
    
    protected float getWaterInertia() {
        return 0.6f;
    }
    
    public void setNoPhysics(final boolean boolean1) {
        this.setFlag(2, this.noPhysics = boolean1);
    }
    
    public boolean isNoPhysics() {
        if (!this.level.isClientSide) {
            return this.noPhysics;
        }
        return (this.entityData.<Byte>get(AbstractArrow.ID_FLAGS) & 0x2) != 0x0;
    }
    
    public void setShotFromCrossbow(final boolean boolean1) {
        this.setFlag(4, boolean1);
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        final Entity aio2 = this.getOwner();
        return new ClientboundAddEntityPacket(this, (aio2 == null) ? 0 : aio2.getId());
    }
    
    static {
        ID_FLAGS = SynchedEntityData.<Byte>defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
        DATA_OWNERUUID_ID = SynchedEntityData.<Optional<UUID>>defineId(AbstractArrow.class, EntityDataSerializers.OPTIONAL_UUID);
        PIERCE_LEVEL = SynchedEntityData.<Byte>defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    }
    
    public enum Pickup {
        DISALLOWED, 
        ALLOWED, 
        CREATIVE_ONLY;
        
        public static Pickup byOrdinal(int integer) {
            if (integer < 0 || integer > values().length) {
                integer = 0;
            }
            return values()[integer];
        }
    }
}
