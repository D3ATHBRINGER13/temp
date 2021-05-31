package net.minecraft.world.entity.fishing;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.function.Predicate;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class FishingHook extends Entity {
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY;
    private boolean inGround;
    private int life;
    private final Player owner;
    private int flightTime;
    private int nibble;
    private int timeUntilLured;
    private int timeUntilHooked;
    private float fishAngle;
    public Entity hookedIn;
    private FishHookState currentState;
    private final int luck;
    private final int lureSpeed;
    
    private FishingHook(final Level bhr, final Player awg, final int integer3, final int integer4) {
        super(EntityType.FISHING_BOBBER, bhr);
        this.currentState = FishHookState.FLYING;
        this.noCulling = true;
        this.owner = awg;
        this.owner.fishing = this;
        this.luck = Math.max(0, integer3);
        this.lureSpeed = Math.max(0, integer4);
    }
    
    public FishingHook(final Level bhr, final Player awg, final double double3, final double double4, final double double5) {
        this(bhr, awg, 0, 0);
        this.setPos(double3, double4, double5);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }
    
    public FishingHook(final Player awg, final Level bhr, final int integer3, final int integer4) {
        this(bhr, awg, integer3, integer4);
        final float float6 = this.owner.xRot;
        final float float7 = this.owner.yRot;
        final float float8 = Mth.cos(-float7 * 0.017453292f - 3.1415927f);
        final float float9 = Mth.sin(-float7 * 0.017453292f - 3.1415927f);
        final float float10 = -Mth.cos(-float6 * 0.017453292f);
        final float float11 = Mth.sin(-float6 * 0.017453292f);
        final double double12 = this.owner.x - float9 * 0.3;
        final double double13 = this.owner.y + this.owner.getEyeHeight();
        final double double14 = this.owner.z - float8 * 0.3;
        this.moveTo(double12, double13, double14, float7, float6);
        Vec3 csi18 = new Vec3(-float9, Mth.clamp(-(float11 / float10), -5.0f, 5.0f), -float8);
        final double double15 = csi18.length();
        csi18 = csi18.multiply(0.6 / double15 + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / double15 + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / double15 + 0.5 + this.random.nextGaussian() * 0.0045);
        this.setDeltaMovement(csi18);
        this.yRot = (float)(Mth.atan2(csi18.x, csi18.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi18.y, Mth.sqrt(Entity.getHorizontalDistanceSqr(csi18))) * 57.2957763671875);
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<Integer>define(FishingHook.DATA_HOOKED_ENTITY, 0);
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (FishingHook.DATA_HOOKED_ENTITY.equals(qk)) {
            final int integer3 = this.getEntityData().<Integer>get(FishingHook.DATA_HOOKED_ENTITY);
            this.hookedIn = ((integer3 > 0) ? this.level.getEntity(integer3 - 1) : null);
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        final double double2 = 64.0;
        return double1 < 4096.0;
    }
    
    @Override
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.owner == null) {
            this.remove();
            return;
        }
        if (!this.level.isClientSide && this.shouldStopFishing()) {
            return;
        }
        if (this.inGround) {
            ++this.life;
            if (this.life >= 1200) {
                this.remove();
                return;
            }
        }
        float float2 = 0.0f;
        final BlockPos ew3 = new BlockPos(this);
        final FluidState clk4 = this.level.getFluidState(ew3);
        if (clk4.is(FluidTags.WATER)) {
            float2 = clk4.getHeight(this.level, ew3);
        }
        if (this.currentState == FishHookState.FLYING) {
            if (this.hookedIn != null) {
                this.setDeltaMovement(Vec3.ZERO);
                this.currentState = FishHookState.HOOKED_IN_ENTITY;
                return;
            }
            if (float2 > 0.0f) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
                this.currentState = FishHookState.BOBBING;
                return;
            }
            if (!this.level.isClientSide) {
                this.checkCollision();
            }
            if (this.inGround || this.onGround || this.horizontalCollision) {
                this.flightTime = 0;
                this.setDeltaMovement(Vec3.ZERO);
            }
            else {
                ++this.flightTime;
            }
        }
        else {
            if (this.currentState == FishHookState.HOOKED_IN_ENTITY) {
                if (this.hookedIn != null) {
                    if (this.hookedIn.removed) {
                        this.hookedIn = null;
                        this.currentState = FishHookState.FLYING;
                    }
                    else {
                        this.x = this.hookedIn.x;
                        this.y = this.hookedIn.getBoundingBox().minY + this.hookedIn.getBbHeight() * 0.8;
                        this.z = this.hookedIn.z;
                        this.setPos(this.x, this.y, this.z);
                    }
                }
                return;
            }
            if (this.currentState == FishHookState.BOBBING) {
                final Vec3 csi5 = this.getDeltaMovement();
                double double6 = this.y + csi5.y - ew3.getY() - float2;
                if (Math.abs(double6) < 0.01) {
                    double6 += Math.signum(double6) * 0.1;
                }
                this.setDeltaMovement(csi5.x * 0.9, csi5.y - double6 * this.random.nextFloat() * 0.2, csi5.z * 0.9);
                if (!this.level.isClientSide && float2 > 0.0f) {
                    this.catchingFish(ew3);
                }
            }
        }
        if (!clk4.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.updateRotation();
        final double double7 = 0.92;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
        this.setPos(this.x, this.y, this.z);
    }
    
    private boolean shouldStopFishing() {
        final ItemStack bcj2 = this.owner.getMainHandItem();
        final ItemStack bcj3 = this.owner.getOffhandItem();
        final boolean boolean4 = bcj2.getItem() == Items.FISHING_ROD;
        final boolean boolean5 = bcj3.getItem() == Items.FISHING_ROD;
        if (this.owner.removed || !this.owner.isAlive() || (!boolean4 && !boolean5) || this.distanceToSqr(this.owner) > 1024.0) {
            this.remove();
            return true;
        }
        return false;
    }
    
    private void updateRotation() {
        final Vec3 csi2 = this.getDeltaMovement();
        final float float3 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi2));
        this.yRot = (float)(Mth.atan2(csi2.x, csi2.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi2.y, float3) * 57.2957763671875);
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
    }
    
    private void checkCollision() {
        final HitResult csf2 = ProjectileUtil.getHitResult(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), (Predicate<Entity>)(aio -> !aio.isSpectator() && (aio.isPickable() || aio instanceof ItemEntity) && (aio != this.owner || this.flightTime >= 5)), ClipContext.Block.COLLIDER, true);
        if (csf2.getType() != HitResult.Type.MISS) {
            if (csf2.getType() == HitResult.Type.ENTITY) {
                this.hookedIn = ((EntityHitResult)csf2).getEntity();
                this.setHookedEntity();
            }
            else {
                this.inGround = true;
            }
        }
    }
    
    private void setHookedEntity() {
        this.getEntityData().<Integer>set(FishingHook.DATA_HOOKED_ENTITY, this.hookedIn.getId() + 1);
    }
    
    private void catchingFish(final BlockPos ew) {
        final ServerLevel vk3 = (ServerLevel)this.level;
        int integer4 = 1;
        final BlockPos ew2 = ew.above();
        if (this.random.nextFloat() < 0.25f && this.level.isRainingAt(ew2)) {
            ++integer4;
        }
        if (this.random.nextFloat() < 0.5f && !this.level.canSeeSky(ew2)) {
            --integer4;
        }
        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
            }
            else {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.2 * this.random.nextFloat() * this.random.nextFloat(), 0.0));
            }
        }
        else if (this.timeUntilHooked > 0) {
            this.timeUntilHooked -= integer4;
            if (this.timeUntilHooked > 0) {
                this.fishAngle += (float)(this.random.nextGaussian() * 4.0);
                final float float6 = this.fishAngle * 0.017453292f;
                final float float7 = Mth.sin(float6);
                final float float8 = Mth.cos(float6);
                final double double9 = this.x + float7 * this.timeUntilHooked * 0.1f;
                final double double10 = Mth.floor(this.getBoundingBox().minY) + 1.0f;
                final double double11 = this.z + float8 * this.timeUntilHooked * 0.1f;
                final Block bmv15 = vk3.getBlockState(new BlockPos(double9, double10 - 1.0, double11)).getBlock();
                if (bmv15 == Blocks.WATER) {
                    if (this.random.nextFloat() < 0.15f) {
                        vk3.<SimpleParticleType>sendParticles(ParticleTypes.BUBBLE, double9, double10 - 0.10000000149011612, double11, 1, float7, 0.1, float8, 0.0);
                    }
                    final float float9 = float7 * 0.04f;
                    final float float10 = float8 * 0.04f;
                    vk3.<SimpleParticleType>sendParticles(ParticleTypes.FISHING, double9, double10, double11, 0, float10, 0.01, -float9, 1.0);
                    vk3.<SimpleParticleType>sendParticles(ParticleTypes.FISHING, double9, double10, double11, 0, -float10, 0.01, float9, 1.0);
                }
            }
            else {
                final Vec3 csi6 = this.getDeltaMovement();
                this.setDeltaMovement(csi6.x, -0.4f * Mth.nextFloat(this.random, 0.6f, 1.0f), csi6.z);
                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                final double double12 = this.getBoundingBox().minY + 0.5;
                vk3.<SimpleParticleType>sendParticles(ParticleTypes.BUBBLE, this.x, double12, this.z, (int)(1.0f + this.getBbWidth() * 20.0f), this.getBbWidth(), 0.0, this.getBbWidth(), 0.20000000298023224);
                vk3.<SimpleParticleType>sendParticles(ParticleTypes.FISHING, this.x, double12, this.z, (int)(1.0f + this.getBbWidth() * 20.0f), this.getBbWidth(), 0.0, this.getBbWidth(), 0.20000000298023224);
                this.nibble = Mth.nextInt(this.random, 20, 40);
            }
        }
        else if (this.timeUntilLured > 0) {
            this.timeUntilLured -= integer4;
            float float6 = 0.15f;
            if (this.timeUntilLured < 20) {
                float6 += (float)((20 - this.timeUntilLured) * 0.05);
            }
            else if (this.timeUntilLured < 40) {
                float6 += (float)((40 - this.timeUntilLured) * 0.02);
            }
            else if (this.timeUntilLured < 60) {
                float6 += (float)((60 - this.timeUntilLured) * 0.01);
            }
            if (this.random.nextFloat() < float6) {
                final float float7 = Mth.nextFloat(this.random, 0.0f, 360.0f) * 0.017453292f;
                final float float8 = Mth.nextFloat(this.random, 25.0f, 60.0f);
                final double double9 = this.x + Mth.sin(float7) * float8 * 0.1f;
                final double double10 = Mth.floor(this.getBoundingBox().minY) + 1.0f;
                final double double11 = this.z + Mth.cos(float7) * float8 * 0.1f;
                final Block bmv15 = vk3.getBlockState(new BlockPos(double9, double10 - 1.0, double11)).getBlock();
                if (bmv15 == Blocks.WATER) {
                    vk3.<SimpleParticleType>sendParticles(ParticleTypes.SPLASH, double9, double10, double11, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
                }
            }
            if (this.timeUntilLured <= 0) {
                this.fishAngle = Mth.nextFloat(this.random, 0.0f, 360.0f);
                this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
            }
        }
        else {
            this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
            this.timeUntilLured -= this.lureSpeed * 20 * 5;
        }
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
    }
    
    public int retrieve(final ItemStack bcj) {
        if (this.level.isClientSide || this.owner == null) {
            return 0;
        }
        int integer3 = 0;
        if (this.hookedIn != null) {
            this.bringInHookedEntity();
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)this.owner, bcj, this, (Collection<ItemStack>)Collections.emptyList());
            this.level.broadcastEntityEvent(this, (byte)31);
            integer3 = ((this.hookedIn instanceof ItemEntity) ? 3 : 5);
        }
        else if (this.nibble > 0) {
            final LootContext.Builder a4 = new LootContext.Builder((ServerLevel)this.level).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(this)).<ItemStack>withParameter(LootContextParams.TOOL, bcj).withRandom(this.random).withLuck(this.luck + this.owner.getLuck());
            final LootTable cpb5 = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
            final List<ItemStack> list6 = cpb5.getRandomItems(a4.create(LootContextParamSets.FISHING));
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)this.owner, bcj, this, (Collection<ItemStack>)list6);
            for (final ItemStack bcj2 : list6) {
                final ItemEntity atx9 = new ItemEntity(this.level, this.x, this.y, this.z, bcj2);
                final double double10 = this.owner.x - this.x;
                final double double11 = this.owner.y - this.y;
                final double double12 = this.owner.z - this.z;
                final double double13 = 0.1;
                atx9.setDeltaMovement(double10 * 0.1, double11 * 0.1 + Math.sqrt(Math.sqrt(double10 * double10 + double11 * double11 + double12 * double12)) * 0.08, double12 * 0.1);
                this.level.addFreshEntity(atx9);
                this.owner.level.addFreshEntity(new ExperienceOrb(this.owner.level, this.owner.x, this.owner.y + 0.5, this.owner.z + 0.5, this.random.nextInt(6) + 1));
                if (bcj2.getItem().is(ItemTags.FISHES)) {
                    this.owner.awardStat(Stats.FISH_CAUGHT, 1);
                }
            }
            integer3 = 1;
        }
        if (this.inGround) {
            integer3 = 2;
        }
        this.remove();
        return integer3;
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 31 && this.level.isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer()) {
            this.bringInHookedEntity();
        }
        super.handleEntityEvent(byte1);
    }
    
    protected void bringInHookedEntity() {
        if (this.owner == null) {
            return;
        }
        final Vec3 csi2 = new Vec3(this.owner.x - this.x, this.owner.y - this.y, this.owner.z - this.z).scale(0.1);
        this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(csi2));
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    public void remove() {
        super.remove();
        if (this.owner != null) {
            this.owner.fishing = null;
        }
    }
    
    @Nullable
    public Player getOwner() {
        return this.owner;
    }
    
    @Override
    public boolean canChangeDimensions() {
        return false;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        final Entity aio2 = this.getOwner();
        return new ClientboundAddEntityPacket(this, (aio2 == null) ? this.getId() : aio2.getId());
    }
    
    static {
        DATA_HOOKED_ENTITY = SynchedEntityData.<Integer>defineId(FishingHook.class, EntityDataSerializers.INT);
    }
    
    enum FishHookState {
        FLYING, 
        HOOKED_IN_ENTITY, 
        BOBBING;
    }
}
